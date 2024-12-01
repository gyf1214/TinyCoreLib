package org.shsts.tinycorelib.api;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.shsts.tinycorelib.api.blockentity.IEvent;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class CoreLibKeys {
    public static final String EVENT_REGISTRY_NAME = "event";
    public static final String SERVER_LOAD_NAME = "server_load";
    public static final String CLIENT_LOAD_NAME = "client_load";
    public static final String REMOVED_IN_WORLD_NAME = "removed_in_world";
    public static final String REMOVED_BY_CHUNK_NAME = "removed_by_chunk";
    public static final String SERVER_TICK_NAME = "server_tick";
    public static final String SERVER_USE_NAME = "server_use";

    public static final ResourceKey<Registry<IEvent<?>>> EVENT_REGISTRY_KEY =
        ResourceKey.createRegistryKey(modLoc(EVENT_REGISTRY_NAME));
    public static final ResourceLocation SERVER_LOAD_LOC = modLoc(SERVER_LOAD_NAME);

    private CoreLibKeys() {}

    private static ResourceLocation modLoc(String id) {
        return new ResourceLocation(ITinyCoreLib.ID, id);
    }
}
