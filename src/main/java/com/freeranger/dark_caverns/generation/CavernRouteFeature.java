package com.freeranger.dark_caverns.generation;

import com.freeranger.dark_caverns.registry.CustomBlocks;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
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

/** Short, grounded connections between otherwise separate local walking areas. */
public final class CavernRouteFeature extends Feature<NoneFeatureConfiguration> {
    private static final int LOW = 24, HIGH = 232, SIZE = 16 * 16 * 256;
    private static final int MAX_EXPANSIONS = 4096, MAX_COST = 56, MAX_PLANS = 24;
    private static final int[] SIDES = {-1, 1, -16, 16};

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
     * All reads and writes stay in the owning chunk; neither noise nor neighbor chunks are
     * requested.
     */
    public static int connect(WorldGenLevel level, ChunkPos chunk, boolean bridges) {
        Grid grid = new Grid(level, chunk);
        int[] roots = new int[SIZE];
        Arrays.fill(roots, -1);
        int[] sizes = new int[SIZE];
        for (int y = LOW; y < HIGH; y++)
            for (int z = 1; z < 15; z++)
                for (int x = 1; x < 15; x++) {
                    int p = index(x, y, z);
                    if (grid.walk(p)) {
                        roots[p] = p;
                        sizes[p] = 1;
                    }
                }
        for (int y = LOW; y < HIGH; y++)
            for (int z = 1; z < 15; z++)
                for (int x = 1; x < 15; x++) {
                    int p = index(x, y, z);
                    if (roots[p] < 0) continue;
                    for (int side : SIDES)
                        for (int dy = -1; dy <= 1; dy++) {
                            int q = p + side + dy * 256;
                            if (!inside(q) || roots[q] < 0 || !grid.step(p, q)) continue;
                            int a = root(roots, p), b = root(roots, q);
                            if (a != b) {
                                roots[b] = a;
                                sizes[a] += sizes[b];
                            }
                        }
                }
        int[] owner = new int[SIZE], parent = new int[SIZE], cost = new int[SIZE];
        Arrays.fill(owner, -1);
        Arrays.fill(parent, -1);
        Arrays.fill(cost, Integer.MAX_VALUE);
        var queue =
                new PriorityQueue<Node>(
                        Comparator.comparingInt(Node::cost).thenComparingInt(Node::pos));
        for (int p = 0; p < SIZE; p++)
            if (roots[p] >= 0) {
                int r = root(roots, p);
                if (sizes[r] < 8) continue;
                owner[p] = r;
                cost[p] = 0;
                queue.add(new Node(p, 0));
            }
        int expanded = 0, plans = 0;
        var tried = new java.util.HashSet<Long>();
        while (!queue.isEmpty() && expanded < MAX_EXPANSIONS && plans < MAX_PLANS) {
            Node node = queue.remove();
            int p = node.pos();
            if (node.cost() != cost[p]) continue;
            expanded++;
            for (int side : SIDES)
                for (int dy = -1; dy <= 1; dy++) {
                    int q = p + side + dy * 256;
                    if (!inside(q)) continue;
                    int entry = grid.entry(q, bridges);
                    if (entry < 0) continue;
                    int next = cost[p] + entry;
                    if (next > MAX_COST) continue;
                    if (owner[q] >= 0 && owner[q] != owner[p] && next + cost[q] <= MAX_COST) {
                        long edge = ((long) Math.min(p, q) << 32) | Math.max(p, q);
                        if (tried.add(edge)) {
                            List<Integer> path = path(parent, p, q);
                            if (path.size() >= 3 && path.size() <= 18) {
                                plans++;
                                int written = grid.place(path, bridges);
                                if (written > 0) return written;
                            }
                        }
                    }
                    if (next < cost[q]) {
                        cost[q] = next;
                        owner[q] = owner[p];
                        parent[q] = p;
                        queue.add(new Node(q, next));
                    }
                }
        }
        return 0;
    }

    private record Node(int pos, int cost) {}

    private static List<Integer> path(int[] parent, int a, int b) {
        var path = new ArrayList<Integer>();
        for (int p = a; p >= 0; p = parent[p]) path.add(p);
        java.util.Collections.reverse(path);
        for (int p = b; p >= 0; p = parent[p]) path.add(p);
        return path;
    }

    private static int root(int[] roots, int p) {
        while (roots[p] != p) {
            roots[p] = roots[roots[p]];
            p = roots[p];
        }
        return p;
    }

    private static int index(int x, int y, int z) {
        return y * 256 + z * 16 + x;
    }

    private static boolean inside(int p) {
        return p >= LOW * 256
                && p < HIGH * 256
                && (p & 15) >= 1
                && (p & 15) <= 14
                && ((p >> 4) & 15) >= 1
                && ((p >> 4) & 15) <= 14;
    }

