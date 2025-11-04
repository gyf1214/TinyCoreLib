package org.shsts.tinycorelib.api.gui;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import org.shsts.tinycorelib.api.network.IPacket;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IMenuSyncHandler {
    void handleSyncPacket(int index, IPacket packet);
}
