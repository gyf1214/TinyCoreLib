package org.shsts.tinycorelib.test;

import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.shsts.tinycorelib.api.gui.client.MenuScreenBase;
import org.slf4j.Logger;

import static org.shsts.tinycorelib.test.All.TEST_CAPABILITY;
import static org.shsts.tinycorelib.test.All.TEST_MENU_EVENT;
import static org.shsts.tinycorelib.test.All.TEST_MENU_SYNC;
import static org.shsts.tinycorelib.test.TestMenu.SLOT_SIZE;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TestScreen extends MenuScreenBase<TestMenu> {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final int TEXT_COLOR = 0xFF202020;

    private static final class SlotBg implements Renderable, GuiEventListener, NarratableEntry {
        private final int x;
        private final int y;
        private boolean focused = false;

        private SlotBg(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            guiGraphics.fill(x, y, x + SLOT_SIZE, y + SLOT_SIZE, 0xFFC0C0C0);
        }

        @Override
        public void setFocused(boolean value) {
            focused = value;
        }

        @Override
        public boolean isFocused() {
            return focused;
        }

        @Override
        public NarrationPriority narrationPriority() {
            return NarrationPriority.NONE;
        }

        @Override
        public void updateNarration(NarrationElementOutput output) {}
    }

    public TestScreen(TestMenu menu, Component title) {
        super(menu, menu.inventory(), title);

        this.imageHeight = menu.endY;

        menu.<TestPacket>onSyncPacket("seconds",
            p -> LOGGER.info("sync packet seconds = {}", p.getValue()));
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFFFFFFFF);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);

        var seconds1 = menu.getSyncPacket("seconds", TEST_MENU_SYNC)
            .map(TestPacket::getValue).orElse(0);
        var seconds2 = TEST_CAPABILITY.get(menu.blockEntity()).getSeconds();
        var line = Component.literal("%d %d".formatted(seconds1, seconds2));
        guiGraphics.drawString(font, line, 8, 18, TEXT_COLOR, false);
    }

    @Override
    protected void init() {
        super.init();
        for (var i = 0; i < menu.slotSize(); i++) {
            var slot = menu.getSlot(i);
            var x = slot.x - 1 + leftPos;
            var y = slot.y - 1 + topPos;
            addWidgetToScreen(new SlotBg(x, y));
        }
        addWidgetToScreen(Button.builder(Component.literal("OK"), $ -> {
            var seconds = TEST_CAPABILITY.get(menu.blockEntity()).getSeconds();
            menu.triggerEvent(TEST_MENU_EVENT, () -> new TestPacket(seconds));
        }).bounds(8 + leftPos, 30 + topPos, 100, 20).build());
    }
}
