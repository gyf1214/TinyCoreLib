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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.shsts.tinycorelib.api.gui.IMenu;
import org.shsts.tinycorelib.api.network.IPacket;
import org.shsts.tinycorelib.content.gui.sync.MenuSyncPacket;
import org.shsts.tinycorelib.content.network.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class Menu extends AbstractContainerMenu implements IMenu {
    private final Level world;
    private final BlockEntity blockEntity;
    private final Player player;
    private final Inventory inventory;
    @Nullable
    private final Channel channel;

    private Predicate<IMenu> isValid = $ -> true;

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
    }

    private final List<SyncSlot<?>> syncSlots = new ArrayList<>();

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
    public <P extends IPacket> int addSyncSlot(Function<BlockEntity, P> factory) {
        var index = syncSlots.size();
        syncSlots.add(new SyncSlot<>(index, factory));
        return index;
    }

    @Override
    public <P extends IPacket> Optional<P> getSyncPacket(int index, Class<P> clazz) {
        var slot = syncSlots.get(index);
        if (!clazz.isInstance(slot.packet)) {
            return Optional.empty();
        }
        return Optional.of(clazz.cast(slot.packet));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <P extends IPacket> void onSyncPacket(int index, Consumer<P> cb) {
        var slot = (SyncSlot<P>) syncSlots.get(index);
        slot.addCallback(cb);
    }

    public void handleSyncPacket(int index, IPacket packet) {
        var slot = syncSlots.get(index);
        if (slot == null) {
            return;
        }
        slot.setPacket(packet);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        for (var slot : syncSlots) {
            slot.sync(blockEntity);
        }
    }
}
