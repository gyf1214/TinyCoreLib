package org.shsts.tinycorelib.test;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.shsts.tinycorelib.api.recipe.IRecipe;

import static org.shsts.tinycorelib.test.All.ITEM_HANDLER_CAPABILITY;
import static org.shsts.tinycorelib.test.All.TEST_CAPABILITY;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TestCookingRecipe implements IRecipe<BlockEntity> {
    public static final MapCodec<TestCookingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            smeltingCodec().forGetter(TestCookingRecipe::smeltingRecipe),
            Codec.INT.fieldOf("beginSeconds").forGetter(TestCookingRecipe::beginSeconds)
        ).apply(instance, TestCookingRecipe::new));

    private final SmeltingRecipe smeltingRecipe;
    private final int beginSeconds;

    public TestCookingRecipe(SmeltingRecipe smeltingRecipe, int beginSeconds) {
        this.smeltingRecipe = smeltingRecipe;
        this.beginSeconds = beginSeconds;
    }

    @Override
    public boolean matches(BlockEntity container) {
        var itemHandler = (IItemHandlerModifiable) ITEM_HANDLER_CAPABILITY.get(container);
        var testCap = TEST_CAPABILITY.get(container);
        var world = container.getLevel();
        assert world != null;
        return smeltingRecipe.matches(new SingleRecipeInput(itemHandler.getStackInSlot(0)), world) &&
            testCap.getSeconds() >= beginSeconds;
    }

    @SuppressWarnings("DataFlowIssue")
    public ItemStack getResult() {
        return smeltingRecipe.assemble(new SingleRecipeInput(ItemStack.EMPTY), null);
    }

    private SmeltingRecipe smeltingRecipe() {
        return smeltingRecipe;
    }

    private int beginSeconds() {
        return beginSeconds;
    }

    @SuppressWarnings("unchecked")
    private static MapCodec<SmeltingRecipe> smeltingCodec() {
        return (MapCodec<SmeltingRecipe>) RecipeSerializer.SMELTING_RECIPE.codec();
    }
}
