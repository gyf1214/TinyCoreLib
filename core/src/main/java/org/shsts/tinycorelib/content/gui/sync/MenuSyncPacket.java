package org.shsts.tinycorelib.content.gui.sync;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import org.shsts.tinycorelib.api.network.IPacket;
import org.shsts.tinycorelib.content.network.Channel;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MenuSyncPacket implements IPacket {
    private final Channel channel;
    private int containerId;
    private int index;
    private IPacket content;

    public MenuSyncPacket(Channel channel) {
        this.channel = channel;
    }

    public MenuSyncPacket(Channel channel, int containerId, int index, IPacket content) {
        this.channel = channel;
        this.containerId = containerId;
        this.index = index;
        this.content = content;
    }

    @Override
    public void serializeToBuf(FriendlyByteBuf buf) {
        buf.writeVarInt(containerId);
        buf.writeVarInt(index);
        channel.serializeMenuSyncPacket(content, buf);
    }

    @Override
    public void deserializeFromBuf(FriendlyByteBuf buf) {
        containerId = buf.readVarInt();
        index = buf.readVarInt();
        content = channel.deserializeMenuSyncPacket(buf);
    }

    public int getContainerId() {
        return containerId;
    }

    public int getIndex() {
        return index;
    }

    public IPacket getContent() {
        return content;
    }
}
