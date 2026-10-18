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
    private final IBlockEntityContainer tinyCore;

    public TestBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
        IBlockEntityContainer tinyCore) {
        super(type, pos, state);
        this.tinyCore = tinyCore;
    }

    public IBlockEntityContainer tinyCoreContainer() {
        return tinyCore;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        tinyCore.onLoad();
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        tinyCore.onChunkUnloaded();
    }

    @Override
    public void setRemoved() {
        tinyCore.onRemoved();
        super.setRemoved();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        tinyCore.loadAdditional(tag, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tinyCore.saveAdditional(tag, registries);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        var tag = super.getUpdateTag(registries);
        tinyCore.writeUpdateTag(tag, true, registries);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return tinyCore.createUpdatePacket(super::getUpdateTag).orElse(null);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        tinyCore.handleUpdateTag(tag, registries);
    }

    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet,
        HolderLookup.Provider registries) {
        tinyCore.handleUpdateTag(packet.getTag(), registries);
    }
}
