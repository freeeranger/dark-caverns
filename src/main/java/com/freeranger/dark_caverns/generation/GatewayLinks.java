package com.freeranger.dark_caverns.generation;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;

/** Stored centrally so offset cave entrances always lead back to their original key gateway. */
public final class GatewayLinks extends SavedData {
    public record Origin(ResourceKey<Level> dimension, BlockPos pos) {}

    public record Link(Origin origin, BlockPos gateway, BlockPos feet, Direction facing) {}

    private final Map<Origin, Link> outward = new HashMap<>();
    private final Map<BlockPos, Link> homeward = new HashMap<>();

    public static GatewayLinks get(MinecraftServer server) {
        return server.overworld()
                .getDataStorage()
                .computeIfAbsent(
                        new Factory<>(GatewayLinks::new, GatewayLinks::load),
                        "dark_caverns_gateways");
    }

    @Nullable public Link from(Origin origin) {
        return outward.get(origin);
    }

    @Nullable public Link home(BlockPos gateway) {
        return homeward.get(gateway);
    }

    public void put(Link link) {
        Link previous = outward.remove(link.origin());
        if (previous != null) homeward.remove(previous.gateway());
        Link occupied = homeward.remove(link.gateway());
        if (occupied != null) outward.remove(occupied.origin());
        outward.put(link.origin(), link);
        homeward.put(link.gateway(), link);
        setDirty();
    }

    public static GatewayLinks load(CompoundTag tag, HolderLookup.Provider registries) {
        var data = new GatewayLinks();
        ListTag list = tag.getList("Links", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            ResourceLocation dimension = ResourceLocation.tryParse(entry.getString("Dimension"));
            if (dimension == null) continue;
            Direction facing = Direction.from3DDataValue(entry.getInt("Facing"));
            if (!facing.getAxis().isHorizontal()) continue;
            data.put(
                    new Link(
                            new Origin(
                                    ResourceKey.create(Registries.DIMENSION, dimension),
                                    BlockPos.of(entry.getLong("Origin"))),
                            BlockPos.of(entry.getLong("Gateway")),
                            BlockPos.of(entry.getLong("Feet")),
                            facing));
        }
        data.setDirty(false);
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        var list = new ListTag();
        outward.values()
                .forEach(
                        link -> {
                            var entry = new CompoundTag();
                            entry.putString(
                                    "Dimension", link.origin().dimension().location().toString());
                            entry.putLong("Origin", link.origin().pos().asLong());
                            entry.putLong("Gateway", link.gateway().asLong());
                            entry.putLong("Feet", link.feet().asLong());
                            entry.putInt("Facing", link.facing().get3DDataValue());
                            list.add(entry);
                        });
        tag.put("Links", list);
        return tag;
    }
}
