package org.shsts.tinycorelib.api.registrate.builder;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.Item;
import org.shsts.tinycorelib.api.core.DistLazy;
import org.shsts.tinycorelib.api.core.Transformer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IItemBuilder<U extends Item, P> extends IEntryBuilder<Item, U, P, IItemBuilder<U, P>> {
    IItemBuilder<U, P> properties(Transformer<Item.Properties> trans);

    IItemBuilder<U, P> tint(DistLazy<ItemColor> color);

    IItemBuilder<U, P> tint(int... colors);
}
