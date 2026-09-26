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
        tag(ItemTags.BOATS).add(CustomItems.TWISTWOOD_BOAT.get());
        tag(ItemTags.CHEST_BOATS).add(CustomItems.TWISTWOOD_CHEST_BOAT.get());
    }

    private void copyBlockTags() {
        copy(BlockTags.LOGS, ItemTags.LOGS);
        copy(BlockTags.LOGS_THAT_BURN, ItemTags.LOGS_THAT_BURN);
        copy(BlockTags.PLANKS, ItemTags.PLANKS);
        copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
        copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
        copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
        copy(BlockTags.FENCE_GATES, ItemTags.FENCE_GATES);
        copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
        copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
        copy(BlockTags.LEAVES, ItemTags.LEAVES);
        copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
        copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
        copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
        copy(BlockTags.STANDING_SIGNS, ItemTags.SIGNS);
        copy(BlockTags.CEILING_HANGING_SIGNS, ItemTags.HANGING_SIGNS);
        for (String path :
                new String[] {
                    "cobblestone",
                    "mushrooms",
                    "stone",
                    "ores",
                    "ores/coal",
                    "ores/iron",
                    "ores/copper",
                    "ores/gold",
                    "ores/diamond",
                    "ores/redstone",
                    "ores/lapis",
                    "ores/platinum",
                    "ores/luminite",
                    "ores/hellstone",
                    "storage_blocks",
                    "storage_blocks/platinum",
                    "storage_blocks/raw_platinum",
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

        tag(common("raw_materials")).addTag(common("raw_materials/platinum"));
        tag(common("raw_materials/platinum")).add(CustomItems.RAW_PLATINUM.get());

        tag(common("ingots")).addTag(common("ingots/platinum"));
        tag(common("ingots/platinum")).add(CustomItems.PLATINUM_INGOT.get());

        tag(ItemTags.BEACON_PAYMENT_ITEMS)
                .add(
                        CustomItems.PLATINUM_INGOT.get(),
                        CustomItems.HELLSTONE.get(),
                        CustomItems.SHROOMSTONE.get());
    }

    private void addEquipment() {
        addArmorNamespace("armor");
        tag(common("armors")).addTag(common("armor"));
        for (String slot : new String[] {"helmets", "chestplates", "leggings", "boots"}) {
            tag(common("armors/" + slot)).addTag(common("armor/" + slot));
        }

        tag(common("tools"))
                .addTag(common("tools/swords"))
                .addTag(common("tools/pickaxes"))
                .addTag(common("tools/axes"))
                .addTag(common("tools/shovels"))
                .addTag(common("tools/hoes"));
        equipment(
                ItemTags.SWORDS,
                "tools/swords",
                CustomEquipment.PLATINUM_SWORD.get(),
                CustomEquipment.HELLSTONE_SWORD.get(),
                CustomEquipment.SHROOMSTONE_SWORD.get());
        equipment(
                ItemTags.PICKAXES,
                "tools/pickaxes",
                CustomEquipment.PLATINUM_PICKAXE.get(),
                CustomEquipment.HELLSTONE_PICKAXE.get(),
                CustomEquipment.SHROOMSTONE_PICKAXE.get());
        equipment(
                ItemTags.AXES,
                "tools/axes",
                CustomEquipment.PLATINUM_AXE.get(),
                CustomEquipment.HELLSTONE_AXE.get(),
                CustomEquipment.SHROOMSTONE_AXE.get());
        equipment(
                ItemTags.SHOVELS,
                "tools/shovels",
                CustomEquipment.PLATINUM_SHOVEL.get(),
                CustomEquipment.HELLSTONE_SHOVEL.get(),
                CustomEquipment.SHROOMSTONE_SHOVEL.get());
        equipment(
                ItemTags.HOES,
                "tools/hoes",
                CustomEquipment.PLATINUM_HOE.get(),
                CustomEquipment.HELLSTONE_HOE.get(),
                CustomEquipment.SHROOMSTONE_HOE.get());
    }

    private void equipment(TagKey<Item> vanilla, String compatibility, Item... items) {
        // Vanilla's enchantable tags inherit these categories, not the old c:tools/* tags.
        tag(vanilla).add(items);
        tag(common(compatibility)).add(items);
    }

    private void addArmorNamespace(String root) {
        tag(common(root))
                .addTag(common(root + "/helmets"))
                .addTag(common(root + "/chestplates"))
                .addTag(common(root + "/leggings"))
                .addTag(common(root + "/boots"));
        equipment(
                ItemTags.HEAD_ARMOR,
                root + "/helmets",
                CustomEquipment.PLATINUM_HELMET.get(),
                CustomEquipment.HELLSTONE_HELMET.get(),
                CustomEquipment.SHROOMSTONE_HELMET.get(),
                CustomEquipment.LUMINITE_HELMET.get());
        equipment(
                ItemTags.CHEST_ARMOR,
                root + "/chestplates",
                CustomEquipment.PLATINUM_CHESTPLATE.get(),
                CustomEquipment.HELLSTONE_CHESTPLATE.get(),
                CustomEquipment.SHROOMSTONE_CHESTPLATE.get());
        equipment(
                ItemTags.LEG_ARMOR,
                root + "/leggings",
                CustomEquipment.PLATINUM_LEGGINGS.get(),
                CustomEquipment.HELLSTONE_LEGGINGS.get(),
                CustomEquipment.SHROOMSTONE_LEGGINGS.get());
        equipment(
                ItemTags.FOOT_ARMOR,
                root + "/boots",
                CustomEquipment.PLATINUM_BOOTS.get(),
                CustomEquipment.HELLSTONE_BOOTS.get(),
                CustomEquipment.SHROOMSTONE_BOOTS.get());
    }

    private static TagKey<Item> common(String path) {
        return DarkCavernsTags.commonItem(path);
    }
}
