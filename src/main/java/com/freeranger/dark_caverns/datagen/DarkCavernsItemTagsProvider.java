package com.freeranger.dark_caverns.datagen;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.registry.CustomEquipment;
import com.freeranger.dark_caverns.registry.CustomItems;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

final class DarkCavernsItemTagsProvider extends ItemTagsProvider {
    DarkCavernsItemTagsProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            CompletableFuture<TagsProvider.TagLookup<Block>> blockTags,
            ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, DarkCaverns.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        copyBlockTags();
        addMaterials();
        addEquipment();
    }

    private void copyBlockTags() {
        for (String path :
                new String[] {
                    "cobblestone",
                    "mushrooms",
                    "stone",
                    "ores",
                    "ores/coal",
                    "ores/iron",
                    "ores/gold",
                    "ores/diamond",
                    "ores/redstone",
                    "ores/lapis",
                    "ores/platinum",
                    "ores/luminite",
                    "ores/hellstone",
                    "storage_blocks",
                    "storage_blocks/platinum",
                    "storage_blocks/luminite",
                    "storage_blocks/hellstone",
                    "storage_blocks/shroomstone"
                }) {
            copy(DarkCavernsTags.commonBlock(path), common(path));
        }
        copy(BlockTags.SLABS, ItemTags.SLABS);
        copy(BlockTags.STAIRS, ItemTags.STAIRS);
        copy(BlockTags.WALLS, ItemTags.WALLS);
        copy(BlockTags.GUARDED_BY_PIGLINS, ItemTags.PIGLIN_LOVED);
    }

    private void addMaterials() {
        tag(common("berries")).add(CustomItems.SCORCHED_BERRIES.get());
        tag(common("cooked_meat")).add(CustomItems.SCORCHED_MEAT.get());

        tag(common("dusts")).addTag(common("dusts/luminite"));
        tag(common("dusts/luminite")).add(CustomItems.LUMINITE_DUST.get());

        tag(common("gems")).addTag(common("gems/hellstone")).addTag(common("gems/shroomstone"));
        tag(common("gems/hellstone")).add(CustomItems.HELLSTONE.get());
        tag(common("gems/shroomstone")).add(CustomItems.SHROOMSTONE.get());

        tag(common("ingots"))
                .addTag(common("ingots/platinum"))
                .addTag(common("ingots/scorchsteel"));
        tag(common("ingots/platinum")).add(CustomItems.PLATINUM_INGOT.get());
        tag(common("ingots/scorchsteel")).add(CustomItems.SCORCHSTEEL_INGOT.get());

        tag(common("nuggets")).addTag(common("nuggets/platinum"));
        tag(common("nuggets/platinum")).add(CustomItems.PLATINUM_PIECE.get());

        tag(ItemTags.BEACON_PAYMENT_ITEMS)
                .add(
                        CustomItems.PLATINUM_INGOT.get(),
                        CustomItems.HELLSTONE.get(),
                        CustomItems.SCORCHSTEEL_INGOT.get(),
                        CustomItems.SHROOMSTONE.get());
    }

    private void addEquipment() {
        addArmorNamespace("armor");
        addArmorNamespace("armors");

        tag(common("tools"))
                .addTag(common("tools/swords"))
                .addTag(common("tools/pickaxes"))
                .addTag(common("tools/axes"))
                .addTag(common("tools/shovels"))
                .addTag(common("tools/hoes"));
        tag(common("tools/swords"))
                .add(
                        CustomEquipment.PLATINUM_SWORD.get(),
                        CustomEquipment.HELLSTONE_SWORD.get(),
                        CustomEquipment.SHROOMSTONE_SWORD.get());
        tag(common("tools/pickaxes"))
                .add(
                        CustomEquipment.PLATINUM_PICKAXE.get(),
                        CustomEquipment.HELLSTONE_PICKAXE.get(),
                        CustomEquipment.SHROOMSTONE_PICKAXE.get());
        tag(common("tools/axes"))
                .add(
                        CustomEquipment.PLATINUM_AXE.get(),
                        CustomEquipment.HELLSTONE_AXE.get(),
                        CustomEquipment.SHROOMSTONE_AXE.get());
        tag(common("tools/shovels"))
                .add(
                        CustomEquipment.PLATINUM_SHOVEL.get(),
                        CustomEquipment.HELLSTONE_SHOVEL.get(),
                        CustomEquipment.SHROOMSTONE_SHOVEL.get());
        tag(common("tools/hoes"))
                .add(
                        CustomEquipment.PLATINUM_HOE.get(),
                        CustomEquipment.HELLSTONE_HOE.get(),
                        CustomEquipment.SHROOMSTONE_HOE.get());
    }

    private void addArmorNamespace(String root) {
        tag(common(root))
                .addTag(common(root + "/helmets"))
                .addTag(common(root + "/chestplates"))
                .addTag(common(root + "/leggings"))
                .addTag(common(root + "/boots"));
        tag(common(root + "/helmets"))
                .add(
                        CustomEquipment.PLATINUM_HELMET.get(),
                        CustomEquipment.HELLSTONE_HELMET.get(),
                        CustomEquipment.SCORCHSTEEL_HELMET.get(),
                        CustomEquipment.SHROOMSTONE_HELMET.get(),
                        CustomEquipment.LUMINITE_HELMET.get());
        tag(common(root + "/chestplates"))
                .add(
                        CustomEquipment.PLATINUM_CHESTPLATE.get(),
                        CustomEquipment.HELLSTONE_CHESTPLATE.get(),
                        CustomEquipment.SCORCHSTEEL_CHESTPLATE.get(),
                        CustomEquipment.SHROOMSTONE_CHESTPLATE.get());
        tag(common(root + "/leggings"))
                .add(
                        CustomEquipment.PLATINUM_LEGGINGS.get(),
                        CustomEquipment.HELLSTONE_LEGGINGS.get(),
                        CustomEquipment.SCORCHSTEEL_LEGGINGS.get(),
                        CustomEquipment.SHROOMSTONE_LEGGINGS.get());
        tag(common(root + "/boots"))
                .add(
                        CustomEquipment.PLATINUM_BOOTS.get(),
                        CustomEquipment.HELLSTONE_BOOTS.get(),
                        CustomEquipment.SCORCHSTEEL_BOOTS.get(),
                        CustomEquipment.SHROOMSTONE_BOOTS.get());
    }

    private static TagKey<Item> common(String path) {
        return DarkCavernsTags.commonItem(path);
    }
}
