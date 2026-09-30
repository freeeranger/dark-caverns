package com.freeranger.dark_caverns.generation;

import com.freeranger.dark_caverns.registry.CustomBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/** Short, grounded connections between otherwise separate local walking areas. */
public final class CavernRouteFeature extends Feature<NoneFeatureConfiguration> {
    private static final int CHUNK_WIDTH = 16;
    private static final int LAYER_SIZE = CHUNK_WIDTH * CHUNK_WIDTH;
    private static final int MIN_ROUTE_Y = 24;
    private static final int MAX_ROUTE_Y = 232; // Exclusive.
    // Include the floor-support and headroom halo used by edit validation.
    private static final int MIN_SNAPSHOT_Y = MIN_ROUTE_Y - 7;
    private static final int MAX_SNAPSHOT_Y = MAX_ROUTE_Y + 5; // Inclusive.
    private static final int SNAPSHOT_SIZE = LAYER_SIZE * (MAX_SNAPSHOT_Y - MIN_SNAPSHOT_Y + 1);
    private static final int MAX_EXPANSIONS = 4096;
    private static final int MAX_COST = 56;
    private static final int MAX_PLANS = 24;
    private static final int MIN_COMPONENT_SIZE = 8;
    private static final int MIN_PATH_LENGTH = 3;
    private static final int MAX_PATH_LENGTH = 18;
    private static final int MAX_EDIT_BLOCKS = 192;
    private static final int MAX_SUPPORT_DEPTH = 4;
    private static final byte PROTECTED = 0;
    private static final byte AIR = 1;
    private static final byte NATURAL = 2;
    // A packed position is a layer offset followed by local Z and X, in that order.
    private static final int[] HORIZONTAL_OFFSETS = {-1, 1, -CHUNK_WIDTH, CHUNK_WIDTH};

