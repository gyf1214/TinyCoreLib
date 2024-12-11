package org.shsts.tinycorelib.api.blockentity;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import java.util.Optional;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IEventManager {
    <T extends ICapabilityProvider> T getProvider(ResourceLocation loc, Class<T> clazz);

    <T extends ICapabilityProvider> Optional<T> tryGetProvider(ResourceLocation loc, Class<T> clazz);

    <A> void invoke(IEvent<A> event, A arg);

    default void invoke(IEvent<Unit> event) {
        invoke(event, Unit.INSTANCE);
    }

    <A, R> R invokeReturn(IReturnEvent<A, R> event, A arg);

    <A> void subscribe(IEvent<A> event, Consumer<A> handler);

    default void subscribe(IEvent<Unit> event, Runnable handler) {
        subscribe(event, $ -> handler.run());
    }

    <A, R> void subscribe(IReturnEvent<A, R> event, IReturnEvent.Handler<A, R> handler);
}
