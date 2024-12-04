package org.shsts.tinycorelib.content.gui.sync;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import org.shsts.tinycorelib.api.gui.IMenuEvent;
import org.shsts.tinycorelib.api.network.IPacket;
import org.shsts.tinycorelib.content.network.Channel;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MenuEventPacket implements IPacket {
    private final Channel channel;
    private int containerId;
    private IMenuEvent<?> event;
    private IPacket content;

    public MenuEventPacket(Channel channel) {
        this.channel = channel;
    }

    public MenuEventPacket(Channel channel, int containerId, IMenuEvent<?> event, IPacket content) {
        this.channel = channel;
        this.containerId = containerId;
        this.event = event;
        this.content = content;
    }

    @Override
    public void serializeToBuf(FriendlyByteBuf buf) {
        buf.writeVarInt(containerId);
        channel.eventPackets.serialize(event, content, buf);
    }

    @Override
    public void deserializeFromBuf(FriendlyByteBuf buf) {
        containerId = buf.readVarInt();
        var entry = channel.eventPackets.deserialize(buf);
        event = entry.key();
        content = entry.packet();
    }

    public int getContainerId() {
        return containerId;
    }

    public IMenuEvent<?> getEvent() {
        return event;
    }

    public IPacket getContent() {
        return content;
    }
}
