package org.shsts.tinycorelib.content.network;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.shsts.tinycorelib.api.network.IPacket;

import java.util.function.BiFunction;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class PacketPayloads {
    private PacketPayloads() {}

    public static <P extends IPacket, R extends CustomPacketPayload>
        StreamCodec<RegistryFriendlyByteBuf, R> codec(
            PacketType<P, R> type, Supplier<P> constructor,
            BiFunction<PacketType<P, R>, P, R> payloadFactory) {
        return CustomPacketPayload.codec(
            (payload, buf) -> payloadContent(payload).serializeToBuf(buf),
            buf -> payloadFactory.apply(type, read(constructor, buf)));
    }

    @SuppressWarnings("unchecked")
    public static <P extends IPacket> P payloadContent(CustomPacketPayload payload) {
        return ((GenericPacketPayload<P>) payload).content();
    }

    public static <P extends IPacket> P read(Supplier<P> constructor,
        RegistryFriendlyByteBuf buf) {
        var packet = constructor.get();
        packet.deserializeFromBuf(buf);
        return packet;
    }
}
