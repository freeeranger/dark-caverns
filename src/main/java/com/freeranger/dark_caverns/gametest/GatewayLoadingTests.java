package com.freeranger.dark_caverns.gametest;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.generation.GatewayChunkLoading;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(DarkCaverns.MOD_ID)
@PrefixGameTestTemplate(false)
public final class GatewayLoadingTests {
    private GatewayLoadingTests() {}

    @GameTest(
            templateNamespace = DarkCaverns.MOD_ID + "_slow",
            template = "sacred_torch",
            timeoutTicks = 400)
    public static void gatewayLoadingReturnsBeforeChunksAndCompletesOnce(GameTestHelper helper) {
        var level = helper.getLevel();
        var traveler = helper.spawn(EntityType.PIG, new BlockPos(1, 10, 1));
        traveler.setNoAi(true);
        traveler.setNoGravity(true);
        var target = new BlockPos(100000, 64, -100000);
        var chunks = GatewayChunkLoading.landingChunks(target, 4);
        helper.assertTrue(chunks.size() == 4, "Border landing should load exactly four chunks");
        helper.assertTrue(
                GatewayChunkLoading.landingChunks(new BlockPos(-8, 0, -8), 4).size() == 1,
                "Interior landing should load just one chunk, including negative coordinates");
        AtomicInteger calls = new AtomicInteger();
        AtomicBoolean loadedAtCompletion = new AtomicBoolean();
        AtomicLong completionTick = new AtomicLong();
        AtomicReference<GatewayChunkLoading.Result> warmResult = new AtomicReference<>();
        long tick = level.getGameTime();
        long start = System.nanoTime();
        var result =
                GatewayChunkLoading.prepare(
                        level,
                        level,
                        traveler,
                        target,
                        4,
                        () -> {
                            calls.incrementAndGet();
                            completionTick.set(level.getGameTime());
                            loadedAtCompletion.set(
                                    chunks.stream()
                                            .allMatch(
                                                    chunk ->
                                                            level.getChunkSource()
                                                                            .getChunkNow(
                                                                                    chunk.x,
                                                                                    chunk.z)
                                                                    != null));
                            // Check while the preparation ticket is held. In real travel the
                            // teleport places its own portal ticket before this one is released.
                            warmResult.set(
                                    GatewayChunkLoading.prepare(
                                            level,
                                            level,
                                            traveler,
                                            target,
                                            4,
                                            calls::incrementAndGet));
                        });
        long elapsed = System.nanoTime() - start;
        helper.assertTrue(
                result == GatewayChunkLoading.Result.QUEUED && calls.get() == 0,
                "Cold gateway request blocked until chunks finished");
        helper.assertTrue(
                GatewayChunkLoading.prepare(
                                level, level, traveler, target, 4, () -> calls.addAndGet(100))
                        == GatewayChunkLoading.Result.PENDING,
                "Repeated stepOn queued duplicate work");
        helper.runAfterDelay(
                390,
                () -> {
                    GatewayChunkLoading.cancel(traveler);
                    traveler.discard();
                });
        helper.succeedWhen(
                () -> {
                    helper.assertTrue(
                            calls.get() == 2, "Cold and warm continuations did not each run once");
                    helper.assertTrue(
                            completionTick.get() > tick,
                            "Preparation did not yield to server ticks");
                    helper.assertTrue(
                            !GatewayChunkLoading.isPending(traveler),
                            "Completed request retained queue entry");
                    helper.assertTrue(
                            loadedAtCompletion.get(),
                            "Continuation ran before the complete landing search was loaded");
                    helper.assertTrue(
                            warmResult.get() == GatewayChunkLoading.Result.READY,
                            "Warm destination should complete immediately");
                    DarkCaverns.LOGGER.info(
                            "Gateway cold request returned in {}ms; chunks completed after {}"
                                    + " server ticks",
                            elapsed / 1_000_000.0,
                            completionTick.get() - tick);
                    traveler.discard();
                });
    }

    @GameTest(template = "sacred_torch", timeoutTicks = 40)
    public static void gatewayRequestsCancelWhenTravelerLeavesOrIsRemoved(GameTestHelper helper) {
        var level = helper.getLevel();
        var moved = helper.spawn(EntityType.PIG, new BlockPos(0, 10, 0));
        var removed = helper.spawn(EntityType.PIG, new BlockPos(2, 10, 0));
        var brokenGateway = helper.spawn(EntityType.PIG, new BlockPos(1, 12, 0));
        brokenGateway.setNoAi(true);
        brokenGateway.setNoGravity(true);
        AtomicBoolean gatewayExists = new AtomicBoolean(true);
        moved.setNoAi(true);
        moved.setNoGravity(true);
        removed.setNoAi(true);
        removed.setNoGravity(true);
        AtomicInteger calls = new AtomicInteger();
        GatewayChunkLoading.prepare(
                level, level, moved, new BlockPos(200000, 64, -200000), 4, calls::incrementAndGet);
        GatewayChunkLoading.prepare(
                level,
                level,
                removed,
                new BlockPos(300000, 64, -300000),
                4,
                calls::incrementAndGet);
        GatewayChunkLoading.prepare(
                level,
                level,
                brokenGateway,
                new BlockPos(300000, 64, -300000),
                4,
                gatewayExists::get,
                calls::incrementAndGet);
        helper.assertTrue(
                GatewayChunkLoading.isPending(moved)
                        && GatewayChunkLoading.isPending(removed)
                        && GatewayChunkLoading.isPending(brokenGateway),
                "Cold requests were not queued");
        moved.setPos(moved.position().add(8, 0, 0));
        removed.discard();
        gatewayExists.set(false);
        helper.runAfterDelay(
                2,
                () -> {
                    try {
                        helper.assertTrue(
                                !GatewayChunkLoading.isPending(moved)
                                        && !GatewayChunkLoading.isPending(removed)
                                        && !GatewayChunkLoading.isPending(brokenGateway),
                                "Abandoned travel retained pending requests");
                        helper.assertTrue(
                                calls.get() == 0, "Abandoned traveler was teleported later");
                        helper.succeed();
                    } finally {
                        GatewayChunkLoading.cancel(moved);
                        GatewayChunkLoading.cancel(removed);
                        GatewayChunkLoading.cancel(brokenGateway);
                        moved.discard();
                        brokenGateway.discard();
                    }
                });
    }
}
