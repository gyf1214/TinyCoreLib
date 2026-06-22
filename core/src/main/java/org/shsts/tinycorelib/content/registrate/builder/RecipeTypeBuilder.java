package org.shsts.tinycorelib.content.registrate.builder;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.shsts.tinycorelib.api.recipe.IRecipe;
import org.shsts.tinycorelib.api.registrate.builder.IRecipeTypeBuilder;
import org.shsts.tinycorelib.api.registrate.entry.IRecipeType;
import org.shsts.tinycorelib.content.recipe.SmartRecipeSerializer;
import org.shsts.tinycorelib.content.recipe.SmartRecipeType;
import org.shsts.tinycorelib.content.registrate.Registrate;
import org.shsts.tinycorelib.content.registrate.entry.RecipeTypeEntry;
import org.shsts.tinycorelib.content.registrate.handler.RecipeTypeHandler;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RecipeTypeBuilder<C, R extends IRecipe<C>, P>
    extends EntryBuilder<RecipeType<?>, RecipeType<?>, P, IRecipeTypeBuilder<R, P>>
    implements IRecipeTypeBuilder<R, P> {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Nullable
    private Class<R> recipeClass = null;
    @Nullable
    private MapCodec<R> serializer = null;

    public RecipeTypeBuilder(Registrate registrate, P parent, String id) {
        super(registrate, registrate.recipeTypeHandler, parent, id);
    }

    @Override
    public IRecipeTypeBuilder<R, P> recipeClass(Class<R> clazz) {
        recipeClass = clazz;
        return self();
    }

    @Override
    public IRecipeTypeBuilder<R, P> serializer(MapCodec<R> value) {
        serializer = value;
        return self();
    }

    @SuppressWarnings({"unchecked", "RedundantSuppression"})
    private RecipeSerializer<?> createSerializer() {
        assert entry != null;
        assert serializer != null;
        var entry1 = (RecipeTypeEntry<C, R>) entry;
        return new SmartRecipeSerializer<>(entry1, serializer);
    }

    @Override
    protected SmartRecipeType<C, R> createObject() {
        assert recipeClass != null;
        return new SmartRecipeType<>(loc, recipeClass);
    }

    @Override
    @SuppressWarnings("unchecked")
    public SmartRecipeType<C, R> buildObject() {
        return (SmartRecipeType<C, R>) super.buildObject();
    }

    @SuppressWarnings({"unchecked", "RedundantSuppression"})
    public void registerSerializer(Registry<RecipeSerializer<?>> registry) {
        assert entry != null;
        LOGGER.trace("register object {} {}", "recipe_serializer", loc);
        var object = createSerializer();
        Registry.register(registry, loc, object);
        var entry1 = (RecipeTypeEntry<C, R>) entry;
        entry1.setSerializer(object);
    }

    @Override
    protected RecipeTypeEntry<C, R> createEntry() {
        return ((RecipeTypeHandler) handler).register(this);
    }

    @Override
    @SuppressWarnings({"unchecked", "RedundantSuppression"})
    public IRecipeType<R> register() {
        return (IRecipeType<R>) super.register();
    }
}
