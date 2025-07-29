package org.shsts.tinycorelib.content.gui;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import org.shsts.tinycorelib.api.gui.ISyncSlotScheduler;
import org.shsts.tinycorelib.api.network.IPacket;

import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SimpleSyncSlotScheduler<P extends IPacket> implements ISyncSlotScheduler<P> {
    private final Supplier<P> packetFactory;
    @Nullable
    private P packet = null;

    public SimpleSyncSlotScheduler(Supplier<P> packetFactory) {
        this.packetFactory = packetFactory;
    }

    @Override
    public boolean shouldSend() {
        var packet1 = packetFactory.get();
        if (!packet1.equals(packet)) {
            packet = packet1;
            return true;
        }
        return false;
    }

    @Override
    public P createPacket() {
        assert packet != null;
        return packet;
    }
}
