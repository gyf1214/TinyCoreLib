package org.shsts.tinycorelib.api.gui;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.shsts.tinycorelib.api.network.IChannel;
import org.shsts.tinycorelib.api.network.IPacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MenuBase extends AbstractContainerMenu implements IMenu {
    protected final Level world;
    protected final BlockEntity blockEntity;
    protected final Player player;
    protected final Inventory inventory;
    @Nullable
    private final IChannel channel;

    private class SyncSlot<P extends IPacket> {
        private final int index;
        private final List<Consumer<P>> callbacks = new ArrayList<>();
        private final Function<BlockEntity, P> factory;
        @Nullable
        private P packet = null;

        public SyncSlot(int index, Function<BlockEntity, P> factory) {
            this.index = index;
            this.factory = factory;
        }

        public void sync(BlockEntity be) {
            if (channel != null && player instanceof ServerPlayer serverPlayer) {
                var packet1 = factory.apply(be);
                if (!packet1.equals(packet)) {
                    packet = packet1;
                    var syncPacket = channel.createMenuSyncPacket(containerId, index, packet1);
                    channel.sendToPlayer(serverPlayer, syncPacket);
                }
            }
        }

        @SuppressWarnings("unchecked")
        public void setPacket(IPacket packet) {
            this.packet = (P) packet;
            for (var cb : callbacks) {
                cb.accept(this.packet);
            }
        }

        public void addCallback(Consumer<P> cb) {
            callbacks.add(cb);
        }

        public <P1 extends IPacket> Optional<P1> getPacket(Class<P1> clazz) {
            return clazz.isInstance(packet) ? Optional.of(clazz.cast(packet)) :
                Optional.empty();
        }
    }

    private final List<SyncSlot<?>> syncSlots = new ArrayList<>();
    private final Map<String, SyncSlot<?>> syncSlotNames = new HashMap<>();

    private record EventHandler<P extends IPacket>(Class<P> clazz, Consumer<P> handler) {
        public void handle(IPacket packet) {
            if (clazz.isInstance(packet)) {
                handler.accept(clazz.cast(packet));
            }
        }
    }

    private final Map<IMenuEvent<?>, EventHandler<?>> eventHandlers = new HashMap<>();

    public MenuBase(MenuType<?> menuType, int id, Inventory inventory, BlockEntity blockEntity,
        @Nullable IChannel channel) {
        super(menuType, id);
        this.blockEntity = blockEntity;
        this.world = blockEntity.getLevel();
        assert world != null;
        this.player = inventory.player;
        this.inventory = inventory;
        this.channel = channel;
    }

    @Override
    public boolean stillValid(Player player) {
        var be = blockEntity;
        var level = be.getLevel();
        var pos = be.getBlockPos();
        return player == this.player &&
            level == player.getLevel() &&
            level.getBlockEntity(pos) == be &&
            player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < 64.0;
    }

    private <P extends IPacket> SyncSlot<P> createSyncSlot(Function<BlockEntity, P> factory) {
        var index = syncSlots.size();
        var slot = new SyncSlot<>(index, factory);
        syncSlots.add(slot);
        return slot;
    }

    protected <P extends IPacket> void addSyncSlot(String name, Function<BlockEntity, P> factory) {
        assert !syncSlotNames.containsKey(name);
        var slot = createSyncSlot(factory);
        syncSlotNames.put(name, slot);
    }

    protected <P extends IPacket> int addSyncSlot(Function<BlockEntity, P> factory) {
        return createSyncSlot(factory).index;
    }

    /**
     * Called by Client to get the latest sync packet.
     */
    public <P extends IPacket> Optional<P> getSyncPacket(int index, Class<P> clazz) {
        return syncSlots.get(index).getPacket(clazz);
    }

    /**
     * Called by Client to get the latest sync packet.
     */
    public <P extends IPacket> Optional<P> getSyncPacket(String name, Class<P> clazz) {
        return syncSlotNames.get(name).getPacket(clazz);
    }

    /**
     * Callback added by Client.
     */
    @SuppressWarnings("unchecked")
    public <P extends IPacket> void onSyncPacket(int index, Consumer<P> cb) {
        var slot = (SyncSlot<P>) syncSlots.get(index);
        slot.addCallback(cb);
    }

    /**
     * Callback added by Client.
     */
    @SuppressWarnings("unchecked")
    public <P extends IPacket> void onSyncPacket(String name, Consumer<P> cb) {
        var slot = (SyncSlot<P>) syncSlotNames.get(name);
        slot.addCallback(cb);
    }

    /**
     * Callback added by Server.
     */
    protected <P extends IPacket> void onEventPacket(IMenuEvent<P> event, Consumer<P> cb) {
        eventHandlers.put(event, new EventHandler<>(event.clazz(), cb));
    }

    /**
     * Trigger an event from Client.
     */
    public <P extends IPacket> void triggerEvent(IMenuEvent<P> event, Supplier<P> factory) {
        if (channel != null) {
            var packet = channel.createMenuEventPacket(containerId, event, factory.get());
            channel.sendToServer(packet);
        }
    }

    public void handleSyncPacket(int index, IPacket packet) {
        var slot = syncSlots.get(index);
        if (slot == null) {
            return;
        }
        slot.setPacket(packet);
    }

    public void handleEventPacket(IMenuEvent<?> event, IPacket packet) {
        eventHandlers.get(event).handle(packet);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        for (var slot : syncSlots) {
            slot.sync(blockEntity);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        // do nothing by default
        return ItemStack.EMPTY;
    }
}
