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
import net.neoforged.neoforge.network.PacketDistributor;
import org.shsts.tinycorelib.api.network.IPacket;
import org.shsts.tinycorelib.api.network.IPacketType;
import org.shsts.tinycorelib.content.gui.SimpleSyncSlotScheduler;
import org.shsts.tinycorelib.content.gui.sync.MenuEventPacket;
import org.shsts.tinycorelib.content.gui.sync.MenuSyncPacket;
import org.shsts.tinycorelib.content.network.PacketPayloadType;
import org.shsts.tinycorelib.content.registrate.handler.PayloadHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MenuBase extends AbstractContainerMenu implements IMenuSyncHandler, IMenuEventHandler {
    protected final Level world;
    @Nullable
    protected final BlockEntity blockEntity;
    protected final Player player;
    protected final Inventory inventory;

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
            if (player instanceof ServerPlayer serverPlayer) {
                if (scheduler.shouldSend()) {
                    var packet = scheduler.createPacket();
                    var type = PayloadHandler.<P, MenuSyncPacket<P>>requireType(
                        scheduler.packetType());
                    PayloadHandler.requirePayloadType(type, PacketPayloadType.MENU_SYNC);
                    PacketDistributor.sendToPlayer(serverPlayer,
                        new MenuSyncPacket<>(type, containerId, index, packet));
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

        @SuppressWarnings("unchecked")
        public <P1 extends IPacket> Optional<P1> getPacket(IPacketType<P1> type) {
            return scheduler.packetType().equals(type) && packet != null ?
                Optional.of((P1) packet) : Optional.empty();
        }
    }

    private final List<SyncSlot<?>> syncSlots = new ArrayList<>();
    private final Map<String, SyncSlot<?>> syncSlotNames = new HashMap<>();

    private record EventHandler<P extends IPacket>(IPacketType<P> type, Consumer<P> handler) {
        @SuppressWarnings("unchecked")
        public void handle(IPacketType<?> packetType, IPacket packet) {
            if (type.equals(packetType)) {
                handler.accept((P) packet);
            }
        }
    }

    private final Map<IPacketType<?>, EventHandler<?>> eventHandlers = new HashMap<>();

    public record Properties(MenuType<?> menuType, int id, Inventory inventory,
        @Nullable BlockEntity blockEntity) {}

    public MenuBase(Properties properties) {
        super(properties.menuType, properties.id);
        this.blockEntity = properties.blockEntity;
        this.inventory = properties.inventory;
        this.player = inventory.player;
        this.world = player.level();
    }

    public BlockEntity blockEntity() {
        assert blockEntity != null;
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
        if (player != this.player) {
            return false;
        }
        if (blockEntity == null) {
            return true;
        }
        var be = blockEntity;
        var level = be.getLevel();
        var pos = be.getBlockPos();
        return level == player.level() &&
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

    protected <P extends IPacket> int addSyncSlot(IPacketType<P> type, Supplier<P> factory) {
        return addSyncSlot(new SimpleSyncSlotScheduler<>(type, factory));
    }

    protected <P extends IPacket> void addSyncSlot(String name, ISyncSlotScheduler<P> scheduler) {
        assert !syncSlotNames.containsKey(name);
        var slot = createSyncSlot(scheduler);
        syncSlotNames.put(name, slot);
    }

    protected <P extends IPacket> void addSyncSlot(String name, IPacketType<P> type,
        Supplier<P> factory) {
        addSyncSlot(name, new SimpleSyncSlotScheduler<>(type, factory));
    }

    /**
     * Called by Client to get the latest sync packet.
     */
    public <P extends IPacket> Optional<P> getSyncPacket(int index, IPacketType<P> type) {
        return syncSlots.get(index).getPacket(type);
    }

    /**
     * Called by Client to get the latest sync packet.
     */
    public <P extends IPacket> Optional<P> getSyncPacket(String name, IPacketType<P> type) {
        return syncSlotNames.get(name).getPacket(type);
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
    protected <P extends IPacket> void onEventPacket(IPacketType<P> type, Consumer<P> cb) {
        PayloadHandler.requirePayloadType(type, PacketPayloadType.MENU_EVENT);
        eventHandlers.put(type, new EventHandler<>(type, cb));
    }

    /**
     * Trigger an event from Client.
     */
    public <P extends IPacket> void triggerEvent(IPacketType<P> type, Supplier<P> factory) {
        var packetType = PayloadHandler.<P, MenuEventPacket<P>>requireType(type);
        PayloadHandler.requirePayloadType(type, PacketPayloadType.MENU_EVENT);
        PacketDistributor.sendToServer(new MenuEventPacket<>(packetType, containerId, factory.get()));
    }

    @Override
    public <P extends IPacket> void handleSyncPacket(IPacketType<P> type, int index, P packet) {
        if (index < 0 || index >= syncSlots.size()) {
            return;
        }
        var slot = syncSlots.get(index);
        if (!slot.scheduler.packetType().equals(type)) {
            return;
        }
        slot.setPacket(packet);
    }

    @Override
    public <P extends IPacket> void handleEventPacket(IPacketType<P> type, P packet) {
        if (eventHandlers.containsKey(type)) {
            eventHandlers.get(type).handle(type, packet);
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
