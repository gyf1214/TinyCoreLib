package org.shsts.tinycorelib.test;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.shsts.tinycorelib.api.gui.client.MenuScreenBase;
import org.slf4j.Logger;

import static org.shsts.tinycorelib.test.All.TEST_CAPABILITY;
import static org.shsts.tinycorelib.test.All.TEST_MENU_EVENT;
import static org.shsts.tinycorelib.test.TestMenu.SLOT_SIZE;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TestScreen extends MenuScreenBase<TestMenu> {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final int TEXT_COLOR = 0xFF202020;

    private record SlotBg(int x, int y) implements Widget, GuiEventListener, NarratableEntry {
        @Override
        public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
            fill(poseStack, x, y, x + SLOT_SIZE, y + SLOT_SIZE, 0xFFC0C0C0);
        }

        @Override
        public NarrationPriority narrationPriority() {
            return NarrationPriority.NONE;
        }

        @Override
        public void updateNarration(NarrationElementOutput output) {}
    }

    public TestScreen(TestMenu menu, Component title) {
        super(menu, title);

        this.imageHeight = menu.endY;

        menu.<TestPacket>onSyncPacket("seconds",
            p -> LOGGER.info("sync packet seconds = {}", p.getValue()));
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        fill(poseStack, leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFFFFFFFF);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderLabels(poseStack, mouseX, mouseY);

        var seconds1 = menu.getSyncPacket("seconds", TestPacket.class)
            .map(TestPacket::getValue).orElse(0);
        var seconds2 = TEST_CAPABILITY.get(menu.blockEntity()).getSeconds();
        var line = new TextComponent("%d %d".formatted(seconds1, seconds2));
        font.draw(poseStack, line, 8f, 18f, TEXT_COLOR);
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
        addWidgetToScreen(new Button(8 + leftPos, 30 + topPos,
            100, 20, new TextComponent("OK"), $ -> {
            var seconds = TEST_CAPABILITY.get(menu.blockEntity()).getSeconds();
            menu.triggerEvent(TEST_MENU_EVENT, () -> new TestPacket(seconds));
        }));
    }
}
