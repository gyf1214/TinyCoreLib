package org.shsts.tinycorelib.test;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.shsts.tinycorelib.api.network.IPacket;

import java.util.Objects;

import static org.shsts.tinycorelib.test.All.TEST_CAPABILITY;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TestSyncPacket implements IPacket {
    private int seconds;

    public TestSyncPacket() {}

    public TestSyncPacket(BlockEntity be) {
        this.seconds = TEST_CAPABILITY.get(be).getSeconds();
    }

    @Override
    public void serializeToBuf(FriendlyByteBuf buf) {
        buf.writeVarInt(seconds);
    }

    @Override
    public void deserializeFromBuf(FriendlyByteBuf buf) {
        seconds = buf.readVarInt();
    }

    public int getSeconds() {
        return seconds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TestSyncPacket that)) {
            return false;
        }
        return seconds == that.seconds;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(seconds);
    }
}
