package org.shsts.tinycorelib.content.gui;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.shsts.tinycorelib.api.gui.IMenuHelper;
import org.shsts.tinycorelib.api.gui.ISyncSlotScheduler;
import org.shsts.tinycorelib.api.network.IPacket;
import org.shsts.tinycorelib.api.network.IPacketType;
import org.shsts.tinycorelib.content.gui.sync.MenuEventPacket;
import org.shsts.tinycorelib.content.gui.sync.MenuSyncPacket;
import org.shsts.tinycorelib.content.network.PacketPayloadType;
import org.shsts.tinycorelib.content.registrate.handler.PayloadHandler;

import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public enum MenuHelper implements IMenuHelper {
    INSTANCE;

    @Override
    public <P extends IPacket> ISyncSlotScheduler<P> simpleScheduler(
        IPacketType<P> type, Supplier<P> factory) {
        return new SimpleSyncSlotScheduler<>(type, factory);
    }

    @Override
    public <P extends IPacket> void sendSyncPacket(
        ServerPlayer player, int containerId, int syncSlotId, IPacketType<P> type, P packet) {
        var packetType = PayloadHandler.<P, MenuSyncPacket<P>>requireType(type);
        requireMenuSyncPacket(packetType);
        PacketDistributor.sendToPlayer(player,
            new MenuSyncPacket<>(packetType, containerId, syncSlotId, packet));
    }

    @Override
    public <P extends IPacket> void sendEventPacket(int containerId, IPacketType<P> type, P packet) {
        var packetType = PayloadHandler.<P, MenuEventPacket<P>>requireType(type);
        requireMenuEventPacket(packetType);
        PacketDistributor.sendToServer(new MenuEventPacket<>(packetType, containerId, packet));
    }

    @Override
    public void requireMenuSyncPacket(IPacketType<?> type) {
        PayloadHandler.requirePayloadType(type, PacketPayloadType.MENU_SYNC);
    }

    @Override
    public void requireMenuEventPacket(IPacketType<?> type) {
        PayloadHandler.requirePayloadType(type, PacketPayloadType.MENU_EVENT);
    }
}
