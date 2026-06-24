package org.shsts.tinycorelib.api.network;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public enum PacketDirection {
    CLIENTBOUND,
    SERVERBOUND,
    BIDIRECTIONAL
}
