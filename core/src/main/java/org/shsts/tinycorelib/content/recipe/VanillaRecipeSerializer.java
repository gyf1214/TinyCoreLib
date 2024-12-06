package org.shsts.tinycorelib.content.recipe;

import com.google.gson.JsonObject;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.shsts.tinycorelib.api.recipe.IRecipe;
import org.shsts.tinycorelib.api.recipe.IVanillaRecipeSerializer;
import org.shsts.tinycorelib.api.registrate.entry.IRecipeType;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class VanillaRecipeSerializer<C, R extends IRecipe<C>>
    extends ForgeRegistryEntry<RecipeSerializer<?>>
    implements RecipeSerializer<SmartRecipe<C, R>> {
    private final IRecipeType<?> type;
    private final IVanillaRecipeSerializer<R> compose;

    public VanillaRecipeSerializer(IRecipeType<?> type, IVanillaRecipeSerializer<R> compose) {
        this.type = type;
        this.compose = compose;
    }

    @Override
    public SmartRecipe<C, R> fromJson(ResourceLocation loc, JsonObject jo,
        ICondition.IContext context) {
        var recipe = compose.fromJson(loc, jo, context);
        return new SmartRecipe<>(type.get(), this, loc, recipe);
    }

    @Override
    public SmartRecipe<C, R> fromJson(ResourceLocation loc, JsonObject jo) {
        return fromJson(loc, jo, ICondition.IContext.EMPTY);
    }

    @Override
    public SmartRecipe<C, R> fromNetwork(ResourceLocation loc, FriendlyByteBuf buf) {
        var recipe = compose.fromNetwork(loc, buf);
        return new SmartRecipe<>(type.get(), this, loc, recipe);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, SmartRecipe<C, R> recipe) {
        compose.toNetwork(buf, recipe.compose);
    }
}
