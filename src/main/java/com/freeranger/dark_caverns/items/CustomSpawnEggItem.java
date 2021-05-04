package com.freeranger.dark_caverns.items;

import com.freeranger.dark_caverns.registry.CustomItemGroups;
import net.minecraft.entity.EntityType;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CustomSpawnEggItem extends SpawnEggItem {
    protected static final List<CustomSpawnEggItem> UNADDED_EGGS = new ArrayList<>();
    private final Lazy<? extends EntityType<?>> entityTypeSupplier;

    public CustomSpawnEggItem(final RegistryObject<? extends EntityType<?>> entityTypeSupplier, int egg, int spots) {
        super(null, egg, spots, new Properties()
                .tab(CustomItemGroups.GROUP)
        );
        this.entityTypeSupplier = Lazy.of(entityTypeSupplier);
        UNADDED_EGGS.add(this);
    }

    @Override
    public EntityType<?> getType(@Nullable CompoundNBT p_208076_1_)
    {
        return this.entityTypeSupplier.get();
    }
}
