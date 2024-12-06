package org.shsts.tinycorelib.content.registrate.builder;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import org.shsts.tinycorelib.api.blockentity.ICapabilityFactory;
import org.shsts.tinycorelib.api.core.DistLazy;
import org.shsts.tinycorelib.api.registrate.builder.IBlockEntityTypeBuilder;
import org.shsts.tinycorelib.api.registrate.entry.IBlockEntityType;
import org.shsts.tinycorelib.content.blockentity.SmartBlockEntityType;
import org.shsts.tinycorelib.content.registrate.Registrate;
import org.shsts.tinycorelib.content.registrate.entry.BlockEntityTypeEntry;
import org.shsts.tinycorelib.content.registrate.handler.BlockEntityTypeHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockEntityTypeBuilder<P>
    extends EntryBuilder<BlockEntityType<?>, BlockEntityType<?>, P, IBlockEntityTypeBuilder<P>>
    implements IBlockEntityTypeBuilder<P> {
    private final Set<Supplier<? extends Block>> validBlocks = new HashSet<>();
    private final Map<ResourceLocation, ICapabilityFactory> capabilities = new HashMap<>();

    public BlockEntityTypeBuilder(Registrate registrate, P parent, String id) {
        super(registrate, registrate.blockEntityTypeHandler, parent, id);
    }

    @Override
    public IBlockEntityTypeBuilder<P> validBlock(Supplier<? extends Block> block) {
        validBlocks.add(block);
        return self();
    }

    @Override
    public IBlockEntityTypeBuilder<P> validBlock(List<Supplier<? extends Block>> blocks) {
        validBlocks.addAll(blocks);
        return self();
    }

    @Override
    public IBlockEntityTypeBuilder<P> capability(
        ResourceLocation loc, ICapabilityFactory factory) {
        capabilities.put(loc, factory);
        return self();
    }

    @Override
    public IBlockEntityTypeBuilder<P> capability(String id, ICapabilityFactory factory) {
        return capability(new ResourceLocation(modid(), id), factory);
    }

    @Override
    public IBlockEntityTypeBuilder<P> renderer(
        DistLazy<BlockEntityRendererProvider<BlockEntity>> renderer) {
        onCreateObject(type -> renderer.runOnDist(Dist.CLIENT, () -> provider ->
            registrate.rendererHandler.setBlockEntityRenderer(type, provider)));
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
        var validBlocks = this.validBlocks.stream()
            .map($ -> (Block) $.get())
            .collect(Collectors.toSet());
        return new SmartBlockEntityType(entry1::create, validBlocks, new HashMap<>(capabilities));
    }
}
