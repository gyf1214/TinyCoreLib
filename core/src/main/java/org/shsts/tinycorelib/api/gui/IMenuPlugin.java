package org.shsts.tinycorelib.api.gui;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.shsts.tinycorelib.api.gui.client.MenuScreenBase;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IMenuPlugin<S extends MenuScreenBase> {
    default void onMenuRemoved() {}

    /**
     * Rewrite this to avail applyMenuScreen.
     */
    @OnlyIn(Dist.CLIENT)
    default @Nullable Class<S> menuScreenClass() {
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    default void applyMenuScreen(S screen) {}

    @OnlyIn(Dist.CLIENT)
    default void onScreenRemoved() {}

    IMenuPlugin<?> EMPTY = new IMenuPlugin<>() {};
}
