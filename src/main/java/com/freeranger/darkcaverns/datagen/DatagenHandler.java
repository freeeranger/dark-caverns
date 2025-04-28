package com.freeranger.darkcaverns.datagen;

import com.freeranger.darkcaverns.DarkCaverns;
import com.freeranger.darkcaverns.datagen.providers.*;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = DarkCaverns.MODID)
public class DatagenHandler {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        event.createProvider(ModelDataProvider::new);
        event.createProvider(BlockTagsDataProvider::new);
        event.createProvider(LanguageEnUsDataProvider::new); // en_us
        event.createProvider(RecipeDataProvider.Runner::new);

        event.createProvider((output, lookupProvider) -> new LootTableProvider(
            output,
            Set.of(),
            List.of(
                new LootTableProvider.SubProviderEntry(
                    BlockLootTableDataProvider::new,
                    LootContextParamSets.BLOCK
                )
            ),
            lookupProvider
        ));
    }
}
