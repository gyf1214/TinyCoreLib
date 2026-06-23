package org.shsts.tinycorelib.content.registrate.builder;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.shsts.tinycorelib.api.network.IPacket;
import org.shsts.tinycorelib.api.network.IPacketType;
import org.shsts.tinycorelib.api.network.PacketDirection;
import org.shsts.tinycorelib.api.registrate.builder.IPacketBuilder;
import org.shsts.tinycorelib.content.common.Builder;
import org.shsts.tinycorelib.content.network.GenericPacketPayload;
import org.shsts.tinycorelib.content.network.PacketPayloadType;
import org.shsts.tinycorelib.content.network.PacketType;
import org.shsts.tinycorelib.content.registrate.Registrate;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PacketBuilder<T extends IPacket, P>
    extends Builder<IPacketType<T>, P, IPacketBuilder<T, P>>
    implements IPacketBuilder<T, P> {
    private final Registrate registrate;
    private final ResourceLocation loc;
    private final Supplier<T> constructor;
    @Nullable
    private PacketDirection direction = null;
    @Nullable
    private BiConsumer<T, IPayloadContext> handler = null;

    public PacketBuilder(Registrate registrate, P parent, String id, Supplier<T> constructor) {
        super(parent);
        this.registrate = registrate;
        this.loc = ResourceLocation.fromNamespaceAndPath(registrate.modid, id);
        this.constructor = constructor;
    }

    @Override
    public IPacketBuilder<T, P> direction(PacketDirection value) {
        direction = value;
        return self();
    }

    @Override
    public IPacketBuilder<T, P> handler(BiConsumer<T, IPayloadContext> value) {
        handler = value;
        return self();
    }

    @Override
    protected IPacketType<T> createObject() {
        if (direction == null) {
            throw new IllegalStateException("Packet %s has no direction".formatted(loc));
        }
        if (handler == null) {
            throw new IllegalStateException("Packet %s has no handler".formatted(loc));
        }
        var type = new PacketType<T, GenericPacketPayload<T>>(loc, direction,
            PacketPayloadType.GENERIC, new CustomPacketPayload.Type<>(loc));
        registrate.payloadHandler.registerGeneric(type, constructor, handler);
        return type;
    }

    @Override
    public IPacketType<T> register() {
        return buildObject();
    }
}
