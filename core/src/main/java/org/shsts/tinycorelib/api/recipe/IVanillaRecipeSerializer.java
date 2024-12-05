package org.shsts.tinycorelib.api.recipe;

import com.google.gson.JsonObject;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IVanillaRecipeSerializer<R extends IRecipe<?>> {
    R fromJson(ResourceLocation loc, JsonObject jo, ICondition.IContext context);

    R fromNetwork(ResourceLocation loc, FriendlyByteBuf buf);

    void toNetwork(FriendlyByteBuf buf, R recipe);
}
