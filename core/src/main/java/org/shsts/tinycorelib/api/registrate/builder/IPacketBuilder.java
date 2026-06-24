package org.shsts.tinycorelib.api.registrate.builder;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.shsts.tinycorelib.api.core.IBuilder;
import org.shsts.tinycorelib.api.network.IPacket;
import org.shsts.tinycorelib.api.network.IPacketType;
import org.shsts.tinycorelib.api.network.PacketDirection;

import java.util.function.BiConsumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IPacketBuilder<T extends IPacket, P>
    extends IBuilder<IPacketType<T>, P, IPacketBuilder<T, P>> {
    IPacketBuilder<T, P> direction(PacketDirection value);

    IPacketBuilder<T, P> handler(BiConsumer<T, IPayloadContext> value);

    IPacketType<T> register();
}
