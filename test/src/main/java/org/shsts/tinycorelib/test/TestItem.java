package org.shsts.tinycorelib.test;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.shsts.tinycorelib.api.item.ICapabilityItem;
import org.shsts.tinycorelib.api.registrate.entry.IItemCapability;

import java.util.List;

import static org.shsts.tinycorelib.test.All.TEST_ITEM_CAPABILITY;
import static org.shsts.tinycorelib.test.All.TEST_ITEM_COMPONENT;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TestItem extends Item implements ICapabilityItem {
    public TestItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltips,
        TooltipFlag isAdvanced) {
        tooltips.add(Component.literal("I'm a test item!"));
        tooltips.add(Component.literal("seconds: " + stack.getOrDefault(TEST_ITEM_COMPONENT, 0)));
    }

    private record TestCapability(ItemStack stack) implements ITestCapability {
        @Override
        public void foo() {
            stack.update(TEST_ITEM_COMPONENT, 0, i -> i + 1);
        }

        @Override
        public int getSeconds() {
            return stack.getOrDefault(TEST_ITEM_COMPONENT, 0);
        }
    }

    @Override
    public @Nullable <T> T getCapability(ItemStack stack, IItemCapability<T> capability) {
        if (capability.is(TEST_ITEM_CAPABILITY)) {
            return capability.cast(new TestCapability(stack));
        }
        return null;
    }
}
