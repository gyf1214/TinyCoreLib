package org.shsts.tinycorelib.api.meta;

import com.google.gson.JsonObject;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IMetaConsumer {
    void acceptMeta(ResourceLocation loc, JsonObject jo) throws MetaLoadingException;

    String name();
}
