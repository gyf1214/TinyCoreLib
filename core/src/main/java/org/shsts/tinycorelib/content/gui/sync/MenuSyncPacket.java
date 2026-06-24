package org.shsts.tinycorelib.content.gui.sync;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.shsts.tinycorelib.api.gui.IMenuSyncHandler;
import org.shsts.tinycorelib.api.network.IPacket;
import org.shsts.tinycorelib.content.network.PacketPayloads;
import org.shsts.tinycorelib.content.network.PacketType;

import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public record MenuSyncPacket<P extends IPacket>(
    PacketType<P, ?> packetType,
    int containerId,
    int syncSlotId,
    P content
) implements CustomPacketPayload {
    public static <P extends IPacket> StreamCodec<RegistryFriendlyByteBuf, MenuSyncPacket<P>> codec(
        PacketType<P, MenuSyncPacket<P>> type, Supplier<P> constructor) {
        return CustomPacketPayload.codec(
            MenuSyncPacket::write,
            buf -> read(type, constructor, buf));
    }

    private static <P extends IPacket> MenuSyncPacket<P> read(
        PacketType<P, MenuSyncPacket<P>> type, Supplier<P> constructor,
        RegistryFriendlyByteBuf buf) {
        var containerId = buf.readVarInt();
        var syncSlotId = buf.readVarInt();
        var content = PacketPayloads.read(constructor, buf);
        return new MenuSyncPacket<>(type, containerId, syncSlotId, content);
    }

    private void write(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(containerId);
        buf.writeVarInt(syncSlotId);
        content.serializeToBuf(buf);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return packetType.type();
    }

    public void handle(IPayloadContext context) {
        var player = context.player();
        if (player.containerMenu.containerId == containerId &&
            player.containerMenu instanceof IMenuSyncHandler menu) {
            menu.handleSyncPacket(packetType, syncSlotId, content);
        }
    }
}
