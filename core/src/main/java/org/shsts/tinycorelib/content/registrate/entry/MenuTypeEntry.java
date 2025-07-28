package org.shsts.tinycorelib.content.registrate.entry;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import org.shsts.tinycorelib.api.registrate.entry.IMenuType;
import org.shsts.tinycorelib.content.gui.SmartMenuType;

import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MenuTypeEntry extends Entry<MenuType<?>> implements IMenuType {
    public MenuTypeEntry(ResourceLocation loc) {
        super(loc);
    }

    public MenuTypeEntry(ResourceLocation loc, Supplier<MenuType<?>> supplier) {
        super(loc, supplier);
    }

    @Override
    public void open(ServerPlayer player, BlockPos pos) {
        ((SmartMenuType<?>) get()).open(player, pos);
    }
}
