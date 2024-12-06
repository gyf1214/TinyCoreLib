package org.shsts.tinycorelib.test;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import org.shsts.tinycorelib.api.core.IBuilder;
import org.shsts.tinycorelib.api.recipe.IRecipe;
import org.shsts.tinycorelib.api.recipe.IRecipeBuilderBase;
import org.shsts.tinycorelib.api.registrate.entry.IRecipeType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class RecipeBuilderBase<R extends IRecipe<?>, U, S extends RecipeBuilderBase<R, U, S>>
    implements IRecipeBuilderBase<R>, IBuilder<U, IRecipeType<S>, S> {
    private final List<Consumer<U>> onCreate = new ArrayList<>();
    private final List<Runnable> onBuild = new ArrayList<>();
    protected final IRecipeType<S> parent;
    protected final ResourceLocation loc;

    protected RecipeBuilderBase(IRecipeType<S> parent, ResourceLocation loc) {
        this.parent = parent;
        this.loc = loc;
    }

    protected abstract U createObject();

    @Override
    public U buildObject() {
        var obj = createObject();
        for (var cb : onCreate) {
            cb.accept(obj);
        }
        return obj;
    }

    @Override
    public IRecipeType<S> build() {
        for (var cb : onBuild) {
            cb.run();
        }
        return parent;
    }

    @Override
    public S onCreateObject(Consumer<U> cons) {
        onCreate.add(cons);
        return self();
    }

    @Override
    public S onBuild(Runnable cb) {
        onBuild.add(cb);
        return self();
    }
}
