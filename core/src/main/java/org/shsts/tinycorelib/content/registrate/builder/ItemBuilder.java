package org.shsts.tinycorelib.content.registrate.builder;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import org.shsts.tinycorelib.api.core.DistLazy;
import org.shsts.tinycorelib.api.core.Transformer;
import org.shsts.tinycorelib.api.registrate.builder.IItemBuilder;
import org.shsts.tinycorelib.api.registrate.entry.IItemCapability;
import org.shsts.tinycorelib.content.registrate.Registrate;
import org.shsts.tinycorelib.content.registrate.entry.Entry;

import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemBuilder<U extends Item, P> extends EntryBuilder<Item, U, P, IItemBuilder<U, P>>
    implements IItemBuilder<U, P> {
    private final Function<Item.Properties, U> factory;
    private Transformer<Item.Properties> properties = $ -> $;
    @Nullable
    protected DistLazy<ItemColor> tint = null;

    public ItemBuilder(Registrate registrate, P parent, String id,
        Function<Item.Properties, U> factory) {
        super(registrate, registrate.getHandler(Registries.ITEM, BuiltInRegistries.ITEM), parent, id);
        this.factory = factory;
    }

    @Override
    public IItemBuilder<U, P> properties(Transformer<Item.Properties> trans) {
        properties = properties.chain(trans);
        return self();
    }

    @Override
    public IItemBuilder<U, P> creativeTab(ResourceKey<CreativeModeTab> tab) {
        onCreateObject(item -> registrate.itemExtensionHandler.addCreativeTabItem(tab, () -> item));
        return self();
    }

    @Override
    public IItemBuilder<U, P> creativeTab(ResourceKey<CreativeModeTab> tab, Function<U, ItemStack> stack) {
        onCreateObject(item -> registrate.itemExtensionHandler.addCreativeTabStack(tab, () -> stack.apply(item)));
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
    public IItemBuilder<U, P> itemProperty(String id, DistLazy<ClampedItemPropertyFunction> property) {
        return itemProperty(ResourceLocation.fromNamespaceAndPath(modid(), id), property);
    }

    @Override
    public IItemBuilder<U, P> itemProperty(ResourceLocation loc, DistLazy<ClampedItemPropertyFunction> property) {
        onCreateObject(item -> property.runOnDist(Dist.CLIENT, () -> propertyFunc ->
            registrate.itemExtensionHandler.addItemProperty(item, loc, propertyFunc)));
        return self();
    }

    @Override
    public IItemBuilder<U, P> capability(IItemCapability<?>... caps) {
        for (var cap : caps) {
            onCreateObject(item -> registrate.capabilityHandler.registerItem(item, cap));
        }
        return self();
    }

    @Override
    protected Entry<U> createEntry() {
        var tint = this.tint;
        if (tint != null) {
            onCreateObject(item -> tint.runOnDist(Dist.CLIENT, () -> itemColor ->
                registrate.tintHandler.addItemColor(item, itemColor)));
        }
        return super.createEntry();
    }

    @Override
    protected U createObject() {
        return factory.apply(properties.apply(new Item.Properties()));
    }
}
