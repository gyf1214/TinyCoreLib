package org.shsts.tinycorelib.test.datagen;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
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
            .tag(itemTag("test"))
            .build();
    }

    private static TagKey<Block> blockTag(String id) {
        return TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(TinyCoreLibTest.ID, id));
    }

    private static TagKey<Item> itemTag(String id) {
        return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(TinyCoreLibTest.ID, id));
    }
}
