package org.shsts.tinycorelib.api.network;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.shsts.tinycorelib.api.gui.IMenuEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IChannel {
    void sendToServer(IPacket packet);

    void sendToPlayer(ServerPlayer player, IPacket packet);

    <P extends IPacket> IChannel registerPacket(Class<P> clazz, Supplier<P> constructor,
        BiConsumer<P, NetworkEvent.Context> handler);

    <P extends IPacket> IChannel registerClientPacket(Class<P> clazz, Supplier<P> constructor,
        BiConsumer<P, NetworkEvent.Context> handler);

    <P extends IPacket> IChannel registerMenuSyncPacket(Class<P> clazz, Supplier<P> constructor);

    <P extends IPacket> IMenuEvent<P> registerMenuEventPacket(Class<P> clazz, Supplier<P> constructor);

    IPacket createMenuSyncPacket(int containerId, int index, IPacket content);

    <P extends IPacket> IPacket createMenuEventPacket(int containerId, IMenuEvent<P> event, P content);
}
