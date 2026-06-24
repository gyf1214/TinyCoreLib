package org.shsts.tinycorelib.api.gui;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import org.shsts.tinycorelib.api.network.IPacket;
import org.shsts.tinycorelib.api.network.IPacketType;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IMenuEventHandler {
    <P extends IPacket> void handleEventPacket(IPacketType<P> type, P packet);
}
