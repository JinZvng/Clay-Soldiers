package com.matthewperiut.clay.network.payload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.*;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
public record WhistlePayload(int mode, boolean offhand, int team) implements CustomPacketPayload {
 public static final Type<WhistlePayload> ID=new Type<>(Identifier.fromNamespaceAndPath("clay","whistle_order"));
 public static final StreamCodec<RegistryFriendlyByteBuf,WhistlePayload> CODEC=StreamCodec.composite(ByteBufCodecs.VAR_INT,WhistlePayload::mode,ByteBufCodecs.BOOL,WhistlePayload::offhand,ByteBufCodecs.VAR_INT,WhistlePayload::team,WhistlePayload::new);
 public Type<? extends CustomPacketPayload> type(){return ID;}
}
