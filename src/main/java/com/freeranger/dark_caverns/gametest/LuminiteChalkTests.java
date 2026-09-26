package com.freeranger.dark_caverns.gametest;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.blocks.LuminiteChalkMarkBlock;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import com.freeranger.dark_caverns.registry.CustomItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(DarkCaverns.MOD_ID)
@PrefixGameTestTemplate(false)
public final class LuminiteChalkTests {
    private LuminiteChalkTests() {}

    @GameTest(template = "sacred_torch")
    public static void chalkPropertiesAndDurability(GameTestHelper helper) {
        ItemStack chalk = new ItemStack(CustomItems.LUMINITE_CHALK.get());
        helper.assertTrue(chalk.getMaxDamage() == 64, "Luminite Chalk should have 64 durability");

        BlockState defaultMarkState = CustomBlocks.LUMINITE_CHALK_MARK.get().defaultBlockState();
        helper.assertTrue(
                defaultMarkState.getLightEmission(helper.getLevel(), BlockPos.ZERO) == 0,
                "Luminite Chalk Mark should not emit light itself (light level 0)");
        helper.assertTrue(
                defaultMarkState.getPistonPushReaction() == PushReaction.DESTROY,
                "Luminite Chalk Mark should be destroyed by pistons");
        helper.assertTrue(
                defaultMarkState.getCollisionShape(helper.getLevel(), BlockPos.ZERO).isEmpty(),
                "Luminite Chalk Mark should have empty collision shape");
        helper.assertTrue(
                defaultMarkState.emissiveRendering(helper.getLevel(), BlockPos.ZERO),
                "Luminite Chalk Mark should have emissive rendering (glow in the dark)");

        helper.succeed();
    }

    @GameTest(template = "sacred_torch")
    public static void chalkPlacementRotationAndErasing(GameTestHelper helper) {
        BlockPos basePos = new BlockPos(2, 2, 2);
        BlockPos markPos = basePos.above();

        helper.setBlock(basePos, Blocks.STONE.defaultBlockState());
        helper.setBlock(markPos, Blocks.AIR.defaultBlockState());

        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(CustomItems.LUMINITE_CHALK.get()));

        Vec3 hitVec = Vec3.atCenterOf(helper.absolutePos(basePos)).add(0, 0.5, 0);
        BlockHitResult hitResult =
                new BlockHitResult(hitVec, Direction.UP, helper.absolutePos(basePos), false);
        UseOnContext context = new UseOnContext(player, InteractionHand.MAIN_HAND, hitResult);

        InteractionResult result = CustomItems.LUMINITE_CHALK.get().useOn(context);
        helper.assertTrue(
                result.consumesAction(), "Using chalk on solid stone top face should succeed");

        BlockState placedState = helper.getBlockState(markPos);
        helper.assertTrue(
                placedState.is(CustomBlocks.LUMINITE_CHALK_MARK.get()),
                "Chalk mark block should be placed at the position above the stone");
        helper.assertTrue(
                placedState.getValue(LuminiteChalkMarkBlock.FACING) == Direction.UP,
                "Chalk mark facing should be UP");
        int initialRotation = placedState.getValue(LuminiteChalkMarkBlock.ROTATION);

        // Click existing mark to rotate
        BlockHitResult markHit =
                new BlockHitResult(
                        Vec3.atCenterOf(helper.absolutePos(markPos)),
                        Direction.UP,
                        helper.absolutePos(markPos),
                        false);
        UseOnContext rotateContext = new UseOnContext(player, InteractionHand.MAIN_HAND, markHit);
        InteractionResult rotateResult = CustomItems.LUMINITE_CHALK.get().useOn(rotateContext);
        helper.assertTrue(rotateResult.consumesAction(), "Clicking mark should rotate it");

        BlockState rotatedState = helper.getBlockState(markPos);
        helper.assertTrue(
                rotatedState.getValue(LuminiteChalkMarkBlock.ROTATION) == (initialRotation + 1) % 4,
                "Chalk mark rotation should have incremented by 1");

        // Sneak-click to erase
        player.setShiftKeyDown(true);
        InteractionResult eraseResult = CustomItems.LUMINITE_CHALK.get().useOn(rotateContext);
        helper.assertTrue(eraseResult.consumesAction(), "Sneak-clicking mark should erase it");
        helper.assertTrue(
                helper.getBlockState(markPos).isAir(),
                "Chalk mark should be removed after sneak-clicking");

        helper.succeed();
    }

    @GameTest(template = "sacred_torch")
    public static void chalkDropsWhenSupportBroken(GameTestHelper helper) {
        BlockPos basePos = new BlockPos(1, 2, 1);
        BlockPos markPos = basePos.above();

        helper.setBlock(basePos, Blocks.STONE.defaultBlockState());
        BlockState mark =
                CustomBlocks.LUMINITE_CHALK_MARK
                        .get()
                        .defaultBlockState()
                        .setValue(LuminiteChalkMarkBlock.FACING, Direction.UP);
        helper.setBlock(markPos, mark);

        helper.assertTrue(
                helper.getBlockState(markPos).is(CustomBlocks.LUMINITE_CHALK_MARK.get()),
                "Chalk mark should be present");

        // Destroy supporting stone block
        helper.destroyBlock(basePos);

        helper.assertTrue(
                helper.getBlockState(markPos).isAir(),
                "Chalk mark should pop into air when supporting block is broken");

        helper.succeed();
    }
}
