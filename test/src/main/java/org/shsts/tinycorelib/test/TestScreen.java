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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.shsts.tinycorelib.test.All.TEST_CAPABILITY;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TestScreen extends MenuScreenBase {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final int TEXT_COLOR = 0xFF202020;

    private final List<Consumer<TestScreen>> initCallbacks = new ArrayList<>();

    public TestScreen(IMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
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

        var seconds1 = iMenu.getSyncPacket("seconds", TestPacket.class)
            .map(TestPacket::getValue).orElse(0);
        var seconds2 = TEST_CAPABILITY.get(iMenu.blockEntity()).getSeconds();
        var line = new TextComponent("%d %d".formatted(seconds1, seconds2));
        font.draw(poseStack, line, 8f, 18f, TEXT_COLOR);
    }

    public void onInit(Consumer<TestScreen> cb) {
        initCallbacks.add(cb);
    }

    @Override
    protected void init() {
        super.init();
        for (var cb : initCallbacks) {
            cb.accept(this);
        }
    }
}
