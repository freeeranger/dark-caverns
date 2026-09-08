package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.DarkCaverns;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

/** Public gameplay-facing block tags. Datagen-only tags remain in the datagen package. */
public final class CustomBlockTags {
    public static final TagKey<Block> ROCKY_CREATURE_SPAWNABLE_ON =
            create("rocky_creature_spawnable_on");
    public static final TagKey<Block> MOLTEN_CREATURE_SPAWNABLE_ON =
            create("molten_creature_spawnable_on");
    public static final TagKey<Block> GLIMMERSHROOM_CREATURE_SPAWNABLE_ON =
            create("glimmershroom_creature_spawnable_on");

    private CustomBlockTags() {}

    private static TagKey<Block> create(String path) {
        return TagKey.create(Registries.BLOCK, DarkCaverns.id(path));
    }
}
