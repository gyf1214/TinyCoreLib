package org.shsts.tinycorelib.content.network;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.shsts.tinycorelib.api.network.IPacket;
import org.shsts.tinycorelib.api.network.IPacketType;
import org.shsts.tinycorelib.api.network.PacketDirection;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public record PacketType<P extends IPacket, R extends CustomPacketPayload>(
    ResourceLocation loc,
    PacketDirection direction,
    PacketPayloadType payloadType,
    CustomPacketPayload.Type<R> type
) implements IPacketType<P> {}
