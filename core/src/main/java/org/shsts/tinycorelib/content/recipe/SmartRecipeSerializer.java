package org.shsts.tinycorelib.content.recipe;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.shsts.tinycorelib.api.recipe.IRecipe;
import org.shsts.tinycorelib.api.recipe.IRecipeSerializer;
import org.shsts.tinycorelib.api.registrate.entry.IRecipeType;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SmartRecipeSerializer<C, R extends IRecipe<C>, B> extends ForgeRegistryEntry<RecipeSerializer<?>>
    implements RecipeSerializer<SmartRecipe<C, R>> {
    private static final Gson GSON = new Gson();

    private final IRecipeType<B> type;
    public final IRecipeSerializer<R, B> compose;

    public SmartRecipeSerializer(IRecipeType<B> type, IRecipeSerializer<R, B> compose) {
        this.type = type;
        this.compose = compose;
    }

    @Override
    public SmartRecipe<C, R> fromJson(ResourceLocation loc, JsonObject jo,
        ICondition.IContext context) {
        return new SmartRecipe<>(type.get(), this, loc,
            compose.fromJson(type, loc, jo, context));
    }

    @Override
    public SmartRecipe<C, R> fromJson(ResourceLocation loc, JsonObject jo) {
        return fromJson(loc, jo, ICondition.IContext.EMPTY);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, SmartRecipe<C, R> recipe) {
        var jo = new JsonObject();
        compose.toJson(jo, recipe.compose);
        var str = GSON.toJson(jo);
        buf.writeUtf(str);
    }

    @Override
    public SmartRecipe<C, R> fromNetwork(ResourceLocation loc, FriendlyByteBuf buf) {
        var jo = GSON.fromJson(buf.readUtf(), JsonObject.class);
        return fromJson(loc, jo);
    }
}
