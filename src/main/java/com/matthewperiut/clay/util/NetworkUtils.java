package com.matthewperiut.clay.util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import io.netty.buffer.Unpooled;

public class HttpUtil {
    private HttpUtil() {
    }

    public static Iterable<ServerPlayer> getTrackingPlayers(Entity entity) {
        return ((ServerChunkCache) entity.level().getChunkSource()).chunkMap.getPlayersWatchingChunk(entity.getChunkPos(), false);
    }

    public static FriendlyByteBuf getBufferForUpgrades(int entityId, Identifier upgradeIdentifier) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        buffer.writeInt(entityId);
        buffer.writeResourceLocation(upgradeIdentifier);
        return buffer;
    }
}
