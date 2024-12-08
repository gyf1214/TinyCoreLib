package org.shsts.tinycorelib.api.gui.client;

import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.shsts.tinycorelib.api.gui.IMenu;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class MenuScreenBase extends AbstractContainerScreen<AbstractContainerMenu> {
    protected final IMenu iMenu;

    public MenuScreenBase(IMenu menu, Inventory inventory, Component title) {
        super(menu.getMenu(), inventory, title);
        this.iMenu = menu;
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {}

    public <T extends GuiEventListener & Widget & NarratableEntry> void addWidgetToScreen(T widget) {
        addRenderableWidget(widget);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);
        renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {}

    @Override
    public void removed() {
        super.removed();
        for (var plugin : iMenu.getPlugins()) {
            plugin.onScreenRemoved();
        }
    }
}
