package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.entities.CorruptedPearlEntity;
import com.freeranger.dark_caverns.entities.ShroombombEntity;
import com.freeranger.dark_caverns.entities.ShroomieEntity;
import com.freeranger.dark_caverns.entities.ThrowableLuminiteTorchEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CustomEntityTypes {
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, DarkCaverns.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<ThrowableLuminiteTorchEntity>>
            THROWABLE_LUMINITE_TORCH =
                    projectile("throwable_luminite_torch", ThrowableLuminiteTorchEntity::new);
    public static final DeferredHolder<EntityType<?>, EntityType<ShroombombEntity>> SHROOMBOMB =
            projectile("shroombomb", ShroombombEntity::new);
    public static final DeferredHolder<EntityType<?>, EntityType<CorruptedPearlEntity>>
            CORRUPTED_PEARL = projectile("corrupted_pearl", CorruptedPearlEntity::new);

    public static final DeferredHolder<EntityType<?>, EntityType<ShroomieEntity>> SHROOMIE_ENTITY =
            mob("shroomie", ShroomieEntity::new, MobCategory.CREATURE, 0.5F, 1.2F, false);

    private CustomEntityTypes() {}

    public static void register(IEventBus modBus) {
        ENTITY_TYPES.register(modBus);
        modBus.addListener(CustomEntityTypes::registerAttributes);
        modBus.addListener(CustomEntityTypes::registerSpawnPlacements);
    }

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(SHROOMIE_ENTITY.get(), ShroomieEntity.createAttributes().build());
    }

    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        registerSpawn(event, SHROOMIE_ENTITY.get(), ShroomieEntity::canSpawn);
    }

    private static <T extends Mob> void registerSpawn(
            RegisterSpawnPlacementsEvent event,
            EntityType<T> type,
            SpawnPlacements.SpawnPredicate<T> predicate) {
        event.register(
                type,
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                predicate,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> projectile(
            String name, EntityType.EntityFactory<T> factory) {
        return ENTITY_TYPES.register(
                name,
                () ->
                        EntityType.Builder.of(factory, MobCategory.MISC)
                                .sized(0.25F, 0.25F)
                                .clientTrackingRange(4)
                                .updateInterval(10)
                                .build(name));
    }

    private static <T extends Mob> DeferredHolder<EntityType<?>, EntityType<T>> mob(
            String name,
            EntityType.EntityFactory<T> factory,
            MobCategory category,
            float width,
            float height,
            boolean fireImmune) {
        EntityType.Builder<T> builder =
                EntityType.Builder.of(factory, category).sized(width, height);
        if (fireImmune) {
            builder.fireImmune();
        }
        return ENTITY_TYPES.register(name, () -> builder.build(name));
    }
}
