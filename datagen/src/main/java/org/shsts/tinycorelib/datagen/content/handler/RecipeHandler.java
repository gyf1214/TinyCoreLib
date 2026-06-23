package org.shsts.tinycorelib.datagen.content.handler;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.shsts.tinycorelib.datagen.content.DataGen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RecipeHandler extends DataHandler<RecipeProvider> {
    private final List<Consumer<RecipeOutput>> callbacks = new ArrayList<>();

    public RecipeHandler(DataGen dataGen) {
        super(dataGen);
    }

    private class Provider extends RecipeProvider {
        public Provider(GatherDataEvent event) {
            super(event.getGenerator().getPackOutput(), event.getLookupProvider());
        }

        @Override
        protected void buildRecipes(RecipeOutput output, HolderLookup.Provider holderLookup) {
            RecipeHandler.this.register(output);
        }
    }

    @Override
    public RecipeProvider createProvider(GatherDataEvent event) {
        return new Provider(event);
    }

    public void registerRecipe(Consumer<RecipeOutput> recipe) {
        callbacks.add(recipe);
    }

    private void register(RecipeOutput output) {
        for (var callback : callbacks) {
            callback.accept(output);
        }
        callbacks.clear();
    }
}
