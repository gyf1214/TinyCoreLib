package org.shsts.tinycorelib.content.blockentity;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import org.shsts.tinycorelib.api.blockentity.IEvent;

import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class Event<A> implements IEvent<A> {
    @SuppressWarnings("unchecked")
    public void invoke(Consumer<?> handler, A arg) {
        ((Consumer<A>) handler).accept(arg);
    }
}