    public CavernRouteFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        return connect(
                        context.level(),
                        new ChunkPos(context.origin()),
                        context.random().nextInt(8) == 0)
                > 0;
    }

    /**
     * Snapshot terrain, identify walking areas, search for a connection, then validate and commit
     * an edit. All reads and writes stay in the owning chunk.
     */
    public static int connect(WorldGenLevel level, ChunkPos chunk, boolean bridges) {
        Grid grid = new Grid(level, chunk);
        WalkingAreas areas = new WalkingAreas(grid);
        // A route needs two eligible owners. Skipping search here cannot change its result.
        if (areas.eligibleCount() < 2) return 0;
        return new RouteSearch(grid, areas, bridges).connect();
    }

    /** Union-find labels for the existing, grounded walking positions. */
    private static final class WalkingAreas {
        private final int[] owners = new int[SNAPSHOT_SIZE];
        private final int[] sizes = new int[SNAPSHOT_SIZE];

        WalkingAreas(Grid grid) {
            Arrays.fill(owners, -1);
            findWalkingPositions(grid);
            mergeAdjacentAreas(grid);
        }

        private void findWalkingPositions(Grid grid) {
            for (int y = MIN_ROUTE_Y; y < MAX_ROUTE_Y; y++) {
                for (int z = 1; z < CHUNK_WIDTH - 1; z++) {
                    for (int x = 1; x < CHUNK_WIDTH - 1; x++) {
                        int position = index(x, y, z);
                        if (grid.isWalkingPosition(position)) {
                            owners[position] = position;
                            sizes[position] = 1;
                        }
                    }
                }
            }
        }

        private void mergeAdjacentAreas(Grid grid) {
            // Preserve traversal and union order: root IDs also break search ties.
            for (int y = MIN_ROUTE_Y; y < MAX_ROUTE_Y; y++) {
                for (int z = 1; z < CHUNK_WIDTH - 1; z++) {
                    for (int x = 1; x < CHUNK_WIDTH - 1; x++) {
                        int position = index(x, y, z);
                        if (owners[position] < 0) continue;
                        for (int offset : HORIZONTAL_OFFSETS) {
                            for (int stepY = -1; stepY <= 1; stepY++) {
                                int neighbor = position + offset + stepY * LAYER_SIZE;
                                if (!insideRouteBounds(neighbor)
                                        || owners[neighbor] < 0
                                        || !grid.canStep(position, neighbor)) continue;
                                int currentRoot = root(position);
                                int neighborRoot = root(neighbor);
                                if (currentRoot != neighborRoot) {
                                    owners[neighborRoot] = currentRoot;
                                    sizes[currentRoot] += sizes[neighborRoot];
                                }
                            }
                        }
                    }
                }
            }
        }

        int eligibleCount() {
            int count = 0;
            for (int position = 0; position < owners.length; position++) {
                if (owners[position] == position && sizes[position] >= MIN_COMPONENT_SIZE) count++;
            }
            return count;
        }

        void resolveOwners() {
            // Resolve every chain before discarding roots for undersized components.
            for (int position = 0; position < owners.length; position++) {
                if (owners[position] >= 0) owners[position] = root(position);
            }
            for (int position = 0; position < owners.length; position++) {
                int owner = owners[position];
                if (owner >= 0 && sizes[owner] < MIN_COMPONENT_SIZE) owners[position] = -1;
            }
        }

        private int root(int position) {
            while (owners[position] != position) {
                owners[position] = owners[owners[position]];
                position = owners[position];
            }
            return position;
        }
    }

    /** Bounded multi-source search, with one frontier for all eligible walking areas. */
    private static final class RouteSearch {
        private final Grid grid;
        private final boolean bridges;
        private final int[] owners;
        private final int[] parents = new int[SNAPSHOT_SIZE];
        private final int[] costs;
        private final PriorityQueue<Node> frontier =
                new PriorityQueue<>(
                        Comparator.comparingInt(Node::cost).thenComparingInt(Node::position));
        private final HashSet<Long> triedEdges = new HashSet<>();

        RouteSearch(Grid grid, WalkingAreas areas, boolean bridges) {
            this.grid = grid;
            this.bridges = bridges;
            areas.resolveOwners();
            // Component labels become search owners; sizes are no longer needed, so reuse their
            // storage for costs. Both arrays remain local to this invocation.
            owners = areas.owners;
            costs = areas.sizes;
            Arrays.fill(parents, -1);
            Arrays.fill(costs, Integer.MAX_VALUE);
            for (int position = 0; position < owners.length; position++) {
                if (owners[position] >= 0) {
                    costs[position] = 0;
                    frontier.add(new Node(position, 0));
                }
            }
        }

        int connect() {
            int expanded = 0;
            int attemptedPlans = 0;
            while (!frontier.isEmpty() && expanded < MAX_EXPANSIONS && attemptedPlans < MAX_PLANS) {
                Node node = frontier.remove();
                int position = node.position();
                if (node.cost() != costs[position]) continue;
                expanded++;
                for (int offset : HORIZONTAL_OFFSETS) {
                    for (int stepY = -1; stepY <= 1; stepY++) {
                        int neighbor = position + offset + stepY * LAYER_SIZE;
                        if (!insideRouteBounds(neighbor)) continue;
                        int entryCost = grid.entryCost(neighbor, bridges);
                        if (entryCost < 0) continue;
                        int nextCost = costs[position] + entryCost;
                        if (nextCost > MAX_COST) continue;
                        if (meetsAnotherArea(position, neighbor, nextCost)
                                && triedEdges.add(edgeKey(position, neighbor))) {
                            List<Integer> path = joinedPath(position, neighbor);
                            if (path.size() >= MIN_PATH_LENGTH && path.size() <= MAX_PATH_LENGTH) {
                                attemptedPlans++;
                                int changed = grid.place(path, bridges);
                                if (changed > 0) return changed;
                            }
                        }
                        if (nextCost < costs[neighbor]) {
                            costs[neighbor] = nextCost;
                            owners[neighbor] = owners[position];
                            parents[neighbor] = position;
                            frontier.add(new Node(neighbor, nextCost));
                        }
                    }
                }
            }
            return 0;
        }

        private boolean meetsAnotherArea(int position, int neighbor, int nextCost) {
            return owners[neighbor] >= 0
                    && owners[neighbor] != owners[position]
                    && nextCost + costs[neighbor] <= MAX_COST;
        }

        private List<Integer> joinedPath(int from, int to) {
            var path = new ArrayList<Integer>();
            for (int position = from; position >= 0; position = parents[position])
                path.add(position);
            Collections.reverse(path);
            for (int position = to; position >= 0; position = parents[position]) path.add(position);
            return path;
        }

        private static long edgeKey(int from, int to) {
            return ((long) Math.min(from, to) << 32) | Math.max(from, to);
        }
    }

    private record Node(int position, int cost) {}

    private static int index(int x, int y, int z) {
        return (y - MIN_SNAPSHOT_Y) * LAYER_SIZE + z * CHUNK_WIDTH + x;
    }

    private static int height(int position) {
        return position / LAYER_SIZE + MIN_SNAPSHOT_Y;
    }

    private static int localX(int position) {
        return position & (CHUNK_WIDTH - 1);
    }

    private static int localZ(int position) {
        return (position / CHUNK_WIDTH) & (CHUNK_WIDTH - 1);
    }

    private static int column(int position) {
        return position & (LAYER_SIZE - 1);
    }

    private static boolean insideRouteBounds(int position) {
        return position >= index(0, MIN_ROUTE_Y, 0)
                && position < index(0, MAX_ROUTE_Y, 0)
                && localX(position) >= 1
                && localX(position) < CHUNK_WIDTH - 1
                && localZ(position) >= 1
                && localZ(position) < CHUNK_WIDTH - 1;
    }

    private static boolean natural(BlockState state) {
        return state.is(CustomBlocks.CARFSTONE.get())
                || state.is(CustomBlocks.MOLTEN_CARFSTONE.get())
                || state.is(CustomBlocks.ASHY_MOLTEN_CARFSTONE.get())
                || state.is(CustomBlocks.GLIMMERGRASS_BLOCK.get())
                || state.is(CustomBlocks.OVERGROWN_CARFSTONE.get())
                || state.is(Blocks.MUD);
    }

    /** Immutable block states plus working classifications for this chunk's terrain snapshot. */
    private static final class Grid {
        private final WorldGenLevel level;
        private final ChunkPos chunk;
        private final BlockState[] states = new BlockState[SNAPSHOT_SIZE];
        private final byte[] kinds = new byte[SNAPSHOT_SIZE];

        Grid(WorldGenLevel level, ChunkPos chunk) {
            this.level = level;
            this.chunk = chunk;
            var mutable = new BlockPos.MutableBlockPos();
            for (int y = MIN_SNAPSHOT_Y; y <= MAX_SNAPSHOT_Y; y++) {
                for (int z = 0; z < CHUNK_WIDTH; z++) {
                    for (int x = 0; x < CHUNK_WIDTH; x++) {
                        int position = index(x, y, z);
                        BlockState state =
                                level.getBlockState(
                                        mutable.set(
                                                chunk.getMinBlockX() + x,
                                                y,
                                                chunk.getMinBlockZ() + z));
                        states[position] = state;
                        kinds[position] = classify(state);
                    }
                }
            }
        }

        private static byte classify(BlockState state) {
            if (state.isAir()) return AIR;
            return natural(state) && state.getFluidState().isEmpty() ? NATURAL : PROTECTED;
        }

        boolean isWalkingPosition(int position) {
            return kinds[position] == AIR
                    && kinds[position + LAYER_SIZE] == AIR
                    && kinds[position - LAYER_SIZE] == NATURAL;
        }

        boolean canStep(int from, int to) {
            if (height(from) == height(to)) return true;
            int lower = Math.min(from, to);
            return kinds[lower + 2 * LAYER_SIZE] == AIR;
        }

        /** Positive means solid footing within reach; zero means a gap; -1 means blocked. */
        int supportDepth(int position) {
            for (int depth = 1; depth <= MAX_SUPPORT_DEPTH; depth++) {
                int support = position - depth * LAYER_SIZE;
                if (kinds[support] == NATURAL) {
                    return kinds[support - LAYER_SIZE] == NATURAL ? depth : -1;
                }
                if (kinds[support] != AIR) return -1;
            }
            return 0;
        }

        int entryCost(int position, boolean bridges) {
            if (kinds[position] == PROTECTED
                    || kinds[position + LAYER_SIZE] == PROTECTED
                    || kinds[position + 2 * LAYER_SIZE] == PROTECTED
                    || kinds[position - LAYER_SIZE] == PROTECTED) return -1;
            int support = supportDepth(position);
            if (support < 0 || (support == 0 && !bridges)) return -1;
            int excavation =
                    (kinds[position] == NATURAL ? 4 : 0)
                            + (kinds[position + LAYER_SIZE] == NATURAL ? 4 : 0)
                            + (kinds[position + 2 * LAYER_SIZE] == NATURAL ? 4 : 0);
            return 1 + excavation + (support == 0 ? 12 : (support - 1) * 3);
        }

        int place(List<Integer> path, boolean bridges) {
            Map<Integer, Integer> columns = treadColumns(path, bridges);
            if (columns == null) return 0;
            Map<BlockPos, BlockState> plan = planEdits(path, columns);
            if (plan == null || !canApply(plan) || !preservesWalkingConnections(plan)) return 0;
            plan.forEach((pos, state) -> level.setBlock(pos, state, Block.UPDATE_CLIENTS));
            return plan.size();
        }

        /** Widen a centerline into a tread, keeping centerline heights at bends. */
        private Map<Integer, Integer> treadColumns(List<Integer> path, boolean bridges) {
            int unsupported = 0;
            var columns = new LinkedHashMap<Integer, Integer>();
            for (int position : path) {
                if (supportDepth(position) == 0) unsupported++;
                Integer previousHeight = columns.put(column(position), height(position));
                if (previousHeight != null && previousHeight != height(position)) return null;
            }
            if (unsupported > (bridges ? 3 : 0)) return null;
            for (int position : path) {
                for (int offset : HORIZONTAL_OFFSETS) {
                    int neighbor = position + offset;
                    if (height(neighbor) != height(position)) continue;
                    columns.putIfAbsent(column(neighbor), height(position));
                }
            }
            return columns;
        }

        private Map<BlockPos, BlockState> planEdits(
                List<Integer> path, Map<Integer, Integer> columns) {
            var plan = new LinkedHashMap<BlockPos, BlockState>();
            for (var entry : columns.entrySet()) {
                int position =
                        index(
                                entry.getKey() % CHUNK_WIDTH,
                                entry.getValue(),
                                entry.getKey() / CHUNK_WIDTH);
                int support = supportDepth(position);
                if (support < 0) return null;
                BlockState floor =
                        support > 0
                                ? states[position - support * LAYER_SIZE]
                                : states[path.getFirst() - LAYER_SIZE];
                if (!natural(floor)) return null;
                for (int stepY = -Math.max(1, support - 1); stepY <= 2; stepY++) {
                    int editedPosition = position + stepY * LAYER_SIZE;
                    if (kinds[editedPosition] == PROTECTED) return null;
                    BlockState replacement = stepY < 0 ? floor : Blocks.AIR.defaultBlockState();
                    if (!states[editedPosition].equals(replacement)) {
                        plan.put(worldPosition(editedPosition), replacement);
                    }
                }
            }
            return plan;
        }

        private boolean canApply(Map<BlockPos, BlockState> plan) {
            if (plan.isEmpty() || plan.size() > MAX_EDIT_BLOCKS) return false;
            for (BlockPos pos : plan.keySet()) {
                int x = pos.getX() & (CHUNK_WIDTH - 1);
                int z = pos.getZ() & (CHUNK_WIDTH - 1);
                if (x == 0 || x == CHUNK_WIDTH - 1 || z == 0 || z == CHUNK_WIDTH - 1) return false;
            }
            // Refuse exposed excavation faces at the boundary without reading another chunk.
            for (var entry : plan.entrySet()) {
                if (!entry.getValue().isAir()) continue;
                for (Direction direction : Direction.values()) {
                    BlockPos neighbor = entry.getKey().relative(direction);
                    if (!chunk.equals(new ChunkPos(neighbor))
                            || !level.getFluidState(neighbor).isEmpty()) return false;
                }
            }
            for (BlockPos pos : plan.keySet()) {
                if (!level.ensureCanWrite(pos)) return false;
            }
            return true;
        }

        /** Preserve existing walking components through the boundary around the whole edit. */
        private boolean preservesWalkingConnections(Map<BlockPos, BlockState> plan) {
            WalkBox box = editBounds(plan);
            int[] floodQueue = new int[box.volume()];
            int[] original = box.labels(floodQueue);
            int[] modified;
            // Apply the candidate only to our private classifications. Restore them even if
            // validation fails, so another search candidate sees the same original snapshot.
            // This avoids cloning the entire chunk array for every rejected edit.
            try {
                for (var entry : plan.entrySet()) {
                    kinds[snapshotIndex(entry.getKey())] = entry.getValue().isAir() ? AIR : NATURAL;
                }
                modified = box.labels(floodQueue);
            } finally {
                for (BlockPos pos : plan.keySet()) {
                    int position = snapshotIndex(pos);
                    kinds[position] = classify(states[position]);
                }
            }
            return retainsComponents(box, original, modified);
        }

        private WalkBox editBounds(Map<BlockPos, BlockState> plan) {
            int minX = CHUNK_WIDTH - 1, maxX = 0;
            int minZ = CHUNK_WIDTH - 1, maxZ = 0;
            int minY = MAX_SNAPSHOT_Y, maxY = MIN_SNAPSHOT_Y;
            for (BlockPos pos : plan.keySet()) {
                int position = snapshotIndex(pos);
                minX = Math.min(minX, localX(position));
                maxX = Math.max(maxX, localX(position));
                minZ = Math.min(minZ, localZ(position));
                maxZ = Math.max(maxZ, localZ(position));
                minY = Math.min(minY, pos.getY());
                maxY = Math.max(maxY, pos.getY());
            }
            return new WalkBox(
                    minX - 1,
                    minY - 2,
                    minZ - 1,
                    maxX - minX + 3,
                    maxY - minY + 5,
                    maxZ - minZ + 3);
        }

        private static boolean retainsComponents(WalkBox box, int[] original, int[] modified) {
            int[] mapped = new int[original.length + 1];
            boolean[] existed = new boolean[mapped.length];
            for (int position = 0; position < original.length; position++) {
                int component = original[position];
                if (component == 0) continue;
                existed[component] = true;
                if (modified[position] == 0) {
                    if (box.isBoundary(position)) return false;
                } else {
                    if (mapped[component] != 0 && mapped[component] != modified[position]) {
                        return false;
                    }
                    mapped[component] = modified[position];
                }
            }
            for (int component = 1; component < mapped.length; component++) {
                if (existed[component] && mapped[component] == 0) return false;
            }
            return true;
        }

        /** Local flood-fill volume, including the unchanged perimeter of a candidate edit. */
        private final class WalkBox {
            private final int minX, minY, minZ, width, height, depth;
            private final int layerSize;

            WalkBox(int minX, int minY, int minZ, int width, int height, int depth) {
                this.minX = minX;
                this.minY = minY;
                this.minZ = minZ;
                this.width = width;
                this.height = height;
                this.depth = depth;
                layerSize = width * depth;
            }

            int volume() {
                return layerSize * height;
            }

            private int snapshotPosition(int position) {
                return index(
                        minX + position % width,
                        minY + position / layerSize,
                        minZ + position / width % depth);
            }

            boolean isBoundary(int position) {
                int x = position % width;
                int z = position / width % depth;
                int y = position / layerSize;
                return x == 0
                        || x == width - 1
                        || z == 0
                        || z == depth - 1
                        || y == 0
                        || y == height - 1;
            }

            private boolean walkable(int position) {
                int floor = position - LAYER_SIZE;
                return kinds[position] == AIR
                        && kinds[position + LAYER_SIZE] == AIR
                        && (kinds[floor] == NATURAL
                                || kinds[floor] == PROTECTED
                                        && states[floor].getFluidState().isEmpty());
            }

            int[] labels(int[] queue) {
                int[] labels = new int[volume()];
                int component = 0;
                for (int start = 0; start < labels.length; start++) {
                    if (labels[start] != 0 || !walkable(snapshotPosition(start))) continue;
                    labels[start] = ++component;
                    floodComponent(start, component, labels, queue);
                }
                return labels;
            }

            private void floodComponent(int start, int component, int[] labels, int[] queue) {
                int head = 0, tail = 1;
                queue[0] = start;
                while (head < tail) {
                    int position = queue[head++];
                    int x = position % width;
                    int z = position / width % depth;
                    int y = position / layerSize;
                    for (int direction = 0; direction < 4; direction++) {
                        if (direction == 0 && x == 0
                                || direction == 1 && x == width - 1
                                || direction == 2 && z == 0
                                || direction == 3 && z == depth - 1) continue;
                        int horizontal =
                                position
                                        + switch (direction) {
                                            case 0 -> -1;
                                            case 1 -> 1;
                                            case 2 -> -width;
                                            default -> width;
                                        };
                        for (int stepY = -1; stepY <= 1; stepY++) {
                            if (y + stepY < 0 || y + stepY >= height) continue;
                            int neighbor = horizontal + stepY * layerSize;
                            if (labels[neighbor] != 0 || !walkable(snapshotPosition(neighbor))) {
                                continue;
                            }
                            int lower = snapshotPosition(stepY > 0 ? position : neighbor);
                            if (stepY != 0 && kinds[lower + 2 * LAYER_SIZE] != AIR) continue;
                            labels[neighbor] = component;
                            queue[tail++] = neighbor;
                        }
                    }
                }
            }
        }

        private int snapshotIndex(BlockPos pos) {
            return index(
                    pos.getX() & (CHUNK_WIDTH - 1), pos.getY(), pos.getZ() & (CHUNK_WIDTH - 1));
        }

        private BlockPos worldPosition(int position) {
            return new BlockPos(
                    chunk.getMinBlockX() + localX(position),
                    height(position),
                    chunk.getMinBlockZ() + localZ(position));
        }
    }
}
