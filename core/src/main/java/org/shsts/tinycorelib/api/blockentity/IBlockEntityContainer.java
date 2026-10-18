package org.shsts.tinycorelib.api.blockentity;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.level.Level;
import org.shsts.tinycorelib.api.registrate.entry.ICapability;

import java.util.Optional;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IBlockEntityContainer extends IEventManager {
    <T> Optional<T> getCapability(ICapability<T> capability);

    void tick(Level level);

    void onLoad();

    void onChunkUnloaded();

    void onRemoved();

    void loadAdditional(CompoundTag tag, HolderLookup.Provider registries);

    void saveAdditional(CompoundTag tag, HolderLookup.Provider registries);

    void writeUpdateTag(CompoundTag tag, boolean force, HolderLookup.Provider registries);

    void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries);

    Optional<Packet<ClientGamePacketListener>> createUpdatePacket(
        Function<HolderLookup.Provider, CompoundTag> parentTagFactory);
}
