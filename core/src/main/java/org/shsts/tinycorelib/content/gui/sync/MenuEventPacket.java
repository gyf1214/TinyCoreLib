package org.shsts.tinycorelib.content.gui.sync;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.shsts.tinycorelib.api.gui.IMenuEventHandler;
import org.shsts.tinycorelib.api.network.IPacket;
import org.shsts.tinycorelib.content.network.PacketPayloads;
import org.shsts.tinycorelib.content.network.PacketType;

import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public record MenuEventPacket<P extends IPacket>(
    PacketType<P, ?> packetType,
    int containerId,
    P content
) implements CustomPacketPayload {
    public static <P extends IPacket> StreamCodec<RegistryFriendlyByteBuf, MenuEventPacket<P>>
        codec(PacketType<P, MenuEventPacket<P>> type, Supplier<P> constructor) {
        return CustomPacketPayload.codec(
            (payload, buf) -> payload.write(buf),
            buf -> read(type, constructor, buf));
    }

    private static <P extends IPacket> MenuEventPacket<P> read(
        PacketType<P, MenuEventPacket<P>> type, Supplier<P> constructor,
        RegistryFriendlyByteBuf buf) {
        var containerId = buf.readVarInt();
        var content = PacketPayloads.read(constructor, buf);
        return new MenuEventPacket<>(type, containerId, content);
    }

    private void write(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(containerId);
        content.serializeToBuf(buf);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return packetType.type();
    }

    public void handle(IPayloadContext context) {
        var player = context.player();
        if (player.containerMenu.containerId == containerId &&
            player.containerMenu instanceof IMenuEventHandler menu) {
            menu.handleEventPacket(packetType, content);
        }
    }
}
