package org.shsts.tinycorelib.content;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import org.shsts.tinycorelib.api.ITinyCoreLib;
import org.shsts.tinycorelib.api.blockentity.IEvent;
import org.shsts.tinycorelib.api.blockentity.IEventManager;
import org.shsts.tinycorelib.api.registrate.entry.ICapability;
import org.shsts.tinycorelib.api.registrate.entry.IEntry;
import org.shsts.tinycorelib.api.registrate.entry.IRegistry;
import org.shsts.tinycorelib.content.recipe.NullRecipe;
import org.shsts.tinycorelib.content.registrate.Registrate;

import static org.shsts.tinycorelib.api.CoreLibKeys.CLIENT_LOAD_NAME;
import static org.shsts.tinycorelib.api.CoreLibKeys.CLIENT_TICK_NAME;
import static org.shsts.tinycorelib.api.CoreLibKeys.EVENT_MANAGER_NAME;
import static org.shsts.tinycorelib.api.CoreLibKeys.EVENT_REGISTRY_NAME;
import static org.shsts.tinycorelib.api.CoreLibKeys.NULL_RECIPE_NAME;
import static org.shsts.tinycorelib.api.CoreLibKeys.REMOVED_BY_CHUNK_NAME;
import static org.shsts.tinycorelib.api.CoreLibKeys.REMOVED_IN_WORLD_NAME;
import static org.shsts.tinycorelib.api.CoreLibKeys.SERVER_LOAD_NAME;
import static org.shsts.tinycorelib.api.CoreLibKeys.SERVER_TICK_NAME;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class CoreContents {
    public static final Registrate REGISTRATE;

    public static final IRegistry<IEvent<?>> EVENT_REGISTRY;

    public static final IEntry<IEvent<Level>> SERVER_LOAD;
    public static final IEntry<IEvent<Level>> CLIENT_LOAD;
    public static final IEntry<IEvent<Level>> REMOVED_IN_WORLD;
    public static final IEntry<IEvent<Level>> REMOVED_BY_CHUNK;
    public static final IEntry<IEvent<Level>> SERVER_TICK;
    public static final IEntry<IEvent<Level>> CLIENT_TICK;

    public static final ICapability<IEventManager> EVENT_MANAGER;
    public static final IEntry<NullRecipe.Serializer> NULL_RECIPE_SERIALIZER;

    static {
        REGISTRATE = new Registrate(ITinyCoreLib.ID);
        EVENT_REGISTRY = REGISTRATE.<IEvent<?>>genericRegistry(EVENT_REGISTRY_NAME, IEvent.class)
            .register();

        SERVER_LOAD = REGISTRATE.event(SERVER_LOAD_NAME);
        CLIENT_LOAD = REGISTRATE.event(CLIENT_LOAD_NAME);
        REMOVED_IN_WORLD = REGISTRATE.event(REMOVED_IN_WORLD_NAME);
        REMOVED_BY_CHUNK = REGISTRATE.event(REMOVED_BY_CHUNK_NAME);
        SERVER_TICK = REGISTRATE.event(SERVER_TICK_NAME);
        CLIENT_TICK = REGISTRATE.event(CLIENT_TICK_NAME);

        EVENT_MANAGER = REGISTRATE.capability(EVENT_MANAGER_NAME, IEventManager.class);
        NULL_RECIPE_SERIALIZER = REGISTRATE.registryEntry(
            REGISTRATE.getHandler(Registries.RECIPE_SERIALIZER),
            NULL_RECIPE_NAME, () -> NullRecipe.Serializer.INSTANCE);
    }

    public static void init() {}
}
