package com.freeranger.dark_caverns.datagen;

import com.freeranger.dark_caverns.DarkCaverns;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

final class DarkCavernsStructureTagsProvider extends TagsProvider<Structure> {
    DarkCavernsStructureTagsProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            ExistingFileHelper existingFileHelper) {
        super(output, Registries.STRUCTURE, lookupProvider, DarkCaverns.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(DarkCavernsTags.modStructure("on_forgotten_tower_maps"))
                .add(ResourceKey.create(Registries.STRUCTURE, DarkCaverns.id("forgotten_tower")));
    }
}
