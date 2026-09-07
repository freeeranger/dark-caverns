package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.generation.PortedJigsawStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CustomStructureTypes {
    private static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, DarkCaverns.MOD_ID);

    public static final DeferredHolder<StructureType<?>, StructureType<PortedJigsawStructure>>
            PORTED_JIGSAW =
                    STRUCTURE_TYPES.register(
                            "ported_jigsaw", () -> () -> PortedJigsawStructure.CODEC);

    private CustomStructureTypes() {}

    public static void register(IEventBus modBus) {
        STRUCTURE_TYPES.register(modBus);
    }
}
