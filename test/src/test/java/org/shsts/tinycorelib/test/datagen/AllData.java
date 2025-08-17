package org.shsts.tinycorelib.test.datagen;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import org.shsts.tinycorelib.api.meta.IMetaExecutor;
import org.shsts.tinycorelib.datagen.api.IDataHandler;
import org.shsts.tinycorelib.datagen.api.context.IDataContext;
import org.shsts.tinycorelib.test.All;
import org.shsts.tinycorelib.test.TinyCoreLibTest;

import java.util.List;

import static org.shsts.tinycorelib.test.All.TEST_BLOCK2;
import static org.shsts.tinycorelib.test.All.TEST_BLOCK3;
import static org.shsts.tinycorelib.test.All.TEST_RECIPE;
import static org.shsts.tinycorelib.test.All.TEST_VANILLA_RECIPE;
import static org.shsts.tinycorelib.test.datagen.TinyDataGenTest.CORE;
import static org.shsts.tinycorelib.test.datagen.TinyDataGenTest.DATA_GEN;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class AllData {
    private static final TagKey<Item> TEST_ITEM_TAG = itemTag("test_item_tag");
    private static final TagKey<Item> TEST_ITEM_TAG2 = itemTag("test_item_tag2");
    private static final TagKey<Item> TEST_PARENT_TAG = itemTag("test_parent_item_tag");

    public static final IMetaExecutor TEST_META_DATAGEN;
    public static IDataHandler<TestResourceProvider> TEST_RESOURCES;
    public static ResourceLocation TEST_RESOURCE1;
    public static ResourceLocation TEST_RESOURCE2;

    static {
        TEST_META_DATAGEN = CORE.registerMeta("test", new TestMetaDataGen());
    }

    public static void init() {}

    public static void generate() {
        TEST_META_DATAGEN.execute();

        DATA_GEN
            .blockModel(AllData::testBlockModel)
            .item(All.TEST_ITEM)
            .model(ctx -> ctx.provider().withExistingParent(ctx.id(), "item/generated")
                .texture("layer0", mcLoc("item/arrow")))
            .tag(TEST_ITEM_TAG)
            .build()
            .tag(TEST_ITEM_TAG, TEST_PARENT_TAG)
            .tag(() -> Items.GLASS, TEST_ITEM_TAG)
            .tag(() -> Items.SAND, List.of(TEST_ITEM_TAG, TEST_ITEM_TAG2))
            .block(All.TEST_BLOCK1)
            .blockState(ctx -> {
                var prov = ctx.provider();
                var models = prov.models();
                prov.simpleBlock(ctx.object(), models.cubeAll(
                    ctx.id(), mcLoc("block/birch_planks")));
            }).tag(blockTag("test_block_tag"))
            .drop(() -> Items.BIRCH_PLANKS)
            .build()
            .block(All.TEST_BLOCK2)
            .blockState(ctx -> {
                var prov = ctx.provider();
                var models = prov.models();
                prov.horizontalBlock(ctx.object(),
                    models.getExistingFile(modLoc("block/test_block_model")));
            }).itemModel(ctx -> ctx.provider()
                .withExistingParent(ctx.id(), modLoc("block/test_block_model")))
            .tag(List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL))
            .build()
            .block(TEST_BLOCK3)
            .blockState(ctx -> ctx.provider().simpleBlock(ctx.object(),
                ctx.provider().models().cubeAll(ctx.id(), mcLoc("block/nether_bricks"))))
            .build();

        TEST_RECIPE.recipe(DATA_GEN, "test_recipe1")
            .range(0, 10)
            .displayItem(Items.GLASS)
            .build();

        TEST_VANILLA_RECIPE.recipe(DATA_GEN, "test_vanilla1")
            .ingredient(TEST_ITEM_TAG)
            .result(TEST_BLOCK2)
            .cookingTime(100)
            .beginSeconds(0)
            .build()
            .recipe(DATA_GEN, "test_vanilla2")
            .ingredient(() -> Blocks.STONE)
            .result(TEST_BLOCK3)
            .cookingTime(100)
            .beginSeconds(10)
            .build();

        DATA_GEN.vanillaRecipe(() -> ShapedRecipeBuilder.shaped(TEST_BLOCK3.get())
            .pattern("###").pattern("#X#")
            .define('#', Items.BIRCH_PLANKS)
            .define('X', TEST_PARENT_TAG)
            .unlockedBy("has_test", has(TEST_PARENT_TAG)));

        DATA_GEN.nullRecipe(Items.OAK_PLANKS);

        DATA_GEN.replaceVanillaRecipe(() -> ShapelessRecipeBuilder.shapeless(Items.BIRCH_PLANKS, 6)
            .requires(Items.BIRCH_LOG)
            .unlockedBy("has_birch", has(Items.BIRCH_LOG)));

        TEST_RESOURCES = DATA_GEN.createHandler(TestResourceProvider::new);

        TEST_RESOURCE1 = TEST_RESOURCES.builder("test1", TestResourceBuilder::factory)
            .name("foo")
            .register();

        TEST_RESOURCE2 = TEST_RESOURCES.builder("test2", TestResourceBuilder::factory)
            .name("bar")
            .reference(TEST_RESOURCE1)
            .register();

        DATA_GEN.addProvider(TestLanguageProvider::new);
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

    private static InventoryChangeTrigger.TriggerInstance inventoryTrigger(ItemPredicate... predicates) {
        return new InventoryChangeTrigger.TriggerInstance(EntityPredicate.Composite.ANY,
            MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, predicates);
    }

    public static InventoryChangeTrigger.TriggerInstance has(TagKey<Item> tag) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(tag).build());
    }

    public static InventoryChangeTrigger.TriggerInstance has(Item item) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(item).build());
    }
}
