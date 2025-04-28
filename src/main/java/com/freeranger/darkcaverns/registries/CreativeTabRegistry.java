package com.freeranger.darkcaverns.registries;

import com.freeranger.darkcaverns.DarkCaverns;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

public class CreativeTabRegistry {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DarkCaverns.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DARK_CAVERNS_TAB = registerTab(
        "dark_caverns",
        BlockRegistry.LUMINITE_ORE::asItem,
        List.of(
            // general stuff
            BlockRegistry.CARFSTONE::asItem,
            BlockRegistry.CARFSTONE_SLAB::asItem,
            BlockRegistry.CARFSTONE_STAIRS::asItem,
            BlockRegistry.CARFSTONE_WALL::asItem,
            BlockRegistry.CARFSTONE_BRICKS::asItem,
            BlockRegistry.CARFSTONE_BRICK_SLAB::asItem,
            BlockRegistry.CARFSTONE_BRICK_STAIRS::asItem,
            BlockRegistry.CARFSTONE_BRICK_WALL::asItem,
            BlockRegistry.CHISELED_CARFSTONE_BRICKS::asItem,

            // molten layer stuff
            BlockRegistry.MOLTEN_CARFSTONE::asItem,
            BlockRegistry.MOLTEN_CARFSTONE_SLAB::asItem,
            BlockRegistry.MOLTEN_CARFSTONE_STAIRS::asItem,
            BlockRegistry.MOLTEN_CARFSTONE_WALL::asItem,
            BlockRegistry.ASHY_MOLTEN_CARFSTONE::asItem,
            BlockRegistry.MOLTEN_CARFSTONE_BRICKS::asItem,
            BlockRegistry.MOLTEN_CARFSTONE_BRICK_SLAB::asItem,
            BlockRegistry.MOLTEN_CARFSTONE_BRICK_STAIRS::asItem,
            BlockRegistry.MOLTEN_CARFSTONE_BRICK_WALL::asItem,
            BlockRegistry.CHISELED_MOLTEN_CARFSTONE_BRICKS::asItem,
            BlockRegistry.CHARRED_GRASS::asItem,
            BlockRegistry.ASHY_CHARRED_GRASS::asItem,
            ItemRegistry.SCORCHED_BERRIES::asItem,
            BlockRegistry.HELLSTONE_ORE::asItem,
            ItemRegistry.HELLSTONE,

            // jungle stuff
            BlockRegistry.OVERGROWN_CARFSTONE::asItem,
            BlockRegistry.UNDERSPROUTS::asItem,
            BlockRegistry.TWISTWOOD_LOG::asItem,
            BlockRegistry.TWISTWOOD_WOOD::asItem,
            BlockRegistry.STRIPPED_TWISTWOOD_LOG::asItem,
            BlockRegistry.STRIPPED_TWISTWOOD_WOOD::asItem,
            BlockRegistry.TWISTWOOD_PLANKS::asItem,
            BlockRegistry.TWISTWOOD_DOOR::asItem,
            BlockRegistry.TWISTWOOD_TRAPDOOR::asItem,
            BlockRegistry.TWISTWOOD_LEAVES::asItem,

            // ores and valuables
            BlockRegistry.CARFSTONE_IRON_ORE::asItem,
            BlockRegistry.CARFSTONE_GOLD_ORE::asItem,
            BlockRegistry.CARFSTONE_DIAMOND_ORE::asItem,
            BlockRegistry.LUMINITE_ORE::asItem,
            BlockRegistry.LUMINITE_BLOCK::asItem,
            ItemRegistry.LUMINITE_DUST
        )
    );

    private static DeferredHolder<CreativeModeTab, CreativeModeTab> registerTab(String name, Supplier<Item> icon, List<Supplier<Item>> items) {
        return CREATIVE_MODE_TABS.register(name, () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + DarkCaverns.MODID + "." + name))
            .icon(() -> icon.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                for(Supplier<Item> item : items) {
                    output.accept(item.get());
                }
            }).build());
    }

    public static void register(IEventBus bus) {
        CREATIVE_MODE_TABS.register(bus);
    }
}
