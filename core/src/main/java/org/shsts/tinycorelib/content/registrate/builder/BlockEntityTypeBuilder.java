package org.shsts.tinycorelib.content.registrate.builder;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.shsts.tinycorelib.api.blockentity.ICapabilityFactory;
import org.shsts.tinycorelib.api.registrate.IBlockEntityType;
import org.shsts.tinycorelib.api.registrate.builder.IBlockEntityTypeBuilder;
import org.shsts.tinycorelib.content.blockentity.SmartBlockEntityType;
import org.shsts.tinycorelib.content.registrate.BlockEntityTypeEntry;
import org.shsts.tinycorelib.content.registrate.Registrate;
import org.shsts.tinycorelib.content.registrate.handler.BlockEntityTypeHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockEntityTypeBuilder<P>
    extends EntryBuilder<BlockEntityType<?>, BlockEntityType<?>, P, IBlockEntityTypeBuilder<P>>
    implements IBlockEntityTypeBuilder<P> {
    private final Set<Block> validBlocks = new HashSet<>();
    private final Map<ResourceLocation, ICapabilityFactory> capabilities = new HashMap<>();

    public BlockEntityTypeBuilder(Registrate registrate, P parent, String id) {
        super(registrate, registrate.blockEntityTypeHandler, parent, id);
    }

    @Override
    public IBlockEntityTypeBuilder<P> validBlock(Block block) {
        validBlocks.add(block);
        return self();
    }

    @Override
    public IBlockEntityTypeBuilder<P> validBlock(Block... blocks) {
        validBlocks.addAll(Arrays.asList(blocks));
        return self();
    }

    @Override
    public IBlockEntityTypeBuilder<P> capability(
        ResourceLocation loc, ICapabilityFactory factory) {
        capabilities.put(loc, factory);
        return self();
    }

    @Override
    protected BlockEntityTypeEntry createEntry() {
        return ((BlockEntityTypeHandler) handler).registerType(this);
    }

    @Override
    public IBlockEntityType register() {
        return (IBlockEntityType) super.register();
    }

    @Override
    protected BlockEntityType<?> createObject() {
        assert entry != null;
        var entry1 = (BlockEntityTypeEntry) entry;
        return new SmartBlockEntityType(entry1::create, validBlocks, capabilities);
    }
}