    private static boolean natural(BlockState state) {
        return state.is(CustomBlocks.CARFSTONE.get())
                || state.is(CustomBlocks.MOLTEN_CARFSTONE.get())
                || state.is(CustomBlocks.GLIMMERGRASS_BLOCK.get())
                || state.is(CustomBlocks.OVERGROWN_CARFSTONE.get());
    }

    private static final class Grid {
        final WorldGenLevel level;
        final ChunkPos chunk;
        final BlockState[] states = new BlockState[SIZE];
        final byte[] kind = new byte[SIZE]; // 0 protected, 1 air, 2 natural base stone

        Grid(WorldGenLevel level, ChunkPos chunk) {
            this.level = level;
            this.chunk = chunk;
            var pos = new BlockPos.MutableBlockPos();
            for (int y = LOW - 7; y <= HIGH + 5; y++)
                for (int z = 0; z < 16; z++)
                    for (int x = 0; x < 16; x++) {
                        int p = index(x, y, z);
                        BlockState state =
                                level.getBlockState(
                                        pos.set(
                                                chunk.getMinBlockX() + x,
                                                y,
                                                chunk.getMinBlockZ() + z));
                        states[p] = state;
                        kind[p] =
                                (byte)
                                        (state.isAir()
                                                ? 1
                                                : natural(state) && state.getFluidState().isEmpty()
                                                        ? 2
                                                        : 0);
                    }
        }

        boolean walk(int p) {
            return kind[p] == 1 && kind[p + 256] == 1 && kind[p - 256] == 2;
        }

        boolean step(int a, int b) {
            return b / 256 == a / 256 || (b > a ? kind[a + 512] == 1 : kind[b + 512] == 1);
        }

        int support(int p) {
            for (int d = 1; d <= 4; d++) {
                if (kind[p - d * 256] == 2) return kind[p - (d + 1) * 256] == 2 ? d : -1;
                if (kind[p - d * 256] != 1) return -1;
            }
            return 0;
        }

        int entry(int p, boolean bridges) {
            if (kind[p] == 0 || kind[p + 256] == 0 || kind[p + 512] == 0 || kind[p - 256] == 0)
                return -1;
            int support = support(p);
            if (support < 0 || (support == 0 && !bridges)) return -1;
            int excavation =
                    (kind[p] == 2 ? 4 : 0)
                            + (kind[p + 256] == 2 ? 4 : 0)
                            + (kind[p + 512] == 2 ? 4 : 0);
            return 1 + excavation + (support == 0 ? 12 : (support - 1) * 3);
        }

        int place(List<Integer> path, boolean bridges) {
            int unsupported = 0;
            var columns = new LinkedHashMap<Integer, Integer>();
            for (int p : path) {
                if (support(p) == 0) unsupported++;
                Integer old = columns.put(p & 255, p / 256);
                if (old != null && old != p / 256) return 0;
            }
            if (unsupported > (bridges ? 3 : 0)) return 0;
            // Widen to a natural two/three-block tread. Centerline heights always win at bends.
            for (int p : path)
                for (int side : SIDES) {
                    int q = p + side;
                    if ((q / 256) != (p / 256)) continue;
                    columns.putIfAbsent(q & 255, p / 256);
                }
            var plan = new LinkedHashMap<BlockPos, BlockState>();
            for (var column : columns.entrySet()) {
                int p = column.getValue() * 256 + column.getKey();
                int support = support(p);
                if (support < 0) return 0;
                BlockState floor =
                        support > 0 ? states[p - support * 256] : states[path.getFirst() - 256];
                if (!natural(floor)) return 0;
                for (int dy = -Math.max(1, support - 1); dy <= 2; dy++) {
                    int q = p + dy * 256;
                    if (kind[q] == 0) return 0;
                    BlockPos pos = pos(q);
                    BlockState replacement = dy < 0 ? floor : Blocks.AIR.defaultBlockState();
                    if (!states[q].equals(replacement)) plan.put(pos, replacement);
                }
            }
            if (plan.isEmpty() || plan.size() > 192) return 0;
            for (BlockPos pos : plan.keySet()) {
                int x = pos.getX() & 15, z = pos.getZ() & 15;
                if (x == 0 || x == 15 || z == 0 || z == 15) return 0;
            }
            // Check every exposed excavation face, including at the chunk boundary. Do not read
            // neighboring chunks: a boundary carve is refused unless the face points into this
            // chunk.
            for (var entry : plan.entrySet()) {
                if (!entry.getValue().isAir()) continue;
                for (Direction direction : Direction.values()) {
                    BlockPos neighbor = entry.getKey().relative(direction);
                    if (!chunk.equals(new ChunkPos(neighbor))) return 0;
                    if (!level.getFluidState(neighbor).isEmpty()) return 0;
                }
            }
            for (BlockPos pos : plan.keySet()) if (!level.ensureCanWrite(pos)) return 0;
            if (!preservesWalking(plan)) return 0;
            plan.forEach((pos, state) -> level.setBlock(pos, state, Block.UPDATE_CLIENTS));
            return plan.size();
        }

