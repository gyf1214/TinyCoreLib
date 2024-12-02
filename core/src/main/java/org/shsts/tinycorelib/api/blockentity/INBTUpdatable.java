package org.shsts.tinycorelib.api.blockentity;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.Tag;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface INBTUpdatable<T extends Tag> {
    boolean shouldSendUpdate();

    T serializeOnUpdate();

    void deserializeOnUpdate(T tag);

    @SuppressWarnings("unchecked")
    default void deserializeTagOnUpdate(Tag tag) {
        deserializeOnUpdate((T) tag);
    }
}
