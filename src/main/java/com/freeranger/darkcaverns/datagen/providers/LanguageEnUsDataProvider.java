package com.freeranger.darkcaverns.datagen.providers;

import com.freeranger.darkcaverns.DarkCaverns;
import com.freeranger.darkcaverns.registries.BlockRegistry;
import com.freeranger.darkcaverns.registries.ItemRegistry;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class LanguageEnUsDataProvider extends LanguageProvider {
    public LanguageEnUsDataProvider(PackOutput output) {
        super(output, DarkCaverns.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        this.add("itemGroup.darkcaverns.dark_caverns", "Dark Caverns");

        // carfstone stuff
        this.add(BlockRegistry.CARFSTONE.get(), "Carfstone");
        this.add(BlockRegistry.CARFSTONE_SLAB.get(), "Carfstone Slab");
        this.add(BlockRegistry.CARFSTONE_STAIRS.get(), "Carfstone Stairs");
        this.add(BlockRegistry.CARFSTONE_WALL.get(), "Carfstone Wall");
        this.add(BlockRegistry.CARFSTONE_BRICKS.get(), "Carfstone Bricks");
        this.add(BlockRegistry.CARFSTONE_BRICK_SLAB.get(), "Carfstone Brick Slab");
        this.add(BlockRegistry.CARFSTONE_BRICK_STAIRS.get(), "Carfstone Brick Stairs");
        this.add(BlockRegistry.CARFSTONE_BRICK_WALL.get(), "Carfstone Brick Wall");
        this.add(BlockRegistry.CHISELED_CARFSTONE_BRICKS.get(), "Chiseled Carfstone Bricks");

        // molten stuff
        this.add(BlockRegistry.MOLTEN_CARFSTONE.get(), "Molten Carfstone");
        this.add(BlockRegistry.MOLTEN_CARFSTONE_SLAB.get(), "Molten Carfstone Slab");
        this.add(BlockRegistry.MOLTEN_CARFSTONE_STAIRS.get(), "Molten Carfstone Stairs");
        this.add(BlockRegistry.MOLTEN_CARFSTONE_WALL.get(), "Molten Carfstone Wall");
        this.add(BlockRegistry.ASHY_MOLTEN_CARFSTONE.get(), "Ashy Molten Carfstone");
        this.add(BlockRegistry.MOLTEN_CARFSTONE_BRICKS.get(), "Molten Carfstone Bricks");
        this.add(BlockRegistry.MOLTEN_CARFSTONE_BRICK_SLAB.get(), "Molten Carfstone Brick Slab");
        this.add(BlockRegistry.MOLTEN_CARFSTONE_BRICK_STAIRS.get(), "Molten Carfstone Brick Stairs");
        this.add(BlockRegistry.MOLTEN_CARFSTONE_BRICK_WALL.get(), "Molten Carfstone Brick Wall");
        this.add(BlockRegistry.CHISELED_MOLTEN_CARFSTONE_BRICKS.get(), "Chiseled Molten Carfstone Bricks");
        this.add(BlockRegistry.CHARRED_GRASS.get(), "Charred Grass");
        this.add(BlockRegistry.ASHY_CHARRED_GRASS.get(), "Ashy Charred Grass");

        // tangled hallow stuff
        this.add(BlockRegistry.OVERGROWN_CARFSTONE.get(), "Overgrown Carfstone");
        this.add(BlockRegistry.UNDERSPROUTS.get(), "Undersprouts");
        this.add(BlockRegistry.TWISTWOOD_LOG.get(), "Twistwood Log");
        this.add(BlockRegistry.STRIPPED_TWISTWOOD_LOG.get(), "Stripped Twistwood Log");
        this.add(BlockRegistry.TWISTWOOD_PLANKS.get(), "Twistwood Planks");
        this.add(BlockRegistry.TWISTWOOD_WOOD.get(), "Twistwood Wood");
        this.add(BlockRegistry.TWISTWOOD_DOOR.get(), "Twistwood Door");
        this.add(BlockRegistry.TWISTWOOD_TRAPDOOR.get(), "Twistwood Trapdoor");
        this.add(BlockRegistry.STRIPPED_TWISTWOOD_WOOD.get(), "Stripped Twistwood Wood");
        this.add(BlockRegistry.TWISTWOOD_LEAVES.get(), "Twistwood Leaves");

        // valuable stuff
        this.add(BlockRegistry.CARFSTONE_IRON_ORE.get(), "Carfstone Iron Ore");
        this.add(BlockRegistry.CARFSTONE_GOLD_ORE.get(), "Carfstone Gold Ore");
        this.add(BlockRegistry.CARFSTONE_DIAMOND_ORE.get(), "Carfstone Diamond Ore");
        this.add(BlockRegistry.LUMINITE_ORE.get(), "Luminite Ore");
        this.add(BlockRegistry.LUMINITE_BLOCK.get(), "Block of Luminite");
        this.add(BlockRegistry.HELLSTONE_ORE.get(), "Hellstone Ore");

        // items
        this.add(ItemRegistry.LUMINITE_DUST.get(), "Luminite Dust");
        this.add(ItemRegistry.HELLSTONE.get(), "Hellstone");

        this.add("block.darkcaverns.scorched_berries", "Scorched Berries");
    }
}
