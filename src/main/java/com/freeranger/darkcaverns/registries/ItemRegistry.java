package com.freeranger.darkcaverns.registries;

import com.freeranger.darkcaverns.DarkCaverns;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(DarkCaverns.MODID);


    public static final DeferredItem<Item> LUMINITE_DUST = ITEMS.registerSimpleItem("luminite_dust");
    
    public static final DeferredItem<Item> HELLSTONE = ITEMS.registerSimpleItem("hellstone");

    public static final DeferredItem<BlockItem> SCORCHED_BERRIES = ITEMS.registerSimpleBlockItem(
        "scorched_berries",
        BlockRegistry.SCORCHED_BERRY_BUSH,
        new Item.Properties().food(
            new FoodProperties.Builder()
                .nutrition(2)
                .saturationModifier(0.1f)
                .alwaysEdible()
                .build(),
            Consumables.defaultFood()
                .onConsume(new ApplyStatusEffectsConsumeEffect(
                    new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 100, 0),
                    1f
                )).build()
        )
    );


    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
