package com.freeranger.dark_caverns.gametest;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.registry.CustomEquipment;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(DarkCaverns.MOD_ID)
@PrefixGameTestTemplate(false)
public final class EquipmentEnchantmentTests {
    private EquipmentEnchantmentTests() {}

    @GameTest(template = "sacred_torch")
    public static void equipmentSupportsVanillaBooksAndTableCategories(GameTestHelper helper) {
        var registry = helper.getLevel().registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        int checked = 0;
        for (var item : BuiltInRegistries.ITEM) {
            if (!BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(DarkCaverns.MOD_ID))
                continue;
            if (!(item instanceof DiggerItem
                    || item instanceof SwordItem
                    || item instanceof ArmorItem)) continue;
            checked++;
            var stack = new ItemStack(item);
            for (var key : List.of(Enchantments.UNBREAKING, Enchantments.MENDING)) {
                helper.assertTrue(
                        stack.supportsEnchantment(registry.getHolderOrThrow(key)),
                        "Missing durability book support: " + item + " / " + key);
            }
            var key =
                    item instanceof DiggerItem
                            ? Enchantments.EFFICIENCY
                            : item instanceof SwordItem
                                    ? Enchantments.SHARPNESS
                                    : Enchantments.PROTECTION;
            var enchantment = registry.getHolderOrThrow(key);
            helper.assertTrue(
                    stack.supportsEnchantment(enchantment)
                            && stack.isPrimaryItemFor(enchantment)
                            && stack.isEnchantable(),
                    "Equipment is not eligible for its normal books/table enchantments: " + item);
            if (item instanceof DiggerItem) {
                for (var mining : List.of(Enchantments.FORTUNE, Enchantments.SILK_TOUCH)) {
                    var holder = registry.getHolderOrThrow(mining);
                    helper.assertTrue(
                            stack.supportsEnchantment(holder) && stack.isPrimaryItemFor(holder),
                            "Missing mining enchantment: " + item + " / " + mining);
                }
            }
        }
        helper.assertTrue(checked > 0, "No Dark Caverns equipment was checked");
        var pickaxe = new ItemStack(CustomEquipment.SHROOMSTONE_PICKAXE.get());
        helper.assertTrue(
                !pickaxe.supportsEnchantment(registry.getHolderOrThrow(Enchantments.PROTECTION))
                        && !pickaxe.supportsEnchantment(
                                registry.getHolderOrThrow(Enchantments.SHARPNESS)),
                "Pickaxe gained unrelated armor/weapon enchantments");
        helper.succeed();
    }

    @GameTest(template = "sacred_torch")
    public static void shroomstonePickaxeAcceptsEfficiencyBookInSurvivalAnvil(
            GameTestHelper helper) {
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.experienceLevel = 30;
        var efficiency =
                helper.getLevel()
                        .registryAccess()
                        .registryOrThrow(Registries.ENCHANTMENT)
                        .getHolderOrThrow(Enchantments.EFFICIENCY);
        var book = new ItemStack(Items.ENCHANTED_BOOK);
        book.enchant(efficiency, 4);
        var anvil = new AnvilMenu(0, player.getInventory());
        anvil.getSlot(AnvilMenu.INPUT_SLOT)
                .set(new ItemStack(CustomEquipment.SHROOMSTONE_PICKAXE.get()));
        anvil.getSlot(AnvilMenu.ADDITIONAL_SLOT).set(book);
        anvil.createResult();
        var result = anvil.getSlot(AnvilMenu.RESULT_SLOT);
        helper.assertTrue(
                result.getItem().is(CustomEquipment.SHROOMSTONE_PICKAXE.get())
                        && result.getItem().getEnchantmentLevel(efficiency) == 4
                        && result.mayPickup(player),
                "Survival anvil did not produce an affordable Efficiency IV shroomstone pickaxe");
        helper.succeed();
    }
}
