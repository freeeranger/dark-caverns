package com.freeranger.dark_caverns.client;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.blockentity.MightyUndersproutsBlockEntity;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public final class MightyUndersproutsRenderer
        extends GeoBlockRenderer<MightyUndersproutsBlockEntity> {
    public MightyUndersproutsRenderer() {
        super(new DefaultedBlockGeoModel<>(DarkCaverns.id("mighty_undersprouts")));
    }

    @Override
    public AABB getRenderBoundingBox(MightyUndersproutsBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos())
                .inflate(1.0, 0.0, 1.0)
                .expandTowards(0.0, 1.0, 0.0);
    }
}
