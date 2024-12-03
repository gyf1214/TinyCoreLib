package org.shsts.tinycorelib.api.registrate.entry;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IMenuType extends IEntry<MenuType<?>> {
    void open(ServerPlayer player, BlockPos pos);
}
