package com.freeranger.dark_caverns.capabilities;

public interface IGatewayCooldownCapability {
    void setCooldown(int cooldown);
    int tickCooldown();
    int getCooldown();
}
