package org.shsts.tinycorelib.api.gui;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.shsts.tinycorelib.api.gui.client.MenuScreenBase;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IMenuPlugin {
    default void onMenuRemoved(Player player) {}

    @OnlyIn(Dist.CLIENT)
    default void applyMenuScreen(MenuScreenBase screen) {}

    @OnlyIn(Dist.CLIENT)
    default void onScreenRemoved() {}

    IMenuPlugin EMPTY = new IMenuPlugin() {};
}
