package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.entities.ThrowableLuminiteTorchEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
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
}
