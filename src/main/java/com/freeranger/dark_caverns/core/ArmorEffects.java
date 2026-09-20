package com.freeranger.dark_caverns.core;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.config.ServerConfig;
import com.freeranger.dark_caverns.registry.CustomAttachments;
import com.freeranger.dark_caverns.registry.CustomEquipment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.PlayLevelSoundEvent;
import net.neoforged.neoforge.event.VanillaGameEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.level.ExplosionKnockbackEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jetbrains.annotations.Nullable;

public final class ArmorEffects {
    private static final float FORTY_PERCENT_REDUCTION_MULTIPLIER = 0.6F;
    private static final double LAVA_DRAG_COMPENSATION = 4.0 / 3.0;
    private static final double SCORCHSTEEL_VISIBILITY_MULTIPLIER = 0.65;
    private static final double SCORCHSTEEL_POUNCE_FORWARD = 0.6;
    private static final double SCORCHSTEEL_POUNCE_UP = 0.1;
    private static final AttributeModifier SHROOMSTONE_SPRINT_SPEED =
            new AttributeModifier(
                    DarkCaverns.id("shroomstone_sprint_speed"),
                    0.1,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    private static final AttributeModifier SHROOMSTONE_JUMP_HEIGHT =
            new AttributeModifier(
                    DarkCaverns.id("shroomstone_jump_height"),
                    0.1,
                    AttributeModifier.Operation.ADD_VALUE);
    private static final AttributeModifier HELLSTONE_LAVA_SWIM_SPEED =
            new AttributeModifier(
                    DarkCaverns.id("hellstone_lava_swim_speed"),
                    0.5,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

    private ArmorEffects() {}

    public static void register(IEventBus gameBus) {
        gameBus.addListener(ArmorEffects::onPlayerTick);
        gameBus.addListener(ArmorEffects::onLivingIncomingDamage);
        gameBus.addListener(ArmorEffects::onLivingDamage);
        gameBus.addListener(ArmorEffects::onLivingKnockBack);
        gameBus.addListener(ArmorEffects::onExplosionKnockback);
        gameBus.addListener(ArmorEffects::onMonsterTick);
        gameBus.addListener(ArmorEffects::onAttackEntity);
        gameBus.addListener(ArmorEffects::onLivingVisibility);
        gameBus.addListener(ArmorEffects::onLivingChangeTarget);
        gameBus.addListener(ArmorEffects::onLivingJump);
        gameBus.addListener(ArmorEffects::onVanillaGameEvent);
        gameBus.addListener(ArmorEffects::onPlayLevelSound);
    }

    private static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        applyShroomstoneMovement(player);
        applyHellstoneLavaMovement(player);

        if (player.level().isClientSide()) {
            return;
        }

        shortenHellstoneBurnTime(player);
        applyScorchsteelBonus(player);
    }

    private static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)
                || !wearing(player, EquipmentSlot.FEET, CustomEquipment.HELLSTONE_BOOTS.get())) {
            return;
        }

        if (event.getSource().is(DamageTypes.IN_FIRE)
                || event.getSource().is(DamageTypes.CAMPFIRE)
                || event.getSource().is(DamageTypes.HOT_FLOOR)) {
            event.setCanceled(true);
        }
    }

    private static void onLivingDamage(LivingDamageEvent.Pre event) {
        if (event.getSource().getEntity() instanceof Player attacker) {
            breakScorchsteelStealth(attacker);
        }
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        breakScorchsteelStealth(player);

        if (event.getSource().is(DamageTypeTags.IS_FALL)
                && wearing(player, EquipmentSlot.FEET, CustomEquipment.SHROOMSTONE_BOOTS.get())) {
            event.setNewDamage(0.0F);
        } else if (event.getSource().is(DamageTypeTags.IS_FIRE)
                && wearing(
                        player, EquipmentSlot.CHEST, CustomEquipment.HELLSTONE_CHESTPLATE.get())) {
            event.setNewDamage(event.getNewDamage() * FORTY_PERCENT_REDUCTION_MULTIPLIER);
        }
    }

    private static void onLivingKnockBack(LivingKnockBackEvent event) {
        if (event.getEntity() instanceof Player player
                && wearing(
                        player,
                        EquipmentSlot.CHEST,
                        CustomEquipment.SHROOMSTONE_CHESTPLATE.get())) {
            event.setStrength(event.getStrength() * FORTY_PERCENT_REDUCTION_MULTIPLIER);
        }
    }

    private static void onExplosionKnockback(ExplosionKnockbackEvent event) {
        if (event.getAffectedEntity() instanceof Player player
                && wearing(
                        player,
                        EquipmentSlot.CHEST,
                        CustomEquipment.SHROOMSTONE_CHESTPLATE.get())) {
            event.setKnockbackVelocity(
                    event.getKnockbackVelocity().scale(FORTY_PERCENT_REDUCTION_MULTIPLIER));
        }
    }

    private static void onAttackEntity(AttackEntityEvent event) {
        breakScorchsteelStealth(event.getEntity());
    }

    private static void onMonsterTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Monster monster)
                || !(monster.getTarget() instanceof Player player)
                || !player.hasEffect(MobEffects.INVISIBILITY)) {
            return;
        }
        ScorchsteelStealthState state =
                player.getExistingDataOrNull(CustomAttachments.SCORCHSTEEL_STEALTH);
        if (state != null && state.isActive()) {
            monster.setTarget(null);
        }
    }

    private static void applyShroomstoneMovement(Player player) {
        setTransientModifier(
                player,
                Attributes.MOVEMENT_SPEED,
                SHROOMSTONE_SPRINT_SPEED,
                player.isSprinting()
                        && wearing(
                                player,
                                EquipmentSlot.HEAD,
                                CustomEquipment.SHROOMSTONE_HELMET.get()));
        setTransientModifier(
                player,
                Attributes.JUMP_STRENGTH,
                SHROOMSTONE_JUMP_HEIGHT,
                wearing(player, EquipmentSlot.LEGS, CustomEquipment.SHROOMSTONE_LEGGINGS.get()));
    }

    private static void applyHellstoneLavaMovement(Player player) {
        boolean active =
                player.isInLava()
                        && wearing(
                                player,
                                EquipmentSlot.LEGS,
                                CustomEquipment.HELLSTONE_LEGGINGS.get());
        setTransientModifier(player, NeoForgeMod.SWIM_SPEED, HELLSTONE_LAVA_SWIM_SPEED, active);
        if (active) {
            Vec3 movement = player.getDeltaMovement();
            player.setDeltaMovement(
                    movement.x * LAVA_DRAG_COMPENSATION,
                    movement.y,
                    movement.z * LAVA_DRAG_COMPENSATION);
        }
    }

    private static void shortenHellstoneBurnTime(Player player) {
        if (player.getRemainingFireTicks() <= 0
                || player.isInLava()
                || isTouchingFire(player)
                || !wearing(player, EquipmentSlot.HEAD, CustomEquipment.HELLSTONE_HELMET.get())) {
            return;
        }
        player.setRemainingFireTicks(Math.max(0, player.getRemainingFireTicks() - 1));
    }

    private static void setTransientModifier(
            Player player,
            Holder<Attribute> attribute,
            AttributeModifier modifier,
            boolean active) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) {
            return;
        }
        if (active) {
            instance.addOrUpdateTransientModifier(modifier);
        } else {
            instance.removeModifier(modifier.id());
        }
    }

    private static boolean isTouchingFire(Player player) {
        return BlockPos.betweenClosedStream(player.getBoundingBox().deflate(1.0E-6))
                .anyMatch(pos -> player.level().getBlockState(pos).is(BlockTags.FIRE));
    }

    private static void applyScorchsteelBonus(Player player) {
        if (!wearing(player, EquipmentSlot.CHEST, CustomEquipment.SCORCHSTEEL_CHESTPLATE.get())) {
            breakScorchsteelStealth(player);
            player.removeData(CustomAttachments.SCORCHSTEEL_STEALTH);
            return;
        }

        ScorchsteelStealthState state = player.getData(CustomAttachments.SCORCHSTEEL_STEALTH);
        int stillTicks = state.update(player.position());

        if (state.isActive() && stillTicks == 0) {
            breakScorchsteelStealth(player);
            return;
        }

        if (stillTicks >= ServerConfig.scorchsteelStealthStandstillTicks()) {
            if (state.activate()) {
                player.displayClientMessage(
                        Component.translatable("message.dark_caverns.scorchsteel.stealth_active"),
                        true);
                player.level()
                        .playSound(
                                null,
                                player.blockPosition(),
                                SoundEvents.BEACON_ACTIVATE,
                                SoundSource.PLAYERS,
                                0.35F,
                                1.5F);
            }
            player.addEffect(
                    new MobEffectInstance(MobEffects.INVISIBILITY, 10, 0, false, false, true));
        }
    }

    private static void breakScorchsteelStealth(Player player) {
        ScorchsteelStealthState state =
                player.getExistingDataOrNull(CustomAttachments.SCORCHSTEEL_STEALTH);
        if (state == null || !state.deactivate()) {
            return;
        }
        player.removeEffect(MobEffects.INVISIBILITY);
        player.displayClientMessage(
                Component.translatable("message.dark_caverns.scorchsteel.stealth_broken"), true);
        player.level()
                .playSound(
                        null,
                        player.blockPosition(),
                        SoundEvents.BEACON_DEACTIVATE,
                        SoundSource.PLAYERS,
                        0.25F,
                        1.4F);
    }

    private static void onLivingVisibility(LivingEvent.LivingVisibilityEvent event) {
        if (event.getEntity() instanceof Player player
                && wearing(player, EquipmentSlot.HEAD, CustomEquipment.SCORCHSTEEL_HELMET.get())) {
            event.modifyVisibility(SCORCHSTEEL_VISIBILITY_MULTIPLIER);
        }
    }

    private static void onLivingChangeTarget(LivingChangeTargetEvent event) {
        if (!(event.getEntity() instanceof Mob mob)
                || !(event.getNewAboutToBeSetTarget() instanceof Player player)
                || !wearing(player, EquipmentSlot.HEAD, CustomEquipment.SCORCHSTEEL_HELMET.get())) {
            return;
        }
        if (mob.getLastHurtByMob() == player) {
            return;
        }
        double followRange = mob.getAttributeValue(Attributes.FOLLOW_RANGE);
        double maxDistance = followRange * SCORCHSTEEL_VISIBILITY_MULTIPLIER;
        if (player.isCrouching() || player.isShiftKeyDown()) {
            maxDistance *= 0.8;
        }
        if (mob.distanceTo(player) > maxDistance) {
            event.setNewAboutToBeSetTarget(null);
        }
    }

    private static void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (!(event.getEntity() instanceof Player player)
                || (!player.isCrouching() && !player.isShiftKeyDown())
                || !wearing(
                        player, EquipmentSlot.LEGS, CustomEquipment.SCORCHSTEEL_LEGGINGS.get())) {
            return;
        }

        Vec3 look = player.getLookAngle();
        Vec3 horizontal = new Vec3(look.x, 0.0, look.z);
        if (horizontal.lengthSqr() < 1.0E-4) {
            float yRot = player.getYRot();
            float f = -((float) Math.toRadians(yRot));
            horizontal = new Vec3(Math.sin(f), 0.0, Math.cos(f));
        } else {
            horizontal = horizontal.normalize();
        }

        Vec3 movement = player.getDeltaMovement();
        player.setDeltaMovement(
                movement.x + horizontal.x * SCORCHSTEEL_POUNCE_FORWARD,
                movement.y + SCORCHSTEEL_POUNCE_UP,
                movement.z + horizontal.z * SCORCHSTEEL_POUNCE_FORWARD);
        player.hurtMarked = true;
        player.hasImpulse = true;

        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.SMOKE,
                    player.getX(),
                    player.getY() + 0.2,
                    player.getZ(),
                    6,
                    0.2,
                    0.1,
                    0.2,
                    0.02);
            player.level()
                    .playSound(
                            null,
                            player.blockPosition(),
                            SoundEvents.PLAYER_ATTACK_SWEEP,
                            SoundSource.PLAYERS,
                            0.5F,
                            1.4F);
        }
    }

    private static void onVanillaGameEvent(VanillaGameEvent event) {
        if (!(event.getCause() instanceof Player player)
                || !wearing(player, EquipmentSlot.FEET, CustomEquipment.SCORCHSTEEL_BOOTS.get())) {
            return;
        }
        if (event.getVanillaEvent().is(GameEvent.STEP.key())
                || event.getVanillaEvent().is(GameEvent.HIT_GROUND.key())) {
            event.setCanceled(true);
        }
    }

    private static void onPlayLevelSound(PlayLevelSoundEvent event) {
        if (event.getSource() != SoundSource.PLAYERS || !isStepOrFallSound(event.getSound())) {
            return;
        }
        if (event instanceof PlayLevelSoundEvent.AtEntity atEntity) {
            if (atEntity.getEntity() instanceof Player player
                    && wearing(
                            player, EquipmentSlot.FEET, CustomEquipment.SCORCHSTEEL_BOOTS.get())) {
                event.setCanceled(true);
            }
            return;
        }
        if (event instanceof PlayLevelSoundEvent.AtPosition atPosition) {
            Vec3 pos = atPosition.getPosition();
            for (Player player : event.getLevel().players()) {
                if (wearing(player, EquipmentSlot.FEET, CustomEquipment.SCORCHSTEEL_BOOTS.get())
                        && player.position().distanceToSqr(pos) <= 1.0) {
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    private static boolean isStepOrFallSound(@Nullable Holder<SoundEvent> soundHolder) {
        if (soundHolder == null) {
            return false;
        }
        ResourceLocation location =
                soundHolder
                        .unwrapKey()
                        .map(key -> key.location())
                        .orElseGet(
                                () -> {
                                    try {
                                        return soundHolder.value().getLocation();
                                    } catch (Exception e) {
                                        return null;
                                    }
                                });
        if (location == null) {
            return false;
        }
        String path = location.getPath();
        return path.endsWith(".step") || path.endsWith(".fall") || path.contains("step");
    }

    private static boolean wearing(Player player, EquipmentSlot slot, Item item) {
        return player.getItemBySlot(slot).is(item);
    }
}
