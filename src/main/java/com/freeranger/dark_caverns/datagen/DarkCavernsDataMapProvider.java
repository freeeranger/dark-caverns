package com.freeranger.dark_caverns.datagen;

import com.freeranger.dark_caverns.DarkCaverns;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

final class DarkCavernsDataMapProvider extends DataMapProvider {
    DarkCavernsDataMapProvider(
            PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        builder(NeoForgeDataMaps.COMPOSTABLES)
                .add(DarkCaverns.id("glimmershroom"), new Compostable(0.65F), false)
                .add(DarkCaverns.id("glimmershroom_block"), new Compostable(0.85F), false)
                .add(DarkCaverns.id("glimmergrass"), new Compostable(0.30F), false)
                .add(DarkCaverns.id("charred_grass"), new Compostable(0.30F), false)
                .add(DarkCaverns.id("scorched_berries"), new Compostable(0.30F), false);
        builder(NeoForgeDataMaps.FURNACE_FUELS)
                .add(DarkCaverns.id("scorchling_tail"), new FurnaceFuel(1600), false);
    }
}
