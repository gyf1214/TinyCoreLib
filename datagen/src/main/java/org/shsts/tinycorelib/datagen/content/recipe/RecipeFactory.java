package org.shsts.tinycorelib.datagen.content.recipe;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import org.shsts.tinycorelib.api.core.IBuilder;
import org.shsts.tinycorelib.api.recipe.IRecipe;
import org.shsts.tinycorelib.api.registrate.entry.IRecipeType;
import org.shsts.tinycorelib.content.recipe.SmartRecipe;
import org.shsts.tinycorelib.datagen.api.recipe.IRecipeFactory;
import org.shsts.tinycorelib.datagen.content.DataGen;

import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RecipeFactory<R extends IRecipe<?>, B extends IBuilder<R, IRecipeFactory<R, B>, B>>
    implements IRecipeFactory<R, B> {
    private final DataGen dataGen;
    private final IRecipeType<R> type;
    private final Function<IRecipeFactory<R, B>, B> factory;

    public RecipeFactory(DataGen dataGen, IRecipeType<R> type,
        Function<IRecipeFactory<R, B>, B> factory) {
        this.dataGen = dataGen;
        this.type = type;
        this.factory = factory;
    }

    @Override
    public B recipe(String id) {
        return recipe(ResourceLocation.fromNamespaceAndPath(dataGen.modid, id));
    }

    @Override
    public B recipe(ResourceLocation loc) {
        var builder = factory.apply(this);
        builder.onCreateObject(recipe -> dataGen.recipeHandler.registerRecipe(output ->
            output.accept(loc, createRecipe(recipe), null)));
        builder.onBuild(builder::buildObject);
        return builder;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Recipe<?> createRecipe(R recipe) {
        return new SmartRecipe(type, recipe);
    }
}
