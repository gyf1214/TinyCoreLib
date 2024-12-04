package org.shsts.tinycorelib.test;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.shsts.tinycorelib.api.gui.IMenu;
import org.shsts.tinycorelib.api.gui.client.MenuScreenBase;
import org.slf4j.Logger;

import static org.shsts.tinycorelib.test.All.TEST_CAPABILITY;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TestScreen extends MenuScreenBase {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final int TEXT_COLOR = 0xFF202020;

    public TestScreen(IMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        menu.<TestSyncPacket>onSyncPacket("seconds",
            p -> LOGGER.info("sync packet seconds = {}", p.getSeconds()));
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        fill(poseStack, leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFFFFFFFF);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        var seconds1 = iMenu.getSyncPacket("seconds", TestSyncPacket.class)
            .map(TestSyncPacket::getSeconds).orElse(0);
        var seconds2 = TEST_CAPABILITY.get(iMenu.blockEntity()).getSeconds();
        var line = new TextComponent("%d %d".formatted(seconds1, seconds2));
        font.draw(poseStack, title, (float) titleLabelX, (float) titleLabelY, TEXT_COLOR);
        font.draw(poseStack, line, (float) titleLabelX, (float) titleLabelY + font.lineHeight, TEXT_COLOR);
    }
}
