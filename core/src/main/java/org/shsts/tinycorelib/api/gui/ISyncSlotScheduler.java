package org.shsts.tinycorelib.api.gui;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import org.shsts.tinycorelib.api.network.IPacket;
import org.shsts.tinycorelib.api.network.IPacketType;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface ISyncSlotScheduler<P extends IPacket> {
    IPacketType<P> packetType();

    boolean shouldSend();

    P createPacket();
}
