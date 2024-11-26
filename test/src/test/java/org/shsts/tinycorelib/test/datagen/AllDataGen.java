package org.shsts.tinycorelib.test.datagen;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.shsts.tinycorelib.test.All;
import org.shsts.tinycorelib.test.TinyCoreLibTest;

import static org.shsts.tinycorelib.test.datagen.TinyDataGenTest.DATA_GEN;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AllDataGen {
    public static void init() {
        DATA_GEN.item(All.TEST_ITEM)
            .model(ctx -> ctx.provider().withExistingParent(ctx.id(), "item/generated")
                .texture("layer0", new ResourceLocation("item/arrow")))
            .tag(TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(TinyCoreLibTest.ID, "test")))
            .build();
    }
}
