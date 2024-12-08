package org.shsts.tinycorelib.test;

import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.SlotItemHandler;
import org.shsts.tinycorelib.api.gui.IMenu;
import org.shsts.tinycorelib.api.gui.IMenuPlugin;
import org.shsts.tinycorelib.api.gui.client.MenuScreenBase;
import org.slf4j.Logger;

import static org.shsts.tinycorelib.test.All.ITEM_HANDLER_CAPABILITY;
import static org.shsts.tinycorelib.test.All.TEST_CAPABILITY;
import static org.shsts.tinycorelib.test.All.TEST_MENU_EVENT;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TestMenuPlugin implements IMenuPlugin {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final IMenu menu;
    private final BlockEntity blockEntity;

    public TestMenuPlugin(IMenu menu) {
        this.menu = menu;
        this.blockEntity = menu.blockEntity();

        var itemHandler = ITEM_HANDLER_CAPABILITY.get(blockEntity);
        var slot = new SlotItemHandler(itemHandler, 0, 111, 32);
        menu.addSlot(slot);

        menu.onEventPacket(TEST_MENU_EVENT, this::onEvent);
    }

    private void onEvent(TestPacket p) {
        LOGGER.info("Menu event: seconds = {}", p.getValue());
        TEST_CAPABILITY.get(blockEntity).foo();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void applyMenuScreen(MenuScreenBase screen) {
        ((TestScreen) screen).onInit(screen1 ->
            screen1.addWidgetToScreen(new Button(8 + screen1.getGuiLeft(), 30 + screen1.getGuiTop(),
                100, 20, new TextComponent("OK"), $ -> {
                var seconds = TEST_CAPABILITY.get(blockEntity).getSeconds();
                menu.triggerEvent(TEST_MENU_EVENT, () -> new TestPacket(seconds));
            })));
    }
}
