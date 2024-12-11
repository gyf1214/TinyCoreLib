package org.shsts.tinycorelib.content.registrate.entry;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.shsts.tinycorelib.api.registrate.entry.IBlockEntityType;
import org.shsts.tinycorelib.content.blockentity.SmartBlockEntity;

import java.util.Optional;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockEntityTypeEntry extends Entry<BlockEntityType<?>> implements IBlockEntityType {
    public BlockEntityTypeEntry(ResourceLocation loc) {
        super(loc);
    }

    public BlockEntityTypeEntry(ResourceLocation loc, Supplier<BlockEntityType<?>> supplier) {
        super(loc, supplier);
    }

    @Override
    public Optional<BlockEntity> get(Level world, BlockPos pos) {
        if (world.isLoaded(pos)) {
            return world.getBlockEntity(pos, get()).map($ -> (BlockEntity) $);
        }
        return Optional.empty();
    }

    @Override
    public SmartBlockEntity create(BlockPos pos, BlockState state) {
        return new SmartBlockEntity(get(), pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> ticker() {
        return (world, pos, state, be) -> SmartBlockEntity.ticker(world, be);
    }
}
