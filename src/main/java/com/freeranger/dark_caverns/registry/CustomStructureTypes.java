package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.generation.PortedJigsawStructure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class CustomStructureTypes {
    public static final DeferredHolder<StructureType<?>, StructureType<PortedJigsawStructure>> PORTED_JIGSAW =
            ModRegistries.STRUCTURE_TYPES.register("ported_jigsaw", () -> () -> PortedJigsawStructure.CODEC);

    private CustomStructureTypes() {
    }

    public static void bootstrap() {
        // Forces class initialization before the deferred register attaches to the mod event bus.
    }
}
