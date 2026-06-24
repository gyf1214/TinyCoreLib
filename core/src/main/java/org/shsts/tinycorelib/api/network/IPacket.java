package org.shsts.tinycorelib.api.network;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.RegistryFriendlyByteBuf;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IPacket {
    void serializeToBuf(RegistryFriendlyByteBuf buf);

    void deserializeFromBuf(RegistryFriendlyByteBuf buf);
}
