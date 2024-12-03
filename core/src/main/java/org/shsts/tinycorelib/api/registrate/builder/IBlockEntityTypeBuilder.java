package org.shsts.tinycorelib.api.registrate.builder;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.shsts.tinycorelib.api.blockentity.ICapabilityFactory;
import org.shsts.tinycorelib.api.registrate.entry.IBlockEntityType;

import java.util.List;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IBlockEntityTypeBuilder<P>
    extends IEntryBuilder<BlockEntityType<?>, BlockEntityType<?>, P, IBlockEntityTypeBuilder<P>> {
    IBlockEntityTypeBuilder<P> validBlock(Supplier<? extends Block> block);

    IBlockEntityTypeBuilder<P> validBlock(List<Supplier<? extends Block>> blocks);

    IBlockEntityTypeBuilder<P> capability(ResourceLocation loc, ICapabilityFactory factory);

    IBlockEntityTypeBuilder<P> capability(String id, ICapabilityFactory factory);

    @Override
    IBlockEntityType register();
}
