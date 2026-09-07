package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.entities.CorruptedPearlEntity;
import com.freeranger.dark_caverns.entities.PortedCreature;
import com.freeranger.dark_caverns.entities.PortedMonster;
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

    public static final DeferredHolder<EntityType<?>, EntityType<PortedMonster>> SCORCHLING_ENTITY =
            monster("scorchling", PortedMonster::scorchling, 0.6F, 0.4F, true);
    public static final DeferredHolder<EntityType<?>, EntityType<PortedCreature>> MOLTENER_ENTITY =
            creature("moltener", PortedCreature::moltener, 0.7F, 0.9F, true);
    public static final DeferredHolder<EntityType<?>, EntityType<PortedCreature>> CAMOROCK_ENTITY =
            creature("camorock", PortedCreature::camorock, 0.9F, 0.6F, false);
    public static final DeferredHolder<EntityType<?>, EntityType<PortedMonster>>
            SCORCHHOUND_ENTITY =
                    monster("scorchhound", PortedMonster::scorchhound, 1.5F, 1.0F, true);
    public static final DeferredHolder<EntityType<?>, EntityType<PortedMonster>>
            LUMINITE_GOLEM_ENTITY =
                    monster("luminite_golem", PortedMonster::luminiteGolem, 1.3F, 2.0F, false);
    public static final DeferredHolder<EntityType<?>, EntityType<PortedCreature>>
            LUMINITE_FOX_ENTITY =
                    creature("luminite_fox", PortedCreature::luminiteFox, 0.7F, 0.4F, false);
    public static final DeferredHolder<EntityType<?>, EntityType<ShroomieEntity>> SHROOMIE_ENTITY =
            ENTITY_TYPES.register(
                    "shroomie",
                    () ->
                            EntityType.Builder.of(ShroomieEntity::new, MobCategory.CREATURE)
                                    .sized(0.5F, 1.2F)
                                    .build("shroomie"));
    public static final DeferredHolder<EntityType<?>, EntityType<PortedCreature>>
            SHROOMLING_ENTITY =
                    creature("shroomling", PortedCreature::shroomling, 1.5F, 0.6F, false);

    private CustomEntityTypes() {}

    public static void register(IEventBus modBus) {
        ENTITY_TYPES.register(modBus);
        modBus.addListener(CustomEntityTypes::registerAttributes);
        modBus.addListener(CustomEntityTypes::registerSpawnPlacements);
    }

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(SCORCHLING_ENTITY.get(), PortedMonster.scorchlingAttributes().build());
        event.put(SCORCHHOUND_ENTITY.get(), PortedMonster.scorchhoundAttributes().build());
        event.put(LUMINITE_GOLEM_ENTITY.get(), PortedMonster.luminiteGolemAttributes().build());
        event.put(MOLTENER_ENTITY.get(), PortedCreature.moltenerAttributes().build());
        event.put(CAMOROCK_ENTITY.get(), PortedCreature.camorockAttributes().build());
        event.put(LUMINITE_FOX_ENTITY.get(), PortedCreature.luminiteFoxAttributes().build());
        event.put(SHROOMIE_ENTITY.get(), ShroomieEntity.createAttributes().build());
        event.put(SHROOMLING_ENTITY.get(), PortedCreature.shroomlingAttributes().build());
    }

    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        registerSpawn(event, SCORCHLING_ENTITY.get(), PortedMonster::canScorchlingSpawn);
        registerSpawn(event, SCORCHHOUND_ENTITY.get(), PortedMonster::canScorchhoundSpawn);
        registerSpawn(event, LUMINITE_GOLEM_ENTITY.get(), PortedMonster::canLuminiteGolemSpawn);
        registerSpawn(event, MOLTENER_ENTITY.get(), PortedCreature::canMoltenerSpawn);
        registerSpawn(event, CAMOROCK_ENTITY.get(), PortedCreature::canCamorockSpawn);
        registerSpawn(event, LUMINITE_FOX_ENTITY.get(), PortedCreature::canLuminiteFoxSpawn);
        registerSpawn(event, SHROOMIE_ENTITY.get(), ShroomieEntity::canSpawn);
        registerSpawn(event, SHROOMLING_ENTITY.get(), PortedCreature::canShroomlingSpawn);
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

    private static DeferredHolder<EntityType<?>, EntityType<PortedMonster>> monster(
            String name,
            EntityType.EntityFactory<PortedMonster> factory,
            float width,
            float height,
            boolean fireImmune) {
        EntityType.Builder<PortedMonster> builder =
                EntityType.Builder.of(factory, MobCategory.MONSTER).sized(width, height);
        if (fireImmune) {
            builder.fireImmune();
        }
        return ENTITY_TYPES.register(name, () -> builder.build(name));
    }

    private static DeferredHolder<EntityType<?>, EntityType<PortedCreature>> creature(
            String name,
            EntityType.EntityFactory<PortedCreature> factory,
            float width,
            float height,
            boolean fireImmune) {
        EntityType.Builder<PortedCreature> builder =
                EntityType.Builder.of(factory, MobCategory.CREATURE).sized(width, height);
        if (fireImmune) {
            builder.fireImmune();
        }
        return ENTITY_TYPES.register(name, () -> builder.build(name));
    }
}
