package org.shsts.tinycorelib.api.registrate.entry;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ItemCapability;

import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IItemCapability<T> extends IEntry<ItemCapability<T, ?>> {
    T get(ItemStack stack);

    Optional<T> tryGet(ItemStack stack);

    <T1> boolean is(IItemCapability<T1> other);

    T cast(Object obj);
}
