package org.shsts.tinycorelib.datagen.test;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import org.shsts.tinycorelib.api.meta.IMetaConsumer;
import org.shsts.tinycorelib.api.meta.MetaLoadingException;
import org.slf4j.Logger;

import static org.shsts.tinycorelib.datagen.test.TinyDataGenTest.DATA_GEN;
import static org.shsts.tinycorelib.test.TinyCoreLibTest.REGISTRATE;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TestMetaDataGen implements IMetaConsumer {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void acceptMeta(ResourceLocation loc, JsonObject jo) throws MetaLoadingException {
        var val = jo.get("desc").getAsString();
        LOGGER.info("accept meta loc={}, desc={}", loc, val);

        var id = jo.get("id").getAsString();
        var item = REGISTRATE.getHandler(Registries.ITEM, BuiltInRegistries.ITEM).getEntry(id);
        var tex = ResourceLocation.parse(jo.get("tex").getAsString());
        DATA_GEN.item(item)
            .model(ctx -> ctx.provider()
                .withExistingParent(ctx.id(), "item/generated")
                .texture("layer0", tex))
            .build();
    }

    @Override
    public String name() {
        return "test meta datagen";
    }
}
