package org.shsts.tinycorelib.api.gui;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import org.shsts.tinycorelib.api.network.IPacket;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IMenuEventHandler {
    void handleEventPacket(IMenuEvent<?> event, IPacket packet);
}
