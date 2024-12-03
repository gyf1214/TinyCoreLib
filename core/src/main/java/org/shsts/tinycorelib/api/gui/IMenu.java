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

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IMenu {
    AbstractContainerMenu getMenu();

    BlockEntity blockEntity();

    Level world();

    Player player();

    Inventory inventory();

    void setValidPredicate(Predicate<IMenu> pred);

    Slot addSlot(Slot slot);

    <P extends IPacket> int addSyncSlot(Function<BlockEntity, P> factory);

    /**
     * Called by Screen to get the latest sync packet.
     */
    <P extends IPacket> Optional<P> getSyncPacket(int index, Class<P> clazz);

    /**
     * Callback added by Screen.
     */
    <P extends IPacket> void onSyncPacket(int index, Consumer<P> cb);
}
