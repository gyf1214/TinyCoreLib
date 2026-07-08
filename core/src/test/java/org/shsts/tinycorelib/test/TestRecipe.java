package org.shsts.tinycorelib.test;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;
import org.shsts.tinycorelib.api.recipe.IRecipe;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TestRecipe implements IRecipe<ITestCapability> {
    public static final MapCodec<TestRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Codec.INT.fieldOf("beginSeconds").forGetter(TestRecipe::beginSeconds),
            Codec.INT.fieldOf("endSeconds").forGetter(TestRecipe::endSeconds),
            ItemStack.CODEC.fieldOf("displayItem").forGetter(TestRecipe::displayItem)
        ).apply(instance, TestRecipe::new));

    private final int beginSeconds, endSeconds;
    public final ItemStack displayItem;

    public TestRecipe(int beginSeconds, int endSeconds, ItemStack displayItem) {
        assert beginSeconds >= 0 && endSeconds >= 0;
        assert endSeconds >= beginSeconds;
        this.beginSeconds = beginSeconds;
        this.endSeconds = endSeconds;
        this.displayItem = displayItem;
    }

    @Override
    public boolean matches(ITestCapability container) {
        var seconds = container.getSeconds();
        return seconds >= beginSeconds && seconds < endSeconds;
    }

    private int beginSeconds() {
        return beginSeconds;
    }

    private int endSeconds() {
        return endSeconds;
    }

    private ItemStack displayItem() {
        return displayItem;
    }
}
