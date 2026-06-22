package org.shsts.tinycorelib.api.registrate.entry;

import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface ICapability<T> extends IEntry<BlockCapability<T, @Nullable Void>> {
    T get(BlockEntity be);

    Optional<T> tryGet(BlockEntity be);
}
