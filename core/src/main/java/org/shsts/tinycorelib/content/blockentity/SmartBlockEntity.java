package org.shsts.tinycorelib.content.blockentity;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.shsts.tinycorelib.api.blockentity.ICapabilityContainer;
import org.shsts.tinycorelib.api.blockentity.IBlockEntityContainer;
import org.shsts.tinycorelib.api.blockentity.IEvent;
import org.shsts.tinycorelib.api.blockentity.IEventManager;
import org.shsts.tinycorelib.api.blockentity.IReturnEvent;
import org.shsts.tinycorelib.api.registrate.entry.ICapability;

import java.util.Optional;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SmartBlockEntity extends BlockEntity implements IEventManager {
    private final IBlockEntityContainer container;

    public SmartBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
        IBlockEntityContainer container) {
        super(type, pos, state);
        this.container = container;
    }

    public static SmartBlockEntity create(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        var smartType = (SmartBlockEntityType) type;
        var container = new BlockEntityContainer(smartType);
        var ret = new SmartBlockEntity(type, pos, state, container);
        container.initContainers(ret);
        return ret;
    }

    @Nullable
    public <T> T getCapability(ICapability<T> capability) {
        return container.getCapability(capability).orElse(null);
    }

    @Override
    public <T extends ICapabilityContainer> T getContainer(ResourceLocation loc, Class<T> clazz) {
        return container.getContainer(loc, clazz);
    }

    @Override
    public <T extends ICapabilityContainer> Optional<T> tryGetContainer(
        ResourceLocation loc, Class<T> clazz) {
        return container.tryGetContainer(loc, clazz);
    }

    @Override
    public <A> void invoke(IEvent<A> event, A arg) {
        container.invoke(event, arg);
    }

    @Override
    public <A, R> R invokeReturn(IReturnEvent<A, R> event, A arg) {
        return container.invokeReturn(event, arg);
    }

    @Override
    public <A> void subscribe(IEvent<A> event, Consumer<A> handler) {
        container.subscribe(event, handler);
    }

    @Override
    public <A, R> void subscribe(IReturnEvent<A, R> event, IReturnEvent.Handler<A, R> handler) {
        container.subscribe(event, handler);
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        container.onChunkUnloaded();
    }

    @Override
    public final void setRemoved() {
        container.onRemoved();
        super.setRemoved();
    }

    private void onTick(Level world) {
        container.tick(world);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        container.onLoad();
    }

    public static <T extends BlockEntity> void ticker(Level world, T be) {
        if (be instanceof SmartBlockEntity sbe) {
            sbe.onTick(world);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        container.loadAdditional(tag, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        container.saveAdditional(tag, registries);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        var tag = super.getUpdateTag(registries);
        container.writeUpdateTag(tag, true, registries);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return container.createUpdatePacket(super::getUpdateTag).orElse(null);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        container.handleUpdateTag(tag, lookupProvider);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt,
        HolderLookup.Provider lookupProvider) {
        container.handleUpdateTag(pkt.getTag(), lookupProvider);
    }

    @Override
    public String toString() {
        var dimension = level == null ? null : level.dimension().location();
        return getClass().getSimpleName() + "[pos=" + worldPosition + "@" + dimension + "]";
    }
}
