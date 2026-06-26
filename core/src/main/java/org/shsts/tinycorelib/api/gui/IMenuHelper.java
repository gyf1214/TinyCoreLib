package org.shsts.tinycorelib.api.gui;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerPlayer;
import org.shsts.tinycorelib.api.network.IPacket;
import org.shsts.tinycorelib.api.network.IPacketType;

import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IMenuHelper {
    <P extends IPacket> ISyncSlotScheduler<P> simpleScheduler(
        IPacketType<P> type, Supplier<P> factory);

    <P extends IPacket> void sendSyncPacket(
        ServerPlayer player, int containerId, int syncSlotId, IPacketType<P> type, P packet);

    <P extends IPacket> void sendEventPacket(
        int containerId, IPacketType<P> type, P packet);

    void requireMenuSyncPacket(IPacketType<?> type);

    void requireMenuEventPacket(IPacketType<?> type);
}
