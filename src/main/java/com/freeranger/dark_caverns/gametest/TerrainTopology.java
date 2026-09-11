package com.freeranger.dark_caverns.gametest;

import java.util.BitSet;

/** Six-neighbor air connectivity and conservative two-block-high, one-step walking routes. */
final class TerrainTopology {
    private static final int WIDTH = TerrainTestVolume.WIDTH;
    private static final int LAYER = WIDTH * WIDTH;
    int air;
    int largestAir;
    int walkable;
    int largestWalk;
    int routeHeight;
    int floatingStone;

    static TerrainTopology measure(byte[] blocks) {
        var result = new TerrainTopology();
        var seen = new BitSet(blocks.length);
        int[] queue = new int[blocks.length];
        for (int start = 0; start < blocks.length; start++) {
            if (blocks[start] != 0) continue;
            result.air++;
            if (seen.get(start)) continue;
            int size = flood(blocks, start, (byte) 0, seen, queue);
            result.largestAir = Math.max(result.largestAir, size);
        }
        seen.clear();
        for (int start = 0; start < blocks.length; start++) {
            if (blocks[start] != 1 || seen.get(start)) continue;
            int size = flood(blocks, start, (byte) 1, seen, queue);
            boolean attached = false;
            for (int i = 0; i < size && !attached; i++) {
                int pos = queue[i];
                int x = pos % WIDTH;
                int z = pos / WIDTH % WIDTH;
                int y = pos / LAYER;
                attached =
                        x == 0 || x == WIDTH - 1 || z == 0 || z == WIDTH - 1 || y == 0 || y == 255;
            }
            if (!attached) result.floatingStone += size;
        }

        var feet = new BitSet(blocks.length);
        for (int i = LAYER; i < blocks.length - LAYER; i++) {
            if (blocks[i] == 0 && blocks[i + LAYER] == 0 && blocks[i - LAYER] == 1) feet.set(i);
        }
        result.walkable = feet.cardinality();
        seen.clear();
        for (int start = feet.nextSetBit(0); start >= 0; start = feet.nextSetBit(start + 1)) {
            if (seen.get(start)) continue;
            int head = 0;
            int tail = 1;
            int minY = 255;
            int maxY = 0;
            queue[0] = start;
            seen.set(start);
            while (head < tail) {
                int pos = queue[head++];
                int x = pos % WIDTH;
                int z = pos / WIDTH % WIDTH;
                int y = pos / LAYER;
                minY = Math.min(minY, y);
                maxY = Math.max(maxY, y);
                for (int direction = 0; direction < 4; direction++) {
                    if (direction == 0 && x == 0
                            || direction == 1 && x == WIDTH - 1
                            || direction == 2 && z == 0
                            || direction == 3 && z == WIDTH - 1) continue;
                    int side =
                            pos
                                    + switch (direction) {
                                        case 0 -> -1;
                                        case 1 -> 1;
                                        case 2 -> -WIDTH;
                                        default -> WIDTH;
                                    };
                    for (int dy = -1; dy <= 1; dy++) {
                        int next = side + dy * LAYER;
                        if (next < LAYER
                                || next >= blocks.length - LAYER
                                || !feet.get(next)
                                || seen.get(next)) continue;
                        if (dy > 0 && blocks[pos + 2 * LAYER] != 0
                                || dy < 0 && blocks[side + LAYER] != 0) continue;
                        seen.set(next);
                        queue[tail++] = next;
                    }
                }
            }
            result.largestWalk = Math.max(result.largestWalk, tail);
            if (tail >= 128) result.routeHeight = Math.max(result.routeHeight, maxY - minY);
        }
        return result;
    }

    private static int flood(byte[] blocks, int start, byte material, BitSet seen, int[] queue) {
        int head = 0;
        int tail = 1;
        queue[0] = start;
        seen.set(start);
        while (head < tail) {
            int pos = queue[head++];
            int x = pos % WIDTH;
            int z = pos / WIDTH % WIDTH;
            int y = pos / LAYER;
            for (int d = 0; d < 6; d++) {
                if (d == 0 && x == 0
                        || d == 1 && x == WIDTH - 1
                        || d == 2 && z == 0
                        || d == 3 && z == WIDTH - 1
                        || d == 4 && y == 0
                        || d == 5 && y == 255) continue;
                int next =
                        pos
                                + switch (d) {
                                    case 0 -> -1;
                                    case 1 -> 1;
                                    case 2 -> -WIDTH;
                                    case 3 -> WIDTH;
                                    case 4 -> -LAYER;
                                    default -> LAYER;
                                };
                if (blocks[next] == material && !seen.get(next)) {
                    seen.set(next);
                    queue[tail++] = next;
                }
            }
        }
        return tail;
    }
}
