package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.DarkCaverns;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModRegistries {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(DarkCaverns.MOD_ID);
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(DarkCaverns.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DarkCaverns.MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, DarkCaverns.MOD_ID);
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, DarkCaverns.MOD_ID);
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, DarkCaverns.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, DarkCaverns.MOD_ID);
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, DarkCaverns.MOD_ID);
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, DarkCaverns.MOD_ID);

    private ModRegistries() {}

    public static void register(IEventBus modBus) {
        CustomBlocks.bootstrap();
        CustomItems.bootstrap();
        CustomCreativeTabs.bootstrap();
        CustomSoundEvents.bootstrap();
        CustomParticles.bootstrap();
        CustomArmorMaterials.bootstrap();
        CustomEntityTypes.bootstrap();
        CustomFeatures.bootstrap();
        CustomStructureTypes.bootstrap();

        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        CREATIVE_TABS.register(modBus);
        SOUNDS.register(modBus);
        PARTICLES.register(modBus);
        ARMOR_MATERIALS.register(modBus);
        ENTITY_TYPES.register(modBus);
        FEATURES.register(modBus);
        STRUCTURE_TYPES.register(modBus);

        modBus.addListener(CustomEntityTypes::registerAttributes);
        modBus.addListener(CustomEntityTypes::registerSpawnPlacements);
    }
}
