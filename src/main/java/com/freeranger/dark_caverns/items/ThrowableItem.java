package com.freeranger.dark_caverns.items;

import java.util.function.BiFunction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class ThrowableItem extends Item {
    private final SoundEvent throwSound;
    private final int cooldownTicks;
    private final BiFunction<Level, LivingEntity, ? extends ThrowableItemProjectile>
            projectileFactory;

    public ThrowableItem(
            Properties properties,
            SoundEvent throwSound,
            int cooldownTicks,
            BiFunction<Level, LivingEntity, ? extends ThrowableItemProjectile> projectileFactory) {
        super(properties);
        this.throwSound = throwSound;
        this.cooldownTicks = cooldownTicks;
        this.projectileFactory = projectileFactory;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
            Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                throwSound,
                SoundSource.NEUTRAL,
                0.5F,
                0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        if (cooldownTicks > 0) {
            player.getCooldowns().addCooldown(this, cooldownTicks);
        }
        if (!level.isClientSide) {
            ThrowableItemProjectile projectile = projectileFactory.apply(level, player);
            projectile.setItem(stack);
            projectile.shootFromRotation(
                    player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(projectile);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        stack.consume(1, player);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
