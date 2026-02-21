package org.shsts.tinycorelib.test.datagen;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.shsts.tinycorelib.api.meta.IMetaConsumer;
import org.shsts.tinycorelib.api.meta.MetaLoadingException;
import org.slf4j.Logger;

import static org.shsts.tinycorelib.test.TinyCoreLibTest.REGISTRATE;
import static org.shsts.tinycorelib.test.datagen.TinyDataGenTest.DATA_GEN;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TestMetaDataGen implements IMetaConsumer {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void acceptMeta(ResourceLocation loc, JsonObject jo) throws MetaLoadingException {
        var val = jo.get("desc").getAsString();
        LOGGER.info("accept meta loc={}, desc={}", loc, val);

        var id = jo.get("id").getAsString();
        var item = REGISTRATE.getHandler(ForgeRegistries.ITEMS).getEntry(id);
        var tex = new ResourceLocation(jo.get("tex").getAsString());
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
