package org.shsts.tinycorelib.content.registrate.handler;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.shsts.tinycorelib.api.network.IPacket;
import org.shsts.tinycorelib.api.network.IPacketType;
import org.shsts.tinycorelib.api.network.PacketDirection;
import org.shsts.tinycorelib.content.gui.sync.MenuEventPacket;
import org.shsts.tinycorelib.content.gui.sync.MenuSyncPacket;
import org.shsts.tinycorelib.content.network.GenericPacketPayload;
import org.shsts.tinycorelib.content.network.PacketPayloadType;
import org.shsts.tinycorelib.content.network.PacketPayloads;
import org.shsts.tinycorelib.content.network.PacketType;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PayloadHandler extends EventHandler<PayloadRegistrar> {
    public static final String NETWORK_VERSION = "1";

    public <P extends IPacket> void registerGeneric(
        PacketType<P, GenericPacketPayload<P>> type,
        Supplier<P> constructor, BiConsumer<P, IPayloadContext> handler) {
        addCallback(registrar -> {
            var codec = PacketPayloads.genericCodec(type, constructor);
            IPayloadHandler<GenericPacketPayload<P>> payloadHandler = (payload, context) ->
                handler.accept(payload.content(), context);
            switch (type.direction()) {
                case CLIENTBOUND -> registrar.playToClient(type.type(), codec, payloadHandler);
                case SERVERBOUND -> registrar.playToServer(type.type(), codec, payloadHandler);
                case BIDIRECTIONAL -> registrar.playBidirectional(type.type(), codec, payloadHandler);
            }
        });
    }

    public <P extends IPacket> void registerMenuSync(
        PacketType<P, MenuSyncPacket<P>> type,
        Supplier<P> constructor) {
        addCallback(registrar -> registrar.playToClient(type.type(),
            MenuSyncPacket.codec(type, constructor),
            MenuSyncPacket::handle));
    }

    public <P extends IPacket> void registerMenuEvent(
        PacketType<P, MenuEventPacket<P>> type,
        Supplier<P> constructor) {
        addCallback(registrar -> registrar.playToServer(type.type(),
            MenuEventPacket.codec(type, constructor),
            MenuEventPacket::handle));
    }

    public static <P extends IPacket> PacketType<P, GenericPacketPayload<P>> requireGeneric(
        IPacketType<P> packetType) {
        var type = requireGenericType(packetType);
        if (type.payloadType() != PacketPayloadType.GENERIC) {
            throw new IllegalArgumentException("Packet type %s is not a generic packet".formatted(type.loc()));
        }
        return type;
    }

    @SuppressWarnings("unchecked")
    private static <P extends IPacket> PacketType<P, GenericPacketPayload<P>> requireGenericType(
        IPacketType<P> packetType) {
        if (!(packetType instanceof PacketType<?, ?> type)) {
            throw new IllegalArgumentException("Unsupported packet type %s".formatted(packetType.loc()));
        }
        return (PacketType<P, GenericPacketPayload<P>>) type;
    }

    public static void requireSendToServer(PacketDirection direction) {
        if (direction == PacketDirection.CLIENTBOUND) {
            throw new IllegalArgumentException("Cannot send clientbound packet type to server");
        }
    }

    public static void requireSendToPlayer(PacketDirection direction) {
        if (direction == PacketDirection.SERVERBOUND) {
            throw new IllegalArgumentException("Cannot send serverbound packet type to player");
        }
    }

    public static void requirePayloadType(IPacketType<?> packetType, PacketPayloadType payloadType) {
        if (requireType(packetType).payloadType() != payloadType) {
            throw new IllegalArgumentException("Packet type %s is not %s".formatted(
                packetType.loc(), payloadType));
        }
    }

    @SuppressWarnings("unchecked")
    public static <P extends IPacket, R extends CustomPacketPayload> PacketType<P, R> requireType(
        IPacketType<P> packetType) {
        if (!(packetType instanceof PacketType<?, ?> type)) {
            throw new IllegalArgumentException("Unsupported packet type %s".formatted(packetType.loc()));
        }
        return (PacketType<P, R>) type;
    }

    public void onRegisterPayload(RegisterPayloadHandlersEvent event) {
        onEvent(event.registrar(PayloadHandler.NETWORK_VERSION));
    }
}
