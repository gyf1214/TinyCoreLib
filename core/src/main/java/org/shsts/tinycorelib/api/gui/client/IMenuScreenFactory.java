package org.shsts.tinycorelib.api.gui.client;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.shsts.tinycorelib.api.gui.MenuBase;

@OnlyIn(Dist.CLIENT)
@FunctionalInterface
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IMenuScreenFactory<M extends MenuBase, S extends MenuScreenBase<M>>
    extends MenuScreens.ScreenConstructor<M, S> {
    S create(M menu, Component title);

    default S create(M menu, Inventory inventory, Component title) {
        assert inventory == menu.inventory();
        return create(menu, title);
    }
}
