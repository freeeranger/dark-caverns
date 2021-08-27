package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.entities.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = DarkCaverns.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CustomEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, DarkCaverns.MOD_ID);

    public static final EntityType<ThrowableLuminiteTorchEntity> THROWABLE_LUMINITE_TORCH_TYPE =
            EntityType.Builder.<ThrowableLuminiteTorchEntity>of(ThrowableLuminiteTorchEntity::new, EntityClassification.MISC)
            .sized(0.25F, 0.25F)
            .updateInterval(10)
            .setTrackingRange(4)
            .build("throwable_luminite_torch");
    public static final RegistryObject<EntityType<ThrowableLuminiteTorchEntity>> THROWABLE_LUMINITE_TORCH =
            ENTITIES.register("throwable_luminite_torch", () -> THROWABLE_LUMINITE_TORCH_TYPE);

    public static final EntityType<ShroombombEntity> SHROOMBOMB_TYPE =
            EntityType.Builder.<ShroombombEntity>of(ShroombombEntity::new, EntityClassification.MISC)
                    .sized(0.25F, 0.25F)
                    .updateInterval(10)
                    .setTrackingRange(4)
                    .build("shroombomb");
    public static final RegistryObject<EntityType<ShroombombEntity>> SHROOMBOMB =
            ENTITIES.register("shroombomb", () -> SHROOMBOMB_TYPE);

    public static final EntityType<CorruptedPearlEntity> CORRUPTED_PEARL_TYPE =
            EntityType.Builder.<CorruptedPearlEntity>of(CorruptedPearlEntity::new, EntityClassification.MISC)
                    .sized(0.25F, 0.25F)
                    .updateInterval(10)
                    .setTrackingRange(4)
                    .build("corrupted_pearl");
    public static final RegistryObject<EntityType<CorruptedPearlEntity>> CORRUPTED_PEARL =
            ENTITIES.register("corrupted_pearl", () -> CORRUPTED_PEARL_TYPE);


    public static final RegistryObject<EntityType<ScorchlingEntity>> SCORCHLING_ENTITY = buildEntity(ScorchlingEntity::new,
            0.6f, 0.4F, "scorchling", true);

    public static final RegistryObject<EntityType<MoltenerEntity>> MOLTENER_ENTITY = buildEntity(MoltenerEntity::new,
            0.7f, 0.9F, "moltener", true);

    public static final RegistryObject<EntityType<CamorockEntity>> CAMOROCK_ENTITY = buildEntity(CamorockEntity::new,
            0.9f, 0.6F, "camorock", false);

    public static final RegistryObject<EntityType<ScorchhoundEntity>> SCORCHHOUND_ENTITY = buildEntity(ScorchhoundEntity::new,
            1.5f, 1F, "scorchhound", true);

    public static final RegistryObject<EntityType<LuminiteGolemEntity>> LUMINITE_GOLEM_ENTITY = buildEntity(LuminiteGolemEntity::new,
            1.3f, 2F, "luminite_golem", false);

    public static final RegistryObject<EntityType<LuminiteFoxEntity>> LUMINITE_FOX_ENTITY = buildEntity(LuminiteFoxEntity::new,
            0.7f, 0.4F, "luminite_fox", false);

    public static final RegistryObject<EntityType<ShroomieEntity>> SHROOMIE_ENTITY = buildEntity(ShroomieEntity::new,
            0.5f, 1.2F, "shroomie", false);

    public static final RegistryObject<EntityType<ShroomlingEntity>> SHROOMLING_ENTITY = buildEntity(ShroomlingEntity::new,
            1.5f, 0.6F, "shroomling", false);

    public static <T extends Entity> RegistryObject<EntityType<T>> buildEntity(EntityType.IFactory<T> entity, float width, float height, String name, boolean isFireImmune) {
        return isFireImmune ? ENTITIES.register(name, () -> EntityType.Builder.of(entity, EntityClassification.CREATURE).sized(width, height).fireImmune().build(name))
                : ENTITIES.register(name, () -> EntityType.Builder.of(entity, EntityClassification.CREATURE).sized(width, height).build(name));
    }

    public static void registerSpawnPlacements(){
        EntitySpawnPlacementRegistry.register(SCORCHLING_ENTITY.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ScorchlingEntity::canScorchlingSpawn);

        EntitySpawnPlacementRegistry.register(SCORCHHOUND_ENTITY.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ScorchhoundEntity::canScorchhoundSpawn);

        EntitySpawnPlacementRegistry.register(MOLTENER_ENTITY.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MoltenerEntity::canMoltenerSpawn);

        EntitySpawnPlacementRegistry.register(CAMOROCK_ENTITY.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, CamorockEntity::canCamorockSpawn);

        EntitySpawnPlacementRegistry.register(LUMINITE_GOLEM_ENTITY.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, LuminiteGolemEntity::canLuminiteGolemSpawn);

        EntitySpawnPlacementRegistry.register(LUMINITE_FOX_ENTITY.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, LuminiteFoxEntity::canLuminiteFoxSpawn);

        EntitySpawnPlacementRegistry.register(SHROOMIE_ENTITY.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ShroomieEntity::canShroomieSpawn);

        EntitySpawnPlacementRegistry.register(SHROOMLING_ENTITY.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ShroomlingEntity::canShroomlingSpawn);
    }
}
