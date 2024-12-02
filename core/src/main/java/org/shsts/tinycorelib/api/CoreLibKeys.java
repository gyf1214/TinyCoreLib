package org.shsts.tinycorelib.api;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityToken;
import org.shsts.tinycorelib.api.blockentity.IEvent;
import org.shsts.tinycorelib.api.blockentity.IEventManager;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class CoreLibKeys {
    public static final String EVENT_REGISTRY_NAME = "event";
    public static final String SERVER_LOAD_NAME = "server_load";
    public static final String CLIENT_LOAD_NAME = "client_load";
    public static final String REMOVED_IN_WORLD_NAME = "removed_in_world";
    public static final String REMOVED_BY_CHUNK_NAME = "removed_by_chunk";
    public static final String CLIENT_TICK_NAME = "client_tick";
    public static final String SERVER_TICK_NAME = "server_tick";
    public static final String SERVER_USE_NAME = "server_use";
    public static final String EVENT_MANAGER_NAME = "event_manager";

    public static final ResourceKey<Registry<IEvent<?>>> EVENT_REGISTRY_KEY =
        ResourceKey.createRegistryKey(modLoc(EVENT_REGISTRY_NAME));
    public static final ResourceLocation SERVER_LOAD_LOC = modLoc(SERVER_LOAD_NAME);
    public static final ResourceLocation CLIENT_LOAD_LOC = modLoc(CLIENT_LOAD_NAME);
    public static final ResourceLocation REMOVED_IN_WORLD_LOC = modLoc(REMOVED_IN_WORLD_NAME);
    public static final ResourceLocation REMOVED_BY_CHUNK_LOC = modLoc(REMOVED_BY_CHUNK_NAME);
    public static final ResourceLocation CLIENT_TICK_LOC = modLoc(CLIENT_TICK_NAME);
    public static final ResourceLocation SERVER_TICK_LOC = modLoc(SERVER_TICK_NAME);
    public static final ResourceLocation SERVER_USE_LOC = modLoc(SERVER_USE_NAME);
    public static final ResourceLocation EVENT_MANAGER_LOC = modLoc(EVENT_MANAGER_NAME);
    public static final CapabilityToken<IEventManager> EVENT_MANAGER_TOKEN = new CapabilityToken<>() {};

    private CoreLibKeys() {}

    private static ResourceLocation modLoc(String id) {
        return new ResourceLocation(ITinyCoreLib.ID, id);
    }
}
