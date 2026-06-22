package org.shsts.tinycorelib.api.network;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import org.shsts.tinycorelib.api.core.ILoc;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IPacketType<P extends IPacket> extends ILoc {
    @Override
    ResourceLocation loc();

    PacketDirection direction();
}
