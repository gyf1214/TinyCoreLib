package org.shsts.tinycorelib.content.gui;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.shsts.tinycorelib.api.gui.IMenu;
import org.shsts.tinycorelib.api.gui.IMenuEvent;
import org.shsts.tinycorelib.api.gui.IMenuPlugin;
import org.shsts.tinycorelib.api.network.IPacket;
import org.shsts.tinycorelib.content.gui.sync.MenuEventPacket;
import org.shsts.tinycorelib.content.gui.sync.MenuSyncPacket;
import org.shsts.tinycorelib.content.network.Channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class Menu extends AbstractContainerMenu implements IMenu {
    private final Level world;
    private final BlockEntity blockEntity;
    private final Player player;
    private final Inventory inventory;
    @Nullable
    private final Channel channel;
    private final List<IMenuPlugin<?>> plugins = new ArrayList<>();

    private Predicate<IMenu> isValid = $ -> true;
    private Predicate<Slot> onQuickMoveStack = $ -> false;

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
                    var syncPacket = new MenuSyncPacket(channel, containerId, index, packet1);
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

    public Menu(MenuType<?> menuType, int id, Inventory inventory, BlockEntity blockEntity,
        @Nullable Channel channel) {
        super(menuType, id);
        this.blockEntity = blockEntity;
        this.world = blockEntity.getLevel();
        assert world != null;
        this.player = inventory.player;
        this.inventory = inventory;
        this.channel = channel;
    }

    public void addPlugin(IMenuPlugin<?> plugin) {
        plugins.add(plugin);
    }

    @Override
    public List<IMenuPlugin<?>> getPlugins() {
        return plugins;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        for (var plugin : plugins) {
            plugin.onMenuRemoved();
        }
    }

    @Override
    public AbstractContainerMenu getMenu() {
        return this;
    }

    @Override
    public boolean stillValid(Player player) {
        var be = blockEntity;
        var level = be.getLevel();
        var pos = be.getBlockPos();
        return player == this.player &&
            level == player.getLevel() &&
            level.getBlockEntity(pos) == be &&
            player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < 64.0 &&
            isValid.test(this);
    }

    @Override
    public BlockEntity blockEntity() {
        return blockEntity;
    }

    @Override
    public Player player() {
        return player;
    }

    @Override
    public Inventory inventory() {
        return inventory;
    }

    @Override
    public Level world() {
        return world;
    }

    @Override
    public void setValidPredicate(Predicate<IMenu> pred) {
        isValid = pred;
    }

    @Override
    public Slot addSlot(Slot slot) {
        return super.addSlot(slot);
    }

    @Override
    public int getSlotSize() {
        return slots.size();
    }

    @Override
    public Slot getSlot(int index) {
        return super.getSlot(index);
    }

    @Override
    public void setOnQuickMoveStack(Predicate<Slot> cb) {
        onQuickMoveStack = cb;
    }

    private <P extends IPacket> SyncSlot<P> createSyncSlot(Function<BlockEntity, P> factory) {
        var index = syncSlots.size();
        var slot = new SyncSlot<>(index, factory);
        syncSlots.add(slot);
        return slot;
    }

    @Override
    public <P extends IPacket> void addSyncSlot(String name, Function<BlockEntity, P> factory) {
        assert !syncSlotNames.containsKey(name);
        var slot = createSyncSlot(factory);
        syncSlotNames.put(name, slot);
    }

    @Override
    public <P extends IPacket> int addSyncSlot(Function<BlockEntity, P> factory) {
        return createSyncSlot(factory).index;
    }

    @Override
    public <P extends IPacket> Optional<P> getSyncPacket(int index, Class<P> clazz) {
        return syncSlots.get(index).getPacket(clazz);
    }

    @Override
    public <P extends IPacket> Optional<P> getSyncPacket(String name, Class<P> clazz) {
        return syncSlotNames.get(name).getPacket(clazz);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <P extends IPacket> void onSyncPacket(int index, Consumer<P> cb) {
        var slot = (SyncSlot<P>) syncSlots.get(index);
        slot.addCallback(cb);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <P extends IPacket> void onSyncPacket(String name, Consumer<P> cb) {
        var slot = (SyncSlot<P>) syncSlotNames.get(name);
        slot.addCallback(cb);
    }

    @Override
    public <P extends IPacket> void onEventPacket(IMenuEvent<P> event, Consumer<P> cb) {
        eventHandlers.put(event, new EventHandler<>(event.clazz(), cb));
    }

    @Override
    public <P extends IPacket> void triggerEvent(IMenuEvent<P> event, Supplier<P> factory) {
        if (channel != null) {
            var packet = new MenuEventPacket(channel, containerId, event, factory.get());
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
        var slot = getSlot(index);
        if (onQuickMoveStack.test(slot)) {
            return slot.getItem();
        }
        return ItemStack.EMPTY;
    }
}
