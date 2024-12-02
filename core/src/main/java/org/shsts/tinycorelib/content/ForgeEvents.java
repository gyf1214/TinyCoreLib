package org.shsts.tinycorelib.content;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.shsts.tinycorelib.content.blockentity.SmartBlockEntityType;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class ForgeEvents {
    @SubscribeEvent
    public static void onAttachBlockEntity(AttachCapabilitiesEvent<BlockEntity> event) {
        if (event.getObject().getType() instanceof SmartBlockEntityType type) {
            type.attachCapabilities(event);
        }
    }
}
