package org.shsts.tinycorelib.content.registrate.handler;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.shsts.tinycorelib.api.gui.MenuBase;
import org.shsts.tinycorelib.api.gui.client.IMenuScreenFactory;
import org.shsts.tinycorelib.content.gui.SmartMenuType;

import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MenuScreenHandler {
    private final List<Runnable> callbacks = new ArrayList<>();

    @OnlyIn(Dist.CLIENT)
    public <M extends MenuBase> void setMenuScreen(SmartMenuType<M> type,
        IMenuScreenFactory<M, ?> factory) {
        callbacks.add(() -> MenuScreens.register(type, factory));
    }

    public void onClientSetup() {
        for (var cb : callbacks) {
            cb.run();
        }
    }
}
