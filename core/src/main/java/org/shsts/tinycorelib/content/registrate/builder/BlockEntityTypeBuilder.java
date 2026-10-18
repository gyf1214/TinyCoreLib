package org.shsts.tinycorelib.content.registrate.builder;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import org.shsts.tinycorelib.api.blockentity.IBlockEntityContainer;
import org.shsts.tinycorelib.api.blockentity.ICapabilityContainer;
import org.shsts.tinycorelib.api.core.DistLazy;
import org.shsts.tinycorelib.api.registrate.builder.IBlockEntityTypeBuilder;
import org.shsts.tinycorelib.api.registrate.entry.IBlockEntityType;
import org.shsts.tinycorelib.api.registrate.entry.ICapability;
import org.shsts.tinycorelib.content.CoreContents;
import org.shsts.tinycorelib.content.blockentity.BlockEntityContainer;
import org.shsts.tinycorelib.content.blockentity.SmartBlockEntity;
import org.shsts.tinycorelib.content.blockentity.SmartBlockEntityType;
import org.shsts.tinycorelib.content.registrate.Registrate;
import org.shsts.tinycorelib.content.registrate.entry.BlockEntityTypeEntry;
import org.shsts.tinycorelib.content.registrate.handler.BlockEntityTypeHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockEntityTypeBuilder<P>
    extends EntryBuilder<BlockEntityType<?>, BlockEntityType<?>, P, IBlockEntityTypeBuilder<P>>
    implements IBlockEntityTypeBuilder<P> {
    private final Set<Supplier<? extends Block>> validBlocks = new HashSet<>();
    private final Set<ICapability<?>> capabilities = new HashSet<>();
    private final Map<ResourceLocation, Function<BlockEntity, ICapabilityContainer>> containers =
        new HashMap<>();
    private IBlockEntityType.Factory<BlockEntity> factory = SmartBlockEntity::new;
    private Function<BlockEntity, IBlockEntityContainer> containerAccessor =
        be -> ((SmartBlockEntity) be).tinyCoreContainer();

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
    public IBlockEntityTypeBuilder<P> capability(ICapability<?>... caps) {
        for (var cap : caps) {
            if (!capabilities.add(cap)) {
                throw new IllegalArgumentException("Duplicate capability declaration " + cap.loc());
            }
        }
        return self();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> IBlockEntityTypeBuilder<P> factory(
        IBlockEntityType.Factory<T> factory,
        Function<? super T, ? extends IBlockEntityContainer> containerAccessor) {
        this.factory = factory::create;
        this.containerAccessor = be -> containerAccessor.apply((T) be);
        return self();
    }

    @Override
    public IBlockEntityTypeBuilder<P> container(
        ResourceLocation loc, Function<BlockEntity, ICapabilityContainer> factory) {
        var old = containers.putIfAbsent(loc, factory);
        if (old != null) {
            throw new IllegalArgumentException("Duplicate container id " + loc);
        }
        return self();
    }

    @Override
    public IBlockEntityTypeBuilder<P> container(
        String id, Function<BlockEntity, ICapabilityContainer> factory) {
        return container(ResourceLocation.fromNamespaceAndPath(modid(), id), factory);
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
        var entry = ((BlockEntityTypeHandler) handler).registerType(this);
        entry.setTicker((world, be) -> containerAccessor.apply(be).tick(world));
        onCreateObject(type -> {
            var type1 = (SmartBlockEntityType) type;
            var accessor = containerAccessor;
            registrate.capabilityHandler.register(type1, CoreContents.EVENT_MANAGER,
                be -> accessor.apply(be));
            for (var capability : capabilities) {
                registerCapability(type1, capability, accessor);
            }
        });
        return entry;
    }

    private <T> void registerCapability(SmartBlockEntityType type, ICapability<T> capability,
        Function<BlockEntity, IBlockEntityContainer> accessor) {
        registrate.capabilityHandler.register(type, capability,
            be -> accessor.apply(be).getCapability(capability).orElse(null));
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
        var factory = this.factory;
        var containerAccessor = this.containerAccessor;
        return new SmartBlockEntityType((pos, state) -> {
            var type = (SmartBlockEntityType) entry1.get();
            var container = new BlockEntityContainer(type);
            var blockEntity = factory.create(type, pos, state, container);
            if (containerAccessor.apply(blockEntity) == null) {
                throw new IllegalStateException(
                    "Block entity container accessor returned null for " + entry1.loc());
            }
            container.initContainers(blockEntity);
            return blockEntity;
        }, validBlocks, containers);
    }
}
