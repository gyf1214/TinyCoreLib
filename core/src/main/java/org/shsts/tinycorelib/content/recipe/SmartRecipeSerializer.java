package org.shsts.tinycorelib.content.recipe;

import com.mojang.serialization.MapCodec;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.shsts.tinycorelib.api.recipe.IRecipe;
import org.shsts.tinycorelib.api.registrate.entry.IRecipeType;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SmartRecipeSerializer<C, R extends IRecipe<C>>
    implements RecipeSerializer<SmartRecipe<C, R>> {
    private final MapCodec<SmartRecipe<C, R>> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, SmartRecipe<C, R>> streamCodec;

    public SmartRecipeSerializer(IRecipeType<R> type, MapCodec<R> compose) {
        this.codec = compose.xmap(recipe -> new SmartRecipe<>(type, recipe), recipe -> recipe.compose);
        this.streamCodec = ByteBufCodecs.fromCodecWithRegistries(codec.codec());
    }

    @Override
    public MapCodec<SmartRecipe<C, R>> codec() {
        return codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, SmartRecipe<C, R>> streamCodec() {
        return streamCodec;
    }
}
