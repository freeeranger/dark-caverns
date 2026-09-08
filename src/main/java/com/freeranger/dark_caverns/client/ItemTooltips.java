package com.freeranger.dark_caverns.client;

import com.freeranger.dark_caverns.DarkCaverns;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

/** Adds concise gameplay explanations to items whose behavior is not visible from their stats. */
public final class ItemTooltips {
    private ItemTooltips() {}

    public static void register(IEventBus gameBus) {
        gameBus.addListener(ItemTooltips::onTooltip);
    }

    private static void onTooltip(ItemTooltipEvent event) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(event.getItemStack().getItem());
        if (!id.getNamespace().equals(DarkCaverns.MOD_ID)) {
            return;
        }

        String path = id.getPath();
        String tooltip = tooltipFor(path);
        if (tooltip != null) {
            event.getToolTip()
                    .add(
                            Component.translatable("tooltip.dark_caverns." + tooltip)
                                    .withStyle(ChatFormatting.GRAY));
        }

        String setTooltip = setTooltipFor(path);
        if (setTooltip != null) {
            event.getToolTip()
                    .add(
                            Component.translatable("tooltip.dark_caverns." + setTooltip)
                                    .withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    private static String tooltipFor(String path) {
        return switch (path) {
            case "key_to_the_caverns" -> "key_to_the_caverns";
            case "luminite_dust" -> "luminite_dust";
            case "platinum_piece" -> "platinum_piece";
            case "scorched_berries" -> "scorched_berries";
            case "throwable_luminite_torch" -> "throwable_luminite_torch";
            case "shroombomb" -> "shroombomb";
            case "corrupted_pearl" -> "corrupted_pearl";
            case "luminite_helmet" -> "luminite_helmet";
            default -> {
                if (path.startsWith("hellstone_") && isTool(path)) {
                    yield "hellstone_tool";
                }
                if (path.startsWith("shroomstone_") && isTool(path)) {
                    yield "shroomstone_tool";
                }
                if (path.startsWith("hellstone_") && isArmor(path)) {
                    yield "hellstone_armor";
                }
                if (path.startsWith("shroomstone_") && isArmor(path)) {
                    yield "shroomstone_armor";
                }
                if (path.startsWith("scorchsteel_") && isArmor(path)) {
                    yield "scorchsteel_armor";
                }
                yield null;
            }
        };
    }

    private static String setTooltipFor(String path) {
        if (path.startsWith("shroomstone_") && isArmor(path)) {
            return "shroomstone_armor_set";
        }
        if (path.startsWith("scorchsteel_") && isArmor(path)) {
            return "scorchsteel_armor_set";
        }
        return null;
    }

    private static boolean isTool(String path) {
        return path.endsWith("_sword")
                || path.endsWith("_axe")
                || path.endsWith("_pickaxe")
                || path.endsWith("_shovel")
                || path.endsWith("_hoe");
    }

    private static boolean isArmor(String path) {
        return path.endsWith("_helmet")
                || path.endsWith("_chestplate")
                || path.endsWith("_leggings")
                || path.endsWith("_boots");
    }
}
