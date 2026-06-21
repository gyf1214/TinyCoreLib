package org.shsts.tinycorelib.content.registrate.builder;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.shsts.tinycorelib.api.recipe.IRecipe;
import org.shsts.tinycorelib.api.registrate.builder.IRecipeTypeBuilder;
import org.shsts.tinycorelib.api.registrate.entry.IRecipeType;
import org.shsts.tinycorelib.content.common.Builder;
import org.shsts.tinycorelib.content.recipe.SmartRecipeSerializer;
import org.shsts.tinycorelib.content.recipe.SmartRecipeType;
import org.shsts.tinycorelib.content.registrate.Registrate;
import org.shsts.tinycorelib.content.registrate.entry.RecipeTypeEntry;
import org.shsts.tinycorelib.content.registrate.handler.RecipeTypeHandler;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RecipeTypeBuilder<C, R extends IRecipe<C>, P>
    extends Builder<RecipeType<?>, P, IRecipeTypeBuilder<R, P>>
    implements IRecipeTypeBuilder<R, P> {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final RecipeTypeHandler handler;
    private final ResourceLocation loc;

    @Nullable
    private Class<R> recipeClass = null;
    @Nullable
    private MapCodec<R> serializer = null;
    @Nullable
    private RecipeTypeEntry<C, R> entry;

    public RecipeTypeBuilder(Registrate registrate, P parent, String id) {
        super(parent);
        this.handler = registrate.recipeTypeHandler;
        this.loc = ResourceLocation.fromNamespaceAndPath(registrate.modid, id);
        onBuild.add(this::register);
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

    private RecipeSerializer<?> createSerializer() {
        assert entry != null;
        assert serializer != null;
        return new SmartRecipeSerializer<>(entry, serializer);
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

    public void registerSerializer(RegisterEvent.RegisterHelper<RecipeSerializer<?>> helper) {
        assert entry != null;
        LOGGER.trace("register object {} {}", "recipe_serializer", loc);
        var object = createSerializer();
        helper.register(loc, object);
        entry.setSerializer(object);
    }

    @Override
    public ResourceLocation loc() {
        return loc;
    }

    @Override
    public IRecipeType<R> register() {
        LOGGER.trace("create recipe type {} {}", getClass().getSimpleName(), loc);
        entry = handler.register(this);
        return entry;
    }
}
