package org.shsts.tinycorelib.content.network;

import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.shsts.tinycorelib.api.gui.IMenuEvent;
import org.shsts.tinycorelib.api.network.IChannel;
import org.shsts.tinycorelib.api.network.IPacket;
import org.shsts.tinycorelib.content.gui.Menu;
import org.shsts.tinycorelib.content.gui.MenuEvent;
import org.shsts.tinycorelib.content.gui.sync.MenuEventPacket;
import org.shsts.tinycorelib.content.gui.sync.MenuSyncPacket;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class Channel implements IChannel {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final ResourceLocation loc;
    private final SimpleChannel channel;
    private int msgId = 0;
    private final Set<Class<?>> registeredPackets = new HashSet<>();

    public final IndexedPacketDispatcher<Class<?>> syncPackets =
        new IndexedPacketDispatcher<>();
    public final IndexedPacketDispatcher<IMenuEvent<?>> eventPackets =
        new IndexedPacketDispatcher<>();

    public Channel(ResourceLocation loc, String version) {
        this.loc = loc;
        this.channel = NetworkRegistry.newSimpleChannel(loc, () -> version,
            version::equals, version::equals);
    }

    private void handleMenuSyncPacket(MenuSyncPacket packet, NetworkEvent.Context ctx) {
        var player = Minecraft.getInstance().player;
        if (player != null && player.containerMenu instanceof Menu menu &&
            menu.containerId == packet.getContainerId()) {
            menu.handleSyncPacket(packet.getIndex(), packet.getContent());
        }
    }

    private void handleMenuEventPacket(MenuEventPacket packet, NetworkEvent.Context ctx) {
        var player = ctx.getSender();
        if (player != null && player.containerMenu instanceof Menu menu &&
            menu.containerId == packet.getContainerId()) {
            menu.handleEventPacket(packet.getEvent(), packet.getContent());
        }
    }

    @Override
    public void sendToServer(IPacket packet) {
        channel.sendToServer(packet);
    }

    @Override
    public void sendToPlayer(ServerPlayer player, IPacket packet) {
        channel.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    @Override
    public <P extends IPacket> IChannel registerPacket(Class<P> clazz, Supplier<P> constructor,
        BiConsumer<P, NetworkEvent.Context> handler) {
        if (registeredPackets.contains(clazz)) {
            return this;
        }
        var id = msgId++;
        channel.registerMessage(id, clazz, IPacket::serializeToBuf,
            (buf) -> {
                var p = constructor.get();
                p.deserializeFromBuf(buf);
                return p;
            }, (msg, ctxSupp) -> {
                var ctx = ctxSupp.get();
                ctx.enqueueWork(() -> handler.accept(msg, ctx));
                ctx.setPacketHandled(true);
            });
        registeredPackets.add(clazz);
        LOGGER.debug("{}: register packet {}", this, clazz.getName());
        return this;
    }

    @Override
    public <P extends IPacket> IChannel registerClientPacket(Class<P> clazz,
        Supplier<P> constructor,
        BiConsumer<P, NetworkEvent.Context> handler) {
        return registerPacket(clazz, constructor, (msg, ctx) ->
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handler.accept(msg, ctx)));
    }

    @Override
    public <P extends IPacket> IChannel registerMenuSyncPacket(Class<P> clazz,
        Supplier<P> constructor) {
        syncPackets.register(clazz, constructor);
        return registerClientPacket(MenuSyncPacket.class, () -> new MenuSyncPacket(this),
            this::handleMenuSyncPacket);
    }

    @Override
    public <P extends IPacket> IMenuEvent<P> registerMenuEventPacket(Class<P> clazz,
        Supplier<P> constructor) {
        var event = eventPackets.register(id -> new MenuEvent<>(id, clazz), constructor);
        registerPacket(MenuEventPacket.class, () -> new MenuEventPacket(this),
            this::handleMenuEventPacket);
        return event;
    }

    @Override
    public String toString() {
        return "Channel{%s}".formatted(loc);
    }
}
