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
public class TestPacket implements IPacket {
    private int value;

    public TestPacket() {}

    public TestPacket(int value) {
        this.value = value;
    }

    public TestPacket(BlockEntity be) {
        this.value = TEST_CAPABILITY.get(be).getSeconds();
    }

    @Override
    public void serializeToBuf(FriendlyByteBuf buf) {
        buf.writeVarInt(value);
    }

    @Override
    public void deserializeFromBuf(FriendlyByteBuf buf) {
        value = buf.readVarInt();
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TestPacket that)) {
            return false;
        }
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
