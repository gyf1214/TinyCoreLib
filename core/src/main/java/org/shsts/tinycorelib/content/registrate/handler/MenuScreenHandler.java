package org.shsts.tinycorelib.content.registrate.handler;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.shsts.tinycorelib.api.gui.MenuBase;
import org.shsts.tinycorelib.api.gui.client.IMenuScreenFactory;
import org.shsts.tinycorelib.content.gui.SmartMenuType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MenuScreenHandler {
    private final List<Consumer<RegisterMenuScreensEvent>> callbacks = new ArrayList<>();

    @OnlyIn(Dist.CLIENT)
    public <M extends MenuBase> void setMenuScreen(SmartMenuType<M> type,
        IMenuScreenFactory<M, ?> factory) {
        callbacks.add(event -> event.register(type, factory));
    }

    public void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        for (var cb : callbacks) {
            cb.accept(event);
        }
        // release references
        callbacks.clear();
    }
}
