package org.shsts.tinycorelib.content.network;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public enum PacketPayloadType {
    GENERIC,
    MENU_SYNC,
    MENU_EVENT
}
