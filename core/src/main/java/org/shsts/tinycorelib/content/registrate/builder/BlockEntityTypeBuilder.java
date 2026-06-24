package org.shsts.tinycorelib.content.registrate.builder;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import org.shsts.tinycorelib.api.blockentity.ICapabilityContainer;
import org.shsts.tinycorelib.api.core.DistLazy;
import org.shsts.tinycorelib.api.registrate.builder.IBlockEntityTypeBuilder;
import org.shsts.tinycorelib.api.registrate.entry.IBlockEntityType;
import org.shsts.tinycorelib.api.registrate.entry.ICapability;
import org.shsts.tinycorelib.content.CoreContents;
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

    public BlockEntityTypeBuilder(Registrate registrate, P parent, String id) {
        super(registrate, registrate.blockEntityTypeHandler, parent, id);
        onCreateObject.add(type -> {
            registerEventManager((SmartBlockEntityType) type);
            for (var capability : capabilities) {
                registerCapability((SmartBlockEntityType) type, capability);
            }
        });
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
        onCreateObject.add(type -> renderer.runOnDist(Dist.CLIENT, () -> provider ->
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

    private void registerEventManager(SmartBlockEntityType type) {
        registrate.capabilityHandler.register(type, CoreContents.EVENT_MANAGER, be -> be);
    }

    private <T> void registerCapability(SmartBlockEntityType type, ICapability<T> capability) {
        registrate.capabilityHandler.register(type, capability);
    }

    @Override
    protected BlockEntityType<?> createObject() {
        assert entry != null;
        var entry1 = (BlockEntityTypeEntry) entry;
        var validBlocks = this.validBlocks.stream()
            .map($ -> (Block) $.get())
            .collect(Collectors.toSet());
        return new SmartBlockEntityType(entry1::create, validBlocks, containers);
    }
}
