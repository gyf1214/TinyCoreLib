package org.shsts.tinycorelib.content.registrate.handler;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.shsts.tinycorelib.api.gui.IMenu;
import org.shsts.tinycorelib.api.gui.client.IMenuScreenFactory;
import org.shsts.tinycorelib.api.gui.client.MenuScreenBase;
import org.shsts.tinycorelib.content.gui.SmartMenuType;

import java.util.HashMap;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MenuScreenHandler {
    private final Map<SmartMenuType, IMenuScreenFactory<?>> entries = new HashMap<>();

    @OnlyIn(Dist.CLIENT)
    public void setMenuScreen(SmartMenuType type, IMenuScreenFactory<?> constructor) {
        entries.put(type, constructor);
    }

    public void onClientSetup() {
        for (var entry : entries.entrySet()) {
            var type = entry.getKey();
            var factory = entry.getValue();
            MenuScreens.<AbstractContainerMenu, MenuScreenBase>register(type,
                (menu, inventory, title) -> factory.create((IMenu) menu, inventory, title));
        }
        entries.clear();
    }
}
