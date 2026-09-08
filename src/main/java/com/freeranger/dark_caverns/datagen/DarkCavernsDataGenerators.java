package com.freeranger.dark_caverns.datagen;

import com.freeranger.dark_caverns.DarkCaverns;
import java.util.List;
import java.util.Set;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.data.event.GatherDataEvent;

/** Registers the generated resources maintained from the mod's Java content definitions. */
public final class DarkCavernsDataGenerators {
    private DarkCavernsDataGenerators() {}

    public static void register(IEventBus modBus) {
        modBus.addListener(DarkCavernsDataGenerators::gatherData);
    }

    private static void gatherData(GatherDataEvent event) {
        if (event.includeClient()) {
            event.createProvider(
                    output ->
                            new DarkCavernsBlockStateProvider(
                                    output, event.getExistingFileHelper()));
            event.createProvider(
                    output ->
                            new DarkCavernsItemModelProvider(
                                    output, event.getExistingFileHelper()));
        }

        if (event.includeServer()) {
            var blockTags =
                    event.createProvider(
                            (output, lookup) ->
                                    new DarkCavernsBlockTagsProvider(
                                            output,
                                            lookup,
                                            DarkCaverns.MOD_ID,
                                            event.getExistingFileHelper()));
            event.addProvider(
                    new DarkCavernsItemTagsProvider(
                            event.getGenerator().getPackOutput(),
                            event.getLookupProvider(),
                            blockTags.contentsGetter(),
                            event.getExistingFileHelper()));
            event.createProvider(
                    (output, lookup) ->
                            new DarkCavernsBiomeTagsProvider(
                                    output, lookup, event.getExistingFileHelper()));
            event.createProvider(
                    (output, lookup) ->
                            new DarkCavernsStructureTagsProvider(
                                    output, lookup, event.getExistingFileHelper()));
            event.createProvider(DarkCavernsRecipeProvider::new);
            event.createProvider(DarkCavernsDataMapProvider::new);
            event.addProvider(
                    new LootTableProvider(
                            event.getGenerator().getPackOutput(),
                            Set.of(),
                            List.of(
                                    new LootTableProvider.SubProviderEntry(
                                            DarkCavernsBlockLoot::new, LootContextParamSets.BLOCK)),
                            event.getLookupProvider()));
        }
    }
}
