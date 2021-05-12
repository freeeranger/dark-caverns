package com.freeranger.dark_caverns.capabilities;

public class DefaultGatewayCooldown implements IGatewayCooldownCapability {

    private int gatewayCooldown;

    @Override
    public void setCooldown(int cooldown) {
        gatewayCooldown = cooldown;
    }

    @Override
    public int tickCooldown() {
        return --gatewayCooldown;
    }

    @Override
    public int getCooldown() {
        return gatewayCooldown;
    }
}
