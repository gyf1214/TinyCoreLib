package org.shsts.tinycorelib.test;

import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.shsts.tinycorelib.api.gui.IMenu;
import org.shsts.tinycorelib.api.gui.IMenuPlugin;
import org.shsts.tinycorelib.api.gui.client.MenuScreenBase;

import static net.minecraft.client.gui.GuiComponent.fill;
import static org.shsts.tinycorelib.test.TestScreen.MARGIN;
import static org.shsts.tinycorelib.test.TestScreen.SLOT_SIZE;
import static org.shsts.tinycorelib.test.TestScreen.SPACING;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class InventoryPlugin implements IMenuPlugin {
    private final IMenu menu;
    private final int endY;

    public InventoryPlugin(IMenu menu, int y) {
        this.menu = menu;
        var inventory = menu.inventory();
        var barY = y + 3 * SLOT_SIZE + SPACING;
        for (var j = 0; j < 9; j++) {
            var x = MARGIN + j * SLOT_SIZE;
            menu.addSlot(new Slot(inventory, j, x + 1, barY + 1));
        }
        for (var i = 0; i < 3; i++) {
            for (var j = 0; j < 9; j++) {
                var x = MARGIN + j * SLOT_SIZE;
                var y1 = y + i * SLOT_SIZE;
                menu.addSlot(new Slot(inventory, 9 + i * 9 + j, x + 1, y1 + 1));
            }
        }
        this.endY = barY + SLOT_SIZE + MARGIN;
    }

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

    @Override
    @OnlyIn(Dist.CLIENT)
    public void applyMenuScreen(MenuScreenBase s) {
        var screen = (TestScreen) s;
        if (screen.getImageHeight() < endY) {
            screen.setImageHeight(endY);
        }
        screen.onInit($ -> {
            for (var i = 0; i < menu.getSlotSize(); i++) {
                var slot = menu.getSlot(i);
                var x = slot.x - 1 + screen.getGuiLeft();
                var y = slot.y - 1 + screen.getGuiTop();
                screen.addWidgetToScreen(new SlotBg(x, y));
            }
        });
    }
}
