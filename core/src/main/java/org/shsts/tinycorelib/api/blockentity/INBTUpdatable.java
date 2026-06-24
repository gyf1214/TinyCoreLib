package org.shsts.tinycorelib.api.blockentity;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface INBTUpdatable<T extends Tag> {
    boolean shouldSendUpdate();

    T serializeOnUpdate(HolderLookup.Provider provider);

    void deserializeOnUpdate(HolderLookup.Provider provider, T tag);

    @SuppressWarnings("unchecked")
    default void deserializeTagOnUpdate(HolderLookup.Provider provider, Tag tag) {
        deserializeOnUpdate(provider, (T) tag);
    }
}
