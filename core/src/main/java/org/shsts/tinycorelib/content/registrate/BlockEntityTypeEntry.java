package org.shsts.tinycorelib.content.registrate;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.shsts.tinycorelib.api.registrate.IBlockEntityType;
import org.shsts.tinycorelib.content.blockentity.SmartBlockEntity;

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
    public SmartBlockEntity create(BlockPos pos, BlockState state) {
        return new SmartBlockEntity(get(), pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> ticker() {
        return SmartBlockEntity::ticker;
    }
}
