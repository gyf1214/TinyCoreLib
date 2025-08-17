package org.shsts.tinycorelib.test;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import org.shsts.tinycorelib.api.meta.IMetaConsumer;
import org.shsts.tinycorelib.api.meta.MetaLoadingException;
import org.slf4j.Logger;

import static org.shsts.tinycorelib.test.TinyCoreLibTest.REGISTRATE;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TestMetaConsumer implements IMetaConsumer {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void acceptMeta(ResourceLocation loc, JsonObject jo) throws MetaLoadingException {
        var val = jo.get("desc").getAsString();
        LOGGER.info("accept meta loc={}, desc={}", loc, val);

        var id = jo.get("id").getAsString();
        REGISTRATE.item(id).register();
    }

    @Override
    public String name() {
        return "test meta consumer";
    }
}
