package org.shsts.tinycorelib.content.registrate.entry;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.shsts.tinycorelib.api.registrate.entry.IItemCapability;

import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemCapabilityEntry<T> extends Entry<ItemCapability<T, ?>> implements IItemCapability<T> {
    public ItemCapabilityEntry(String modid, String id, Class<T> typeClass) {
        this(ResourceLocation.fromNamespaceAndPath(modid, id), typeClass);
    }

    public ItemCapabilityEntry(ResourceLocation loc, Class<T> typeClass) {
        super(loc, () -> ItemCapability.createVoid(loc, typeClass));
    }

    public ItemCapabilityEntry(ItemCapability<T, ?> cap) {
        super(cap.name(), cap);
    }

    @Override
    public T get(ItemStack stack) {
        return tryGet(stack).orElseThrow();
    }

    @Override
    public Optional<T> tryGet(ItemStack stack) {
        return Optional.ofNullable(stack.getCapability(get(), null));
    }

    @Override
    public <T1> boolean is(IItemCapability<T1> other) {
        return loc().equals(other.loc());
    }

    @Override
    public T cast(Object obj) {
        return get().typeClass().cast(obj);
    }
}
