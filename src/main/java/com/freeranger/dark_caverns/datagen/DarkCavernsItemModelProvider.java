package com.freeranger.dark_caverns.datagen;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import com.freeranger.dark_caverns.registry.CustomItems;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

final class DarkCavernsItemModelProvider extends ItemModelProvider {
    DarkCavernsItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, DarkCaverns.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        Set<Item> handheld =
                Set.of(
                        item("platinum_sword"),
                        item("platinum_pickaxe"),
                        item("platinum_axe"),
                        item("platinum_shovel"),
                        item("platinum_hoe"),
                        item("hellstone_sword"),
                        item("hellstone_pickaxe"),
                        item("hellstone_axe"),
                        item("hellstone_shovel"),
                        item("hellstone_hoe"),
                        item("shroomstone_sword"),
                        item("shroomstone_pickaxe"),
                        item("shroomstone_axe"),
                        item("shroomstone_shovel"),
                        item("shroomstone_hoe"));
        Set<Item> separatelyModeledBlockItems =
                Set.of(
                        CustomBlocks.GLIMMERSHROOM.get().asItem(),
                        CustomBlocks.GLIMMERGRASS.get().asItem(),
                        CustomBlocks.CHARRED_GRASS.get().asItem(),
                        CustomBlocks.LUMINITE_LANTERN.get().asItem(),
                        CustomItems.SCORCHED_BERRIES.get(),
                        CustomItems.LUMINITE_TORCH.get());

        BuiltInRegistries.ITEM.stream()
                .filter(
                        item ->
                                BuiltInRegistries.ITEM
                                        .getKey(item)
                                        .getNamespace()
                                        .equals(DarkCaverns.MOD_ID))
                .filter(
                        item ->
                                !(item instanceof BlockItem)
                                        || separatelyModeledBlockItems.contains(item))
                .forEach(
                        item -> {
                            if (handheld.contains(item)) {
                                handheldItem(item);
                            } else if (item == CustomBlocks.GLIMMERSHROOM.get().asItem()
                                    || item == CustomBlocks.GLIMMERGRASS.get().asItem()
                                    || item == CustomBlocks.CHARRED_GRASS.get().asItem()
                                    || item == CustomItems.LUMINITE_TORCH.get()) {
                                blockTextureItem(item);
                            } else {
                                basicItem(item);
                            }
                        });
    }

    private void blockTextureItem(Item item) {
        String name = BuiltInRegistries.ITEM.getKey(item).getPath();
        getBuilder(name)
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("block/" + name));
    }

    private static Item item(String path) {
        return BuiltInRegistries.ITEM.get(DarkCaverns.id(path));
    }
}
