package org.shsts.tinycorelib;

import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.shsts.tinycorelib.api.ITinyCoreLib;
import org.shsts.tinycorelib.api.meta.IMetaConsumer;
import org.shsts.tinycorelib.api.meta.IMetaExecutor;
import org.shsts.tinycorelib.api.network.IPacket;
import org.shsts.tinycorelib.api.network.IPacketType;
import org.shsts.tinycorelib.api.recipe.IRecipeManager;
import org.shsts.tinycorelib.api.registrate.IRegistrate;
import org.shsts.tinycorelib.api.registrate.entry.IEntry;
import org.shsts.tinycorelib.content.meta.MetaExecutor;
import org.shsts.tinycorelib.content.meta.MetaLocator;
import org.shsts.tinycorelib.content.network.GenericPacketPayload;
import org.shsts.tinycorelib.content.recipe.SmartRecipeManager;
import org.shsts.tinycorelib.content.registrate.Registrate;
import org.shsts.tinycorelib.content.registrate.entry.Entry;
import org.shsts.tinycorelib.content.registrate.handler.PayloadHandler;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TinyCoreLib implements ITinyCoreLib {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final MetaLocator metaLocator = new MetaLocator();

    public TinyCoreLib() {
        LOGGER.info("Initialize TinyCoreLib API");
    }

    public void scanMeta() {
        metaLocator.scanFiles();
    }

    @Override
    public IRegistrate registrate(String modid) {
        return new Registrate(modid);
    }

    @Override
    public <U> IEntry<U> createEntry(ResourceLocation loc, U obj) {
        return new Entry<>(loc, obj);
    }

    @Override
    public IMetaExecutor registerMeta(String folder, IMetaConsumer consumer) {
        return new MetaExecutor(metaLocator, folder, consumer);
    }

    @Override
    public <P extends IPacket> void sendToServer(IPacketType<P> type, P packet) {
        var packetType = PayloadHandler.requireGeneric(type);
        PayloadHandler.requireSendToServer(packetType.direction());
        PacketDistributor.sendToServer(new GenericPacketPayload<>(packetType, packet));
    }

    @Override
    public <P extends IPacket> void sendToPlayer(ServerPlayer player, IPacketType<P> type,
        P packet) {
        var packetType = PayloadHandler.requireGeneric(type);
        PayloadHandler.requireSendToPlayer(packetType.direction());
        PacketDistributor.sendToPlayer(player, new GenericPacketPayload<>(packetType, packet));
    }

    @Override
    public IRecipeManager recipeManager(Level world) {
        return new SmartRecipeManager(world.getRecipeManager());
    }

    @Override
    public IRecipeManager clientRecipeManager() {
        var connection = Minecraft.getInstance().getConnection();
        assert connection != null;
        return new SmartRecipeManager(connection.getRecipeManager());
    }
}
