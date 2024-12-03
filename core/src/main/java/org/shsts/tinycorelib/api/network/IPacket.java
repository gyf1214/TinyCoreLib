package org.shsts.tinycorelib.api.network;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IPacket {
    void serializeToBuf(FriendlyByteBuf buf);

    void deserializeFromBuf(FriendlyByteBuf buf);
}
