package org.shsts.tinycorelib.content.gui;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkHooks;
import org.shsts.tinycorelib.api.gui.MenuBase;
import org.shsts.tinycorelib.api.network.IChannel;

import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SmartMenuType<M extends MenuBase> extends MenuType<M> {
    @Nullable
    private final IChannel channel;
    private final Function<BlockEntity, Component> title;
    private final Function<MenuBase.Properties, M> factory;

    @SuppressWarnings("DataFlowIssue")
    public SmartMenuType(@Nullable IChannel channel, Function<BlockEntity, Component> title,
        Function<MenuBase.Properties, M> factory) {
        super(null);
        this.channel = channel;
        this.title = title;
        this.factory = factory;
    }

    @Override
    public M create(int containerId, Inventory pPlayerInventory) {
        throw new IllegalStateException();
    }

    private static BlockEntity getBlockEntityFromData(FriendlyByteBuf data) {
        var pos = data.readBlockPos();
        var level = Minecraft.getInstance().level;
        assert level != null;
        var be = level.getBlockEntity(pos);
        assert be != null;
        return be;
    }

    private M create(int containerId, Inventory inventory, BlockEntity be) {
        return factory.apply(new MenuBase.Properties(this, containerId, inventory, be, channel));
    }

    @Override
    public M create(int containerId, Inventory inventory, FriendlyByteBuf data) {
        var be = getBlockEntityFromData(data);
        return create(containerId, inventory, be);
    }

    public void open(ServerPlayer player, BlockPos pos) {
        var be = player.level.getBlockEntity(pos);
        if (be == null) {
            return;
        }
        var provider = new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return title.apply(be);
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId,
                Inventory inventory, Player player) {
                return create(containerId, inventory, be);
            }
        };
        NetworkHooks.openGui(player, provider, pos);
    }
}
