package com.freeranger.dark_caverns.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GatewayCooldownProvider implements ICapabilitySerializable<CompoundNBT> {
    private final DefaultGatewayCooldown cooldown = new DefaultGatewayCooldown();
    private final LazyOptional<IGatewayCooldownCapability> cooldownOptional = LazyOptional.of(() -> cooldown);

    public void invalidate(){
        cooldownOptional.invalidate();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == GatewayCooldownCapability.GATEWAY_COOLDOWN_CAPABILITY ? cooldownOptional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        if(GatewayCooldownCapability.GATEWAY_COOLDOWN_CAPABILITY == null){
            return new CompoundNBT();
        }else{
            return (CompoundNBT) GatewayCooldownCapability.GATEWAY_COOLDOWN_CAPABILITY.writeNBT(cooldown, null);
        }
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if(GatewayCooldownCapability.GATEWAY_COOLDOWN_CAPABILITY != null){
            GatewayCooldownCapability.GATEWAY_COOLDOWN_CAPABILITY.readNBT(cooldown, null, nbt);
        }
    }
}
