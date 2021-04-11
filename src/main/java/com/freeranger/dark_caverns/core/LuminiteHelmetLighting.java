package com.freeranger.dark_caverns.core;

import com.freeranger.dark_caverns.registry.CustomItems;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@OnlyIn(Dist.CLIENT)
public class LuminiteHelmetLighting {
    private static final Minecraft MC = Minecraft.getInstance();
    public static final Map<BlockPos, LightData> SOURCES = new ConcurrentHashMap<>();

    public static void tick(LivingEntity entity)
    {
        if (entity != null && MC.player != null)
        {
            if (shouldGlow(entity))
                SOURCES.put(entity.blockPosition().above((int) entity.getEyeHeight()), new LightData());

            if (entity == MC.player)
            {
                SOURCES.forEach((blockPos, data) -> {
                    if (MC.level != null) {
                        MC.level.getChunkSource().getLightEngine().checkBlock(blockPos);
                    }
                });
                SOURCES.entrySet().removeIf(entry -> !entry.getValue().shouldStay);
                SOURCES.forEach((blockPos, data) -> data.shouldStay = false);
            }
        }
    }

    public static boolean shouldGlow(LivingEntity entity)
    {
        int maxDist = 64;
        int dist = 0;
        if (MC.player != null) {
            dist = (int) MC.player.distanceTo(entity);
        }

        Vector3d playerPos = new Vector3d(MC.player.position().x, MC.player.getEyeY(), MC.player.position().z);
        Vector3d entityPos = new Vector3d(entity.position().x, entity.getEyeY(),entity.position().z);
        boolean visible = MC.player.level.clip(new RayTraceContext(playerPos, entityPos, RayTraceContext.BlockMode.VISUAL, RayTraceContext.FluidMode.NONE, entity)).getType() == RayTraceResult.Type.MISS;

        return entity.getItemBySlot(EquipmentSlotType.HEAD).getItem() == CustomItems.LUMINITE_HELMET.get() && dist <= maxDist && (visible || dist < 24);
    }

    public static void cleanUp()
    {
        if (SOURCES.size() > 0 && MC.level != null)
        {
            SOURCES.forEach((blockPos, data) ->
            {
                data.shouldStay = false;
                MC.level.getChunkSource().getLightEngine().checkBlock(blockPos);
            });
            SOURCES.clear();
        }
    }

    public static class LightData
    {
        public boolean shouldStay = true;
    }

}
