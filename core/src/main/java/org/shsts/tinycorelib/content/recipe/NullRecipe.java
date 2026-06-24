package org.shsts.tinycorelib.content.recipe;

import com.mojang.serialization.MapCodec;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public enum NullRecipe implements CraftingRecipe {
    INSTANCE;

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    public static class Serializer implements RecipeSerializer<NullRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        private static final MapCodec<NullRecipe> Codec = MapCodec.unit(NullRecipe.INSTANCE);
        private static final StreamCodec<RegistryFriendlyByteBuf, NullRecipe> StreamCodec =
            net.minecraft.network.codec.StreamCodec.unit(NullRecipe.INSTANCE);

        @Override
        public MapCodec<NullRecipe> codec() {
            return Codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, NullRecipe> streamCodec() {
            return StreamCodec;
        }
    }
}
