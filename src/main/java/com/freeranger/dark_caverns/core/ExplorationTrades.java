package com.freeranger.dark_caverns.core;

import com.freeranger.dark_caverns.DarkCaverns;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

@EventBusSubscriber(modid = DarkCaverns.MOD_ID)
public final class ExplorationTrades {
    public static final TagKey<Structure> FORGOTTEN_TOWER_MAP_DESTINATIONS =
            TagKey.create(Registries.STRUCTURE, DarkCaverns.id("on_forgotten_tower_maps"));

    private ExplorationTrades() {}

    @SubscribeEvent
    public static void addCartographerTrade(VillagerTradesEvent event) {
        if (event.getType() != VillagerProfession.CARTOGRAPHER) {
            return;
        }
        event.getTrades()
                .get(5)
                .add(
                        new VillagerTrades.TreasureMapForEmeralds(
                                36,
                                FORGOTTEN_TOWER_MAP_DESTINATIONS,
                                "filled_map.dark_caverns:forgotten_tower",
                                MapDecorationTypes.RED_X,
                                1,
                                15));
    }
}
