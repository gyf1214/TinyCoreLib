package org.shsts.tinycorelib.api.registrate.builder;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.shsts.tinycorelib.api.core.DistLazy;
import org.shsts.tinycorelib.api.core.Transformer;
import org.shsts.tinycorelib.api.registrate.entry.IItemCapability;

import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IItemBuilder<U extends Item, P> extends IEntryBuilder<Item, U, P, IItemBuilder<U, P>> {
    IItemBuilder<U, P> properties(Transformer<Item.Properties> trans);

    IItemBuilder<U, P> creativeTab(ResourceKey<CreativeModeTab> tab);

    IItemBuilder<U, P> creativeTab(ResourceKey<CreativeModeTab> tab, Function<U, ItemStack> stack);

    IItemBuilder<U, P> tint(DistLazy<ItemColor> color);

    IItemBuilder<U, P> tint(int... colors);

    IItemBuilder<U, P> itemProperty(String id, DistLazy<ClampedItemPropertyFunction> property);

    IItemBuilder<U, P> itemProperty(ResourceLocation loc, DistLazy<ClampedItemPropertyFunction> property);

    IItemBuilder<U, P> capability(IItemCapability<?>... caps);
}
