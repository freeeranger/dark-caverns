package com.freeranger.dark_caverns.registry;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class CustomCreativeTabs {
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DARK_CAVERNS =
            ModRegistries.CREATIVE_TABS.register(
                    "dark_caverns",
                    () -> CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup.dark_caverns"))
                            .icon(() -> new ItemStack(CustomBlocks.LUMINITE_ORE.get()))
                            .displayItems((parameters, output) -> ModRegistries.ITEMS.getEntries()
                                    .forEach(item -> output.accept(item.get())))
                            .build()
            );

    private CustomCreativeTabs() {
    }

    public static void bootstrap() {
        // Forces class initialization before the deferred registers attach to the event bus.
    }
}
