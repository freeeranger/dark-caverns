package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.generation.TransitionPlacement;
import com.freeranger.dark_caverns.generation.TransitionSurfaceRule;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CustomWorldgen {
    private static final DeferredRegister<MapCodec<? extends SurfaceRules.RuleSource>> SURFACES =
            DeferredRegister.create(Registries.MATERIAL_RULE, DarkCaverns.MOD_ID);
    private static final DeferredRegister<PlacementModifierType<?>> PLACEMENTS =
            DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, DarkCaverns.MOD_ID);
    public static final DeferredHolder<
                    PlacementModifierType<?>, PlacementModifierType<TransitionPlacement>>
            TRANSITION_PLACEMENT =
                    PLACEMENTS.register("biome_transition", () -> () -> TransitionPlacement.CODEC);

    static {
        SURFACES.register("biome_transition", () -> TransitionSurfaceRule.CODEC);
    }

    private CustomWorldgen() {}

    public static void register(IEventBus bus) {
        SURFACES.register(bus);
        PLACEMENTS.register(bus);
    }
}
