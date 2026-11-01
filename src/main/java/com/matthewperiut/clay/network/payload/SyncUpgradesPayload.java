package com.matthewperiut.clay.network.payload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import com.matthewperiut.clay.network.ClayNetworkingConstants;
import com.matthewperiut.clay.registry.UpgradeRegistry;
import com.matthewperiut.clay.upgrade.ISoldierUpgrade;
import com.matthewperiut.clay.upgrade.UpgradeManager;
import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public record SyncUpgradesPayload(int entityId, Action action, List<ISoldierUpgrade> upgrades) implements CustomPacketPayload {
    public enum Action {
        ADD,
        REMOVE,
        SET
    }

    public static final StreamCodec<ByteBuf, Action> ACTION_CODEC = new StreamCodec<ByteBuf, Action>() {

        @Override
        public void encode(ByteBuf buf, Action value) {
            buf.writeInt(value.ordinal());
        }

        @Override
        public Action decode(ByteBuf buf) {
            return Action.values()[buf.readInt()];
        }
    };

    public static final StreamCodec<ByteBuf, List<ISoldierUpgrade>> UPGRADES_CODEC = new StreamCodec<ByteBuf, List<ISoldierUpgrade>>() {
        @Override
        public void encode(ByteBuf buf, List<ISoldierUpgrade> upgrades) {
            buf.writeInt(upgrades.size());
            for (ISoldierUpgrade upgrade : upgrades) {
                String id = upgrade.getUpgradeIdentifier().toString();
                byte[] identifierBytes = id.getBytes(StandardCharsets.UTF_8);
                buf.writeInt(identifierBytes.length);
                buf.writeBytes(identifierBytes);
            }
        }

        @Override
        public List<ISoldierUpgrade> decode(ByteBuf byteBuf) {
            int size = byteBuf.readInt(); // Read the size of the list
            List<ISoldierUpgrade> upgrades = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                int identifierLength = byteBuf.readInt();
                byte[] identifierBytes = new byte[identifierLength];
                byteBuf.readBytes(identifierBytes);
                String identifierStr = new String(identifierBytes, StandardCharsets.UTF_8);
                Identifier id = Identifier.parse(identifierStr);
                ISoldierUpgrade upgrade = UpgradeManager.INSTANCE.getUpgrade(id);
                if (upgrade != null) {
                    upgrades.add(upgrade);
                }
            }
            return upgrades;
        }
    };

    public static final CustomPacketPayload.Type<SyncUpgradesPayload> ID = new CustomPacketPayload.Type<>(ClayNetworkingConstants.SYNC_UPGRADES_PACKET_ID);
     public static final StreamCodec<RegistryFriendlyByteBuf, SyncUpgradesPayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, SyncUpgradesPayload::entityId,
        ACTION_CODEC, SyncUpgradesPayload::action,
        UPGRADES_CODEC, SyncUpgradesPayload::upgrades,
        SyncUpgradesPayload::new
     );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}