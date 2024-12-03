package org.shsts.tinycorelib.api.registrate.entry;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;

import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface ICapability<T> extends IEntry<Capability<T>> {
    T get(BlockEntity be, @Nullable Direction dir);

    T get(BlockEntity be);

    Optional<T> tryGet(BlockEntity be, @Nullable Direction dir);

    Optional<T> tryGet(BlockEntity be);
}
