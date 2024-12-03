package org.shsts.tinycorelib.content.registrate.builder;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.registries.ForgeRegistries;
import org.shsts.tinycorelib.api.core.DistLazy;
import org.shsts.tinycorelib.api.core.Transformer;
import org.shsts.tinycorelib.api.registrate.builder.IItemBuilder;
import org.shsts.tinycorelib.content.registrate.Registrate;
import org.shsts.tinycorelib.content.registrate.entry.Entry;

import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemBuilder<U extends Item, P> extends EntryBuilder<Item, U, P, IItemBuilder<U, P>>
    implements IItemBuilder<U, P> {
    private final Function<Item.Properties, U> factory;
    private Transformer<Item.Properties> properties = $ -> $.tab(CreativeModeTab.TAB_MISC);
    @Nullable
    protected DistLazy<ItemColor> tint = null;

    public ItemBuilder(Registrate registrate, P parent, String id,
        Function<Item.Properties, U> factory) {
        super(registrate, registrate.getHandler(ForgeRegistries.ITEMS), parent, id);
        this.factory = factory;
    }

    @Override
    public IItemBuilder<U, P> properties(Transformer<Item.Properties> trans) {
        properties = properties.chain(trans);
        return self();
    }

    @Override
    public IItemBuilder<U, P> tint(DistLazy<ItemColor> color) {
        tint = color;
        return self();
    }

    @Override
    public IItemBuilder<U, P> tint(int... colors) {
        return tint(() -> () -> ($, index) -> index < colors.length ? colors[index] : 0xFFFFFFFF);
    }

    @Override
    protected Entry<U> createEntry() {
        var tint = this.tint;
        if (tint != null) {
            onCreateObject.add(item -> tint.runOnDist(Dist.CLIENT, () -> itemColor ->
                registrate.tintHandler.addItemColor(item, itemColor)));
        }
        return super.createEntry();
    }

    @Override
    protected U createObject() {
        return factory.apply(properties.apply(new Item.Properties()));
    }
}
