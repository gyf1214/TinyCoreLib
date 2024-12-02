package org.shsts.tinycorelib.content.registrate;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import org.shsts.tinycorelib.api.registrate.ICapability;

import java.util.NoSuchElementException;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CapabilityEntry<T> extends Entry<Capability<T>> implements ICapability<T> {
    public CapabilityEntry(String modid, CapabilityToken<T> token) {
        super(new ResourceLocation(modid, ""), () -> CapabilityManager.get(token));
    }

    @Override
    public T get(BlockEntity be, @Nullable Direction dir) {
        return be.getCapability(get(), dir)
            .orElseThrow(NoSuchElementException::new);
    }

    @Override
    public T get(BlockEntity be) {
        return get(be, null);
    }

    @Override
    public Optional<T> tryGet(BlockEntity be, @Nullable Direction dir) {
        return be.getCapability(get(), dir).resolve();
    }

    @Override
    public Optional<T> tryGet(BlockEntity be) {
        return tryGet(be, null);
    }
}