        /** Preserve connections through the unchanged boundary around the entire edit. */
        boolean preservesWalking(Map<BlockPos, BlockState> plan) {
            int minX = 15, maxX = 0, minZ = 15, maxZ = 0, minY = 255, maxY = 0;
            byte[] after = kind.clone();
            for (var entry : plan.entrySet()) {
                BlockPos pos = entry.getKey();
                int x = pos.getX() & 15, y = pos.getY(), z = pos.getZ() & 15;
                minX = Math.min(minX, x);
                maxX = Math.max(maxX, x);
                minY = Math.min(minY, y);
                maxY = Math.max(maxY, y);
                minZ = Math.min(minZ, z);
                maxZ = Math.max(maxZ, z);
                after[index(x, y, z)] = (byte) (entry.getValue().isAir() ? 1 : 2);
            }
            var box =
                    new WalkBox(
                            minX - 1,
                            minY - 2,
                            minZ - 1,
                            maxX - minX + 3,
                            maxY - minY + 5,
                            maxZ - minZ + 3);
            int[] original = box.labels(kind), modified = box.labels(after);
            int[] mapped = new int[original.length + 1];
            boolean[] existed = new boolean[mapped.length];
            for (int i = 0; i < original.length; i++) {
                int component = original[i];
                if (component == 0) continue;
                existed[component] = true;
                if (modified[i] == 0) {
                    if (box.boundary(i)) return false;
                } else {
                    if (mapped[component] != 0 && mapped[component] != modified[i]) return false;
                    mapped[component] = modified[i];
                }
            }
            for (int i = 1; i < mapped.length; i++) if (existed[i] && mapped[i] == 0) return false;
            return true;
        }

        final class WalkBox {
            final int x0, y0, z0, width, height, depth;

            WalkBox(int x0, int y0, int z0, int width, int height, int depth) {
                this.x0 = x0;
                this.y0 = y0;
                this.z0 = z0;
                this.width = width;
                this.height = height;
                this.depth = depth;
            }

            int global(int p) {
                return index(x0 + p % width, y0 + p / (width * depth), z0 + p / width % depth);
            }

            boolean boundary(int p) {
                int x = p % width, z = p / width % depth, y = p / (width * depth);
                return x == 0
                        || x == width - 1
                        || z == 0
                        || z == depth - 1
                        || y == 0
                        || y == height - 1;
            }

            boolean walk(byte[] blocks, int p) {
                int floor = p - 256;
                return blocks[p] == 1
                        && blocks[p + 256] == 1
                        && (blocks[floor] == 2
                                || blocks[floor] == 0 && states[floor].getFluidState().isEmpty());
            }

            int[] labels(byte[] blocks) {
                int count = width * height * depth;
                int[] labels = new int[count], queue = new int[count];
                int component = 0;
                for (int start = 0; start < count; start++) {
                    if (labels[start] != 0 || !walk(blocks, global(start))) continue;
                    labels[start] = ++component;
                    int head = 0, tail = 1;
                    queue[0] = start;
                    while (head < tail) {
                        int p = queue[head++],
                                x = p % width,
                                z = p / width % depth,
                                y = p / (width * depth);
                        for (int direction = 0; direction < 4; direction++) {
                            if (direction == 0 && x == 0
                                    || direction == 1 && x == width - 1
                                    || direction == 2 && z == 0
                                    || direction == 3 && z == depth - 1) continue;
                            int side =
                                    p
                                            + switch (direction) {
                                                case 0 -> -1;
                                                case 1 -> 1;
                                                case 2 -> -width;
                                                default -> width;
                                            };
                            for (int dy = -1; dy <= 1; dy++) {
                                if (y + dy < 0 || y + dy >= height) continue;
                                int q = side + dy * width * depth;
                                if (labels[q] != 0 || !walk(blocks, global(q))) continue;
                                if (dy > 0 && blocks[global(p) + 512] != 1
                                        || dy < 0 && blocks[global(q) + 512] != 1) continue;
                                labels[q] = component;
                                queue[tail++] = q;
                            }
                        }
                    }
                }
                return labels;
            }
        }

        BlockPos pos(int p) {
            return new BlockPos(
                    chunk.getMinBlockX() + (p & 15),
                    p / 256,
                    chunk.getMinBlockZ() + ((p >> 4) & 15));
        }
    }
}
