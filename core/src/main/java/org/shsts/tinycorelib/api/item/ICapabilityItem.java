package org.shsts.tinycorelib.api.item;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;
import org.shsts.tinycorelib.api.registrate.entry.IItemCapability;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface ICapabilityItem {
    @Nullable
    <T> T getCapability(ItemStack stack, IItemCapability<T> capability);
}
