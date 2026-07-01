package org.shsts.tinycorelib.content.registrate.entry;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.shsts.tinycorelib.api.registrate.entry.ICapability;

import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CapabilityEntry<T> extends Entry<BlockCapability<T, ?>>
    implements ICapability<T> {
    public CapabilityEntry(String modid, String id, Class<T> typeClass) {
        this(ResourceLocation.fromNamespaceAndPath(modid, id), typeClass);
    }

    public CapabilityEntry(ResourceLocation loc, Class<T> typeClass) {
        super(loc, () -> BlockCapability.createVoid(loc, typeClass));
    }

    public CapabilityEntry(BlockCapability<T, ?> cap) {
        super(cap.name(), cap);
    }

    @Override
    public T get(BlockEntity be) {
        return tryGet(be).orElseThrow();
    }

    @Override
    public Optional<T> tryGet(BlockEntity be) {
        var world = be.getLevel();
        if (world == null) {
            return Optional.empty();
        }
        BlockCapability<T, ?> cap = get();
        BlockPos pos = be.getBlockPos();
        return Optional.ofNullable(world.getCapability(cap, pos, null));
    }
}
