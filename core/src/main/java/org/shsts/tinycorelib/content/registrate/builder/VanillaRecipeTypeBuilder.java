package org.shsts.tinycorelib.content.registrate.builder;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.shsts.tinycorelib.api.recipe.IRecipe;
import org.shsts.tinycorelib.api.recipe.IVanillaRecipeBuilder;
import org.shsts.tinycorelib.api.recipe.IVanillaRecipeSerializer;
import org.shsts.tinycorelib.api.registrate.builder.IVanillaRecipeTypeBuilder;
import org.shsts.tinycorelib.api.registrate.entry.IRecipeType;
import org.shsts.tinycorelib.content.recipe.VanillaRecipeSerializer;
import org.shsts.tinycorelib.content.registrate.Registrate;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class VanillaRecipeTypeBuilder<C, R extends IRecipe<C>, B extends IVanillaRecipeBuilder<R, B>, P>
    extends RecipeTypeBuilderBase<C, R, B, P, IVanillaRecipeTypeBuilder<R, B, P>>
    implements IVanillaRecipeTypeBuilder<R, B, P> {

    @Nullable
    private IVanillaRecipeSerializer<R> serializer = null;

    public VanillaRecipeTypeBuilder(Registrate registrate, P parent, String id,
        IRecipeType.BuilderFactory<B> builderFactory) {
        super(registrate, parent, id, builderFactory);
    }

    @Override
    public IVanillaRecipeTypeBuilder<R, B, P> serializer(IVanillaRecipeSerializer<R> value) {
        serializer = value;
        return self();
    }

    @Override
    protected RecipeSerializer<?> createSerializer() {
        assert entry != null;
        assert serializer != null;
        return new VanillaRecipeSerializer<>(entry, serializer);
    }
}
