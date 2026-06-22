package org.shsts.tinycorelib.api.gui;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import org.shsts.tinycorelib.api.network.IPacket;
import org.shsts.tinycorelib.api.network.IPacketType;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IMenuSyncHandler {
    <P extends IPacket> void handleSyncPacket(IPacketType<P> type, int index, P packet);
}
