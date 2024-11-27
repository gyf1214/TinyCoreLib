package org.shsts.tinycorelib.test.datagen;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import org.shsts.tinycorelib.datagen.api.context.IDataContext;
import org.shsts.tinycorelib.test.All;
import org.shsts.tinycorelib.test.TinyCoreLibTest;

import static org.shsts.tinycorelib.test.datagen.TinyDataGenTest.DATA_GEN;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class AllData {
    public static void init() {
        DATA_GEN
            .blockModel(AllData::testBlockModel)
            .item(All.TEST_ITEM)
            .model(ctx -> ctx.provider().withExistingParent(ctx.id(), "item/generated")
                .texture("layer0", mcLoc("item/arrow")))
            .tag(itemTag("test_item_tag"))
            .build()
            .tag(itemTag("test_item_tag"), itemTag("test_parent_item_tag"))
            .block(All.TEST_BLOCK1)
            .blockState(ctx -> {
                var prov = ctx.provider();
                var models = prov.models();
                prov.simpleBlock(ctx.object(), models.cubeAll(
                    ctx.id(), mcLoc("block/birch_planks")));
            }).tag(blockTag("test_block_tag"))
            .build()
            .block(All.TEST_BLOCK2)
            .blockState(ctx -> {
                var prov = ctx.provider();
                var models = prov.models();
                prov.horizontalBlock(ctx.object(),
                    models.getExistingFile(modLoc("block/test_block_model")));
            }).itemModel(ctx -> ctx.provider()
                .withExistingParent(ctx.id(), modLoc("block/test_block_model")))
            .build();
    }

    private static ResourceLocation mcLoc(String id) {
        return new ResourceLocation(id);
    }

    private static ResourceLocation modLoc(String id) {
        return new ResourceLocation(TinyCoreLibTest.ID, id);
    }

    private static void testBlockModel(IDataContext<BlockModelProvider> ctx) {
        ctx.provider().withExistingParent("test_block_model", mcLoc("block/block"))
            .element()
            .from(0, 0, 0).to(16, 16, 16)
            .allFaces((dir, face) -> {
                face.cullface(dir);
                if (dir == Direction.NORTH) {
                    face.texture("#front");
                } else {
                    face.texture("#other").tintindex(0);
                }
            }).end()
            .texture("front", mcLoc("block/glass"))
            .texture("other", mcLoc("block/oak_planks"))
            .texture("particle", "#other");
    }

    private static TagKey<Block> blockTag(String id) {
        return TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(TinyCoreLibTest.ID, id));
    }

    private static TagKey<Item> itemTag(String id) {
        return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(TinyCoreLibTest.ID, id));
    }
}
