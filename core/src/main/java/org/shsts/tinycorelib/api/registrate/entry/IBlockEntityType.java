package org.shsts.tinycorelib.api.registrate.entry;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.shsts.tinycorelib.api.blockentity.IBlockEntityContainer;

import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IBlockEntityType extends IEntry<BlockEntityType<?>> {
    @FunctionalInterface
    interface Factory<T extends BlockEntity> {
        T create(BlockEntityType<?> type, BlockPos pos, BlockState state,
            IBlockEntityContainer container);
    }

    Optional<BlockEntity> get(Level world, BlockPos pos);

    BlockEntity create(BlockPos pos, BlockState state);

    <T extends BlockEntity> BlockEntityTicker<T> ticker();
}
