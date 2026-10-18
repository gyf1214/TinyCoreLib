package org.shsts.tinycorelib.test;

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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.shsts.tinycorelib.api.blockentity.IBlockEntityContainer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TestBlockEntity extends BlockEntity {
    private final IBlockEntityContainer container;

    public TestBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
        IBlockEntityContainer container) {
        super(type, pos, state);
        this.container = container;
    }

    public IBlockEntityContainer container() {
        return container;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        container.onLoad();
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        container.onChunkUnloaded();
    }

    @Override
    public void setRemoved() {
        container.onRemoved();
        super.setRemoved();
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
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        container.handleUpdateTag(tag, registries);
    }

    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet,
        HolderLookup.Provider registries) {
        container.handleUpdateTag(packet.getTag(), registries);
    }
}
