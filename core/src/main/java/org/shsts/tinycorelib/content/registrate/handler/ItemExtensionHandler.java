package org.shsts.tinycorelib.content.registrate.handler;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemExtensionHandler {
    private final Multimap<ResourceKey<CreativeModeTab>, Supplier<? extends ItemStack>> creativeTabs =
        ArrayListMultimap.create();
    private final List<Runnable> clientCallbacks = new ArrayList<>();

    public void addCreativeTabItem(ResourceKey<CreativeModeTab> tab, Supplier<? extends Item> item) {
        creativeTabs.put(tab, () -> new ItemStack(item.get()));
    }

    public void addCreativeTabStack(ResourceKey<CreativeModeTab> tab, Supplier<ItemStack> stack) {
        creativeTabs.put(tab, stack);
    }

    public void onRegisterCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        for (var stack : creativeTabs.get(event.getTabKey())) {
            event.accept(stack.get());
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void addItemProperty(Item item, ResourceLocation loc, ClampedItemPropertyFunction propertyFunc) {
        clientCallbacks.add(() -> ItemProperties.register(item, loc, propertyFunc));
    }

    @OnlyIn(Dist.CLIENT)
    public void onClientSetup() {
        for (var cb : clientCallbacks) {
            cb.run();
        }
        clientCallbacks.clear();
    }
}
