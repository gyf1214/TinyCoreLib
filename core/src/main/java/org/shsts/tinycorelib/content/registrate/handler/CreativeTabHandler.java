package org.shsts.tinycorelib.content.registrate.handler;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CreativeTabHandler {
    private final Multimap<ResourceKey<CreativeModeTab>, Supplier<? extends Item>> entries =
        ArrayListMultimap.create();

    public void setCreativeTab(Supplier<? extends Item> item, ResourceKey<CreativeModeTab> tab) {
        entries.put(tab, item);
    }

    public void onRegisterCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        for (var item : entries.get(event.getTabKey())) {
            event.accept(new ItemStack(item.get()));
        }
    }
}
