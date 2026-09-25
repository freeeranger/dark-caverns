package com.freeranger.dark_caverns.items;

import com.freeranger.dark_caverns.blocks.LuminiteChalkMarkBlock;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class LuminiteChalkItem extends Item {
    private static final DustParticleOptions LUMINITE_DUST_PARTICLES =
            new DustParticleOptions(new Vector3f(0.0F, 0.98F, 0.77F), 1.0F);

    public LuminiteChalkItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(clickedPos);
        Player player = context.getPlayer();

        if (clickedState.is(CustomBlocks.LUMINITE_CHALK_MARK.get())) {
            return handleExistingMark(level, clickedPos, clickedState, player);
        }

        Direction clickedFace = context.getClickedFace();
        BlockPos targetPos = clickedPos.relative(clickedFace);
        BlockState targetState = level.getBlockState(targetPos);

        if (targetState.is(CustomBlocks.LUMINITE_CHALK_MARK.get())) {
            return handleExistingMark(level, targetPos, targetState, player);
        }

        if (!clickedState.isFaceSturdy(level, clickedPos, clickedFace)) {
            return InteractionResult.PASS;
        }

        if (!targetState.canBeReplaced() || !level.getFluidState(targetPos).isEmpty()) {
            return InteractionResult.PASS;
        }

        int rotation = calculateRotation(context, clickedPos, clickedFace);
        BlockState newState =
                CustomBlocks.LUMINITE_CHALK_MARK
                        .get()
                        .defaultBlockState()
                        .setValue(LuminiteChalkMarkBlock.FACING, clickedFace)
                        .setValue(LuminiteChalkMarkBlock.ROTATION, rotation);

        if (!level.isClientSide()) {
            level.setBlock(targetPos, newState, Block.UPDATE_ALL);
            level.playSound(
                    null, targetPos, SoundEvents.CALCITE_PLACE, SoundSource.BLOCKS, 0.9F, 1.3F);
            spawnParticles((ServerLevel) level, targetPos, clickedFace);

            if (player != null && !player.getAbilities().instabuild) {
                context.getItemInHand()
                        .hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private static InteractionResult handleExistingMark(
            Level level, BlockPos pos, BlockState state, Player player) {
        if (player != null && player.isSecondaryUseActive()) {
            if (!level.isClientSide()) {
                level.removeBlock(pos, false);
                level.playSound(null, pos, SoundEvents.SAND_BREAK, SoundSource.BLOCKS, 0.8F, 1.4F);
                spawnParticles(
                        (ServerLevel) level, pos, state.getValue(LuminiteChalkMarkBlock.FACING));
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        int currentRot = state.getValue(LuminiteChalkMarkBlock.ROTATION);
        int nextRot = (currentRot + 1) % 4;
        if (!level.isClientSide()) {
            level.setBlock(
                    pos,
                    state.setValue(LuminiteChalkMarkBlock.ROTATION, nextRot),
                    Block.UPDATE_ALL);
            level.playSound(null, pos, SoundEvents.CALCITE_HIT, SoundSource.BLOCKS, 0.8F, 1.5F);
            spawnParticles((ServerLevel) level, pos, state.getValue(LuminiteChalkMarkBlock.FACING));
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private static int calculateRotation(
            UseOnContext context, BlockPos clickedPos, Direction clickedFace) {
        Vec3 hit = context.getClickLocation();
        double rx = hit.x - (double) clickedPos.getX();
        double ry = hit.y - (double) clickedPos.getY();
        double rz = hit.z - (double) clickedPos.getZ();

        if (clickedFace == Direction.UP || clickedFace == Direction.DOWN) {
            double dx = rx - 0.5;
            double dz = rz - 0.5;
            if (Math.abs(dx) > 0.25 || Math.abs(dz) > 0.25) {
                if (Math.abs(dz) >= Math.abs(dx)) {
                    return dz < 0 ? 0 : 2;
                } else {
                    return dx > 0 ? 1 : 3;
                }
            }
            Direction playerDir = context.getHorizontalDirection();
            return switch (playerDir) {
                case NORTH -> 0;
                case EAST -> 1;
                case SOUTH -> 2;
                case WEST -> 3;
                default -> 0;
            };
        }

        double dy = ry - 0.5;
        double horizOffset;
        if (clickedFace == Direction.NORTH) {
            horizOffset = rx - 0.5;
        } else if (clickedFace == Direction.SOUTH) {
            horizOffset = -(rx - 0.5);
        } else if (clickedFace == Direction.EAST) {
            horizOffset = rz - 0.5;
        } else {
            horizOffset = -(rz - 0.5);
        }

        if (Math.abs(dy) > Math.abs(horizOffset)) {
            return dy > 0 ? 0 : 2;
        } else {
            return horizOffset > 0 ? 1 : 3;
        }
    }

    private static void spawnParticles(ServerLevel level, BlockPos pos, Direction facing) {
        double px = pos.getX() + 0.5 + facing.getStepX() * 0.45;
        double py = pos.getY() + 0.5 + facing.getStepY() * 0.45;
        double pz = pos.getZ() + 0.5 + facing.getStepZ() * 0.45;
        level.sendParticles(LUMINITE_DUST_PARTICLES, px, py, pz, 5, 0.15, 0.15, 0.15, 0.02);
    }
}
