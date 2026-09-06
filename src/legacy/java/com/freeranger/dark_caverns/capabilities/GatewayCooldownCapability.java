package com.freeranger.dark_caverns.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

public class GatewayCooldownCapability {

    @CapabilityInject(IGatewayCooldownCapability.class)
    public static Capability<IGatewayCooldownCapability> GATEWAY_COOLDOWN_CAPABILITY = null;

    public static void register(){
        CapabilityManager.INSTANCE.register(IGatewayCooldownCapability.class, new Storage(),  DefaultGatewayCooldown::new);
    }

    public static class Storage implements Capability.IStorage<IGatewayCooldownCapability> {
        @Nullable
        @Override
        public INBT writeNBT(Capability<IGatewayCooldownCapability> capability, IGatewayCooldownCapability instance, Direction side) {
            CompoundNBT tag = new CompoundNBT();
            tag.putInt("gatewayCooldown", instance.getCooldown());
            return tag;
        }

        @Override
        public void readNBT(Capability<IGatewayCooldownCapability> capability, IGatewayCooldownCapability instance, Direction side, INBT nbt) {
            int gatewayCooldown = ((CompoundNBT) nbt).getInt("gatewayCooldown");
            instance.setCooldown(gatewayCooldown);
        }
    }
}
