package org.shsts.tinycorelib.test;

import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.items.SlotItemHandler;
import org.shsts.tinycorelib.api.gui.MenuBase;
import org.slf4j.Logger;

import static org.shsts.tinycorelib.test.All.ITEM_HANDLER_CAPABILITY;
import static org.shsts.tinycorelib.test.All.TEST_CAPABILITY;
import static org.shsts.tinycorelib.test.All.TEST_MENU_EVENT;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TestMenu extends MenuBase {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final int CONTENT_HEIGHT = 50;
    public static final int SLOT_SIZE = 18;
    public static final int SPACING = 3;
    public static final int MARGIN = 6;

    public final int endY;

    public TestMenu(Properties properties) {
        super(properties);

        var y = CONTENT_HEIGHT + SPACING;
        var barY = y + 3 * SLOT_SIZE + SPACING;
        for (var j = 0; j < 9; j++) {
            var x = MARGIN + j * SLOT_SIZE;
            addSlot(new Slot(inventory, j, x + 1, barY + 1));
        }
        for (var i = 0; i < 3; i++) {
            for (var j = 0; j < 9; j++) {
                var x = MARGIN + j * SLOT_SIZE;
                var y1 = y + i * SLOT_SIZE;
                addSlot(new Slot(inventory, 9 + i * 9 + j, x + 1, y1 + 1));
            }
        }
        this.endY = barY + SLOT_SIZE + MARGIN;

        assert blockEntity != null;

        addSyncSlot("seconds", () -> new TestPacket(blockEntity));

        var itemHandler = ITEM_HANDLER_CAPABILITY.get(blockEntity);
        var slot = new SlotItemHandler(itemHandler, 0, 111, 32);
        addSlot(slot);

        onEventPacket(TEST_MENU_EVENT, this::onEvent);
    }

    private void onEvent(TestPacket p) {
        LOGGER.info("Menu event: seconds = {}", p.getValue());
        TEST_CAPABILITY.get(blockEntity()).foo();
    }
}
