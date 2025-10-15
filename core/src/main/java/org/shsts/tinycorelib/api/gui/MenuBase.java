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
import org.shsts.tinycorelib.content.gui.SimpleSyncSlotScheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MenuBase extends AbstractContainerMenu {
    protected final Level world;
    protected final BlockEntity blockEntity;
    protected final Player player;
    protected final Inventory inventory;
    @Nullable
    private final IChannel channel;

    private class SyncSlot<P extends IPacket> {
        private final int index;
        private final ISyncSlotScheduler<P> scheduler;
        private final List<Consumer<P>> callbacks = new ArrayList<>();
        @Nullable
        private P packet = null;

        public SyncSlot(int index, ISyncSlotScheduler<P> scheduler) {
            this.index = index;
            this.scheduler = scheduler;
        }

        public void sync() {
            if (channel != null && player instanceof ServerPlayer serverPlayer) {
                if (scheduler.shouldSend()) {
                    var packet = scheduler.createPacket();
                    var syncPacket = channel.createMenuSyncPacket(containerId, index, packet);
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

    public record Properties(MenuType<?> menuType, int id, Inventory inventory,
        BlockEntity blockEntity, @Nullable IChannel channel) {}

    public MenuBase(Properties properties) {
        super(properties.menuType, properties.id);
        this.blockEntity = properties.blockEntity;
        this.world = blockEntity.getLevel();
        assert world != null;
        this.inventory = properties.inventory;
        this.player = inventory.player;
        this.channel = properties.channel;
    }

    public BlockEntity blockEntity() {
        return blockEntity;
    }

    public Player player() {
        return player;
    }

    public Inventory inventory() {
        return inventory;
    }

    public int slotSize() {
        return slots.size();
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

    private <P extends IPacket> SyncSlot<P> createSyncSlot(ISyncSlotScheduler<P> scheduler) {
        var index = syncSlots.size();
        var slot = new SyncSlot<>(index, scheduler);
        syncSlots.add(slot);
        return slot;
    }

    protected <P extends IPacket> int addSyncSlot(ISyncSlotScheduler<P> scheduler) {
        return createSyncSlot(scheduler).index;
    }

    protected <P extends IPacket> int addSyncSlot(Supplier<P> factory) {
        return addSyncSlot(new SimpleSyncSlotScheduler<>(factory));
    }

    protected <P extends IPacket> void addSyncSlot(String name, ISyncSlotScheduler<P> scheduler) {
        assert !syncSlotNames.containsKey(name);
        var slot = createSyncSlot(scheduler);
        syncSlotNames.put(name, slot);
    }

    protected <P extends IPacket> void addSyncSlot(String name, Supplier<P> factory) {
        addSyncSlot(name, new SimpleSyncSlotScheduler<>(factory));
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
        if (eventHandlers.containsKey(event)) {
            eventHandlers.get(event).handle(packet);
        }
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        for (var slot : syncSlots) {
            slot.sync();
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        // do nothing by default
        return ItemStack.EMPTY;
    }
}
