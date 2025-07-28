package org.shsts.tinycorelib.api.gui;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.shsts.tinycorelib.api.network.IChannel;

@FunctionalInterface
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IMenuFactory<M extends MenuBase> {
    M create(MenuType<?> menuType, int id, Inventory inventory, BlockEntity blockEntity,
        @Nullable IChannel channel);
}
