package org.shsts.tinycorelib.api.recipe;

import com.google.gson.JsonObject;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.shsts.tinycorelib.api.registrate.entry.IRecipeType;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IRecipeSerializer<R extends IRecipe<?>, B> {
    R fromJson(IRecipeType<B> type, ResourceLocation loc, JsonObject jo, ICondition.IContext context);

    void toJson(JsonObject jo, R recipe);
}
