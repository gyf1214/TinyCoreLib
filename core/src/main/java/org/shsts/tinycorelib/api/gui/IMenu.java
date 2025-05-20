package org.shsts.tinycorelib.api.gui;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.shsts.tinycorelib.api.network.IPacket;

import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IMenu {
    AbstractContainerMenu getMenu();

    BlockEntity blockEntity();

    Level world();

    Player player();

    Inventory inventory();

    List<IMenuPlugin<?>> getPlugins();

    void setValidPredicate(BooleanSupplier pred);

    void addMenuSlot(Slot slot);

    int getSlotSize();

    Slot getMenuSlot(int index);

    /**
     * The callback returns whether to continue call this function.
     */
    void setOnQuickMoveStack(Predicate<Slot> cb);

    <P extends IPacket> int addSyncSlot(Function<BlockEntity, P> factory);

    <P extends IPacket> void addSyncSlot(String name, Function<BlockEntity, P> factory);

    /**
     * Called by Client to get the latest sync packet.
     */
    <P extends IPacket> Optional<P> getSyncPacket(int index, Class<P> clazz);

    /**
     * Called by Client to get the latest sync packet.
     */
    <P extends IPacket> Optional<P> getSyncPacket(String name, Class<P> clazz);

    /**
     * Callback added by Client.
     */
    <P extends IPacket> void onSyncPacket(int index, Consumer<P> cb);

    /**
     * Callback added by Client.
     */
    <P extends IPacket> void onSyncPacket(String name, Consumer<P> cb);

    /**
     * Callback added by Server.
     */
    <P extends IPacket> void onEventPacket(IMenuEvent<P> event, Consumer<P> cb);

    /**
     * Trigger an event from Client.
     */
    <P extends IPacket> void triggerEvent(IMenuEvent<P> event, Supplier<P> factory);
}
