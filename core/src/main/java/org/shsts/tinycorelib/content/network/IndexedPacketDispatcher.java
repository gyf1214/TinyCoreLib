package org.shsts.tinycorelib.content.network;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import org.shsts.tinycorelib.api.network.IPacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class IndexedPacketDispatcher<K> {
    private record Registry<K, P extends IPacket>(K key, Supplier<P> constructor) {
        public P create() {
            return constructor.get();
        }
    }

    private final List<Registry<K, ?>> registries = new ArrayList<>();
    private final Map<K, Integer> ids = new HashMap<>();

    public record Entry<K>(K key, IPacket packet) {}

    public <K1 extends K, P extends IPacket> K1 register(IntFunction<K1> keyFactory,
        Supplier<P> constructor) {
        var id = registries.size();
        var key = keyFactory.apply(id);
        registries.add(new Registry<>(key, constructor));
        ids.put(key, id);
        return key;
    }

    public <P extends IPacket> void register(K key, Supplier<P> constructor) {
        register($ -> key, constructor);
    }

    public void serialize(K key, IPacket packet, FriendlyByteBuf buf) {
        var id = ids.get(key);
        buf.writeVarInt(id);
        packet.serializeToBuf(buf);
    }

    public Entry<K> deserialize(FriendlyByteBuf buf) {
        var id = buf.readVarInt();
        var registry = registries.get(id);
        var packet = registry.create();
        packet.deserializeFromBuf(buf);
        return new Entry<>(registry.key, packet);
    }
}
