package org.shsts.tinycorelib.test;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TestItem extends Item {
    public TestItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltips,
        TooltipFlag isAdvanced) {
        tooltips.add(new TextComponent("I'm a test item!"));
    }
}
