package org.shsts.tinycorelib.content.network;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.shsts.tinycorelib.api.network.IPacket;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public record GenericPacketPayload<P extends IPacket>(
    PacketType<P, ?> packetType,
    P content
) implements CustomPacketPayload {
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return packetType.type();
    }
}
