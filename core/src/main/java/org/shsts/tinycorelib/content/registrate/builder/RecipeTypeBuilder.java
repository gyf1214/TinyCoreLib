package org.shsts.tinycorelib.content.registrate.builder;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.shsts.tinycorelib.api.recipe.IRecipe;
import org.shsts.tinycorelib.api.recipe.IRecipeBuilder;
import org.shsts.tinycorelib.api.recipe.IRecipeSerializer;
import org.shsts.tinycorelib.api.registrate.builder.IRecipeTypeBuilder;
import org.shsts.tinycorelib.api.registrate.entry.IRecipeType;
import org.shsts.tinycorelib.content.recipe.SmartRecipeSerializer;
import org.shsts.tinycorelib.content.registrate.Registrate;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RecipeTypeBuilder<C, R extends IRecipe<C>, B extends IRecipeBuilder<R, B>, P>
    extends RecipeTypeBuilderBase<C, R, B, P, IRecipeTypeBuilder<R, B, P>>
    implements IRecipeTypeBuilder<R, B, P> {
    @Nullable
    private IRecipeSerializer<R, B> serializer = null;

    public RecipeTypeBuilder(Registrate registrate, P parent, String id,
        IRecipeType.BuilderFactory<B> builderFactory) {
        super(registrate, parent, id, builderFactory);
    }

    @Override
    public IRecipeTypeBuilder<R, B, P> serializer(IRecipeSerializer<R, B> serializer) {
        this.serializer = serializer;
        return self();
    }

    @Override
    protected RecipeSerializer<?> createSerializer() {
        assert entry != null;
        assert serializer != null;
        return new SmartRecipeSerializer<>(entry, serializer);
    }
}
