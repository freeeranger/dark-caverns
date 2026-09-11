package com.freeranger.dark_caverns.gametest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

/** A bounded in-memory view for exercising real features without touching a saved world. */
final class TerrainTestWorld {
    private TerrainTestWorld() {}

    static WorldGenLevel create(
            Function<BlockPos, BlockState> read,
            BiConsumer<BlockPos, BlockState> write,
            Predicate<BlockPos> bounds,
            Holder<Biome> biome,
            long seed) {
        return create(
                read,
                write,
                bounds,
                biome,
                seed,
                pos -> {
                    throw new UnsupportedOperationException("No chunk backing");
                });
    }

    static WorldGenLevel create(
            Function<BlockPos, BlockState> read,
            BiConsumer<BlockPos, BlockState> write,
            Predicate<BlockPos> bounds,
            Holder<Biome> biome,
            long seed,
            Function<ChunkPos, ChunkAccess> chunks) {
        RandomSource random = RandomSource.create(seed);
        return (WorldGenLevel)
                Proxy.newProxyInstance(
                        WorldGenLevel.class.getClassLoader(),
                        new Class<?>[] {WorldGenLevel.class},
                        (proxy, method, args) -> {
                            return switch (method.getName()) {
                                case "getBlockState" -> read.apply((BlockPos) args[0]);
                                case "getChunk" -> {
                                    ChunkPos pos =
                                            args[0] instanceof BlockPos block
                                                    ? new ChunkPos(block)
                                                    : new ChunkPos((int) args[0], (int) args[1]);
                                    yield chunks.apply(pos);
                                }
                                case "getFluidState" ->
                                        read.apply((BlockPos) args[0]).getFluidState();
                                case "isEmptyBlock" -> read.apply((BlockPos) args[0]).isAir();
                                case "ensureCanWrite" -> bounds.test((BlockPos) args[0]);
                                case "setBlock" -> {
                                    BlockPos pos = ((BlockPos) args[0]).immutable();
                                    if (!bounds.test(pos))
                                        throw new AssertionError(
                                                "Out-of-bounds feature write: " + pos);
                                    write.accept(pos, (BlockState) args[1]);
                                    yield true;
                                }
                                case "getMinBuildHeight" -> 0;
                                case "getMaxBuildHeight" -> 256;
                                case "getHeight" -> {
                                    if (args == null || args.length == 0) yield 256;
                                    int x = (int) args[1];
                                    int z = (int) args[2];
                                    int y = 255;
                                    while (y >= 0 && read.apply(new BlockPos(x, y, z)).isAir()) y--;
                                    yield y + 1;
                                }
                                case "getBiome" -> biome;
                                case "getSeed" -> seed;
                                case "getRandom" -> random;
                                case "toString" -> "TerrainTestWorld";
                                case "hashCode" -> System.identityHashCode(proxy);
                                case "equals" -> proxy == args[0];
                                default -> {
                                    if (method.isDefault())
                                        yield InvocationHandler.invokeDefault(proxy, method, args);
                                    throw new UnsupportedOperationException(
                                            "Unexpected world access: " + method);
                                }
                            };
                        });
    }
}
