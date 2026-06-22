package org.shsts.tinycorelib.content.blockentity;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.shsts.tinycorelib.api.blockentity.ICapabilityContainer;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SmartBlockEntityType extends BlockEntityType<SmartBlockEntity> {
    private final Map<ResourceLocation, Function<BlockEntity, ICapabilityContainer>> containers;

    @SuppressWarnings("DataFlowIssue")
    public SmartBlockEntityType(BlockEntitySupplier<SmartBlockEntity> factory,
        Set<Block> validBlocks,
        Map<ResourceLocation, Function<BlockEntity, ICapabilityContainer>> containers) {
        super(factory, validBlocks, null);
        this.containers = Map.copyOf(containers);
    }

    public Map<ResourceLocation, Function<BlockEntity, ICapabilityContainer>> containerFactories() {
        return containers;
    }
}
