package org.shsts.tinycorelib.content.blockentity;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.shsts.tinycorelib.api.blockentity.ICapabilityFactory;

import java.util.Map;
import java.util.Set;

import static org.shsts.tinycorelib.api.CoreLibKeys.EVENT_MANAGER_LOC;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SmartBlockEntityType extends BlockEntityType<SmartBlockEntity> {
    private final Map<ResourceLocation, ICapabilityFactory> capabilities;

    @SuppressWarnings("DataFlowIssue")
    public SmartBlockEntityType(BlockEntitySupplier<SmartBlockEntity> factory,
        Set<Block> validBlocks, Map<ResourceLocation, ICapabilityFactory> capabilities) {
        super(factory, validBlocks, null);
        this.capabilities = capabilities;
    }

    public void attachCapabilities(AttachCapabilitiesEvent<BlockEntity> e) {
        var be = e.getObject();
        var eventManager = new EventManager();
        e.addCapability(EVENT_MANAGER_LOC, eventManager);
        eventManager.addProvider(EVENT_MANAGER_LOC, eventManager);
        for (var entry : capabilities.entrySet()) {
            var provider = entry.getValue().create(be);
            e.addCapability(entry.getKey(), provider);
            eventManager.addProvider(entry.getKey(), provider);
        }
    }
}
