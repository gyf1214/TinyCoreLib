package org.shsts.tinycorelib.api.gui.client;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.shsts.tinycorelib.api.gui.IMenu;

@OnlyIn(Dist.CLIENT)
@FunctionalInterface
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IMenuScreenFactory<U extends MenuScreenBase> {
    U create(IMenu menu, Inventory inventory, Component title);
}
