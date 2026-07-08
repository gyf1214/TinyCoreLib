package org.shsts.tinycorelib.datagen.content.handler;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.shsts.tinycorelib.datagen.content.DataGen;

import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RecipeHandler extends DataHandler<RecipeHandler.Provider> {
    public RecipeHandler(DataGen dataGen) {
        super(dataGen);
    }

    public class Provider extends RecipeProvider {
        private RecipeOutput recipeOutput;

        public Provider(GatherDataEvent event) {
            super(event.getGenerator().getPackOutput(), event.getLookupProvider());
        }

        @Override
        protected void buildRecipes(RecipeOutput output, HolderLookup.Provider holderLookup) {
            recipeOutput = output;
            register(this);
        }
    }

    @Override
    public RecipeHandler.Provider createProvider(GatherDataEvent event) {
        return new Provider(event);
    }

    public void registerRecipe(Consumer<RecipeOutput> recipe) {
        addCallback(prov -> recipe.accept(prov.recipeOutput));
    }
}
