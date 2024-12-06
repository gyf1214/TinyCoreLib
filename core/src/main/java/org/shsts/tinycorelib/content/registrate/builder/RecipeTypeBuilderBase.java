package org.shsts.tinycorelib.content.registrate.builder;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.IForgeRegistry;
import org.shsts.tinycorelib.api.core.Transformer;
import org.shsts.tinycorelib.api.recipe.IRecipe;
import org.shsts.tinycorelib.api.registrate.builder.IRecipeTypeBuilderBase;
import org.shsts.tinycorelib.api.registrate.entry.IRecipeType;
import org.shsts.tinycorelib.content.common.Builder;
import org.shsts.tinycorelib.content.recipe.SmartRecipeType;
import org.shsts.tinycorelib.content.registrate.Registrate;
import org.shsts.tinycorelib.content.registrate.entry.RecipeTypeEntry;
import org.shsts.tinycorelib.content.registrate.handler.RecipeTypeHandler;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class RecipeTypeBuilderBase<C, R extends IRecipe<C>, B, P,
    S extends IRecipeTypeBuilderBase<R, B, P, S>>
    extends Builder<RecipeType<?>, P, S>
    implements IRecipeTypeBuilderBase<R, B, P, S> {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final RecipeTypeHandler handler;
    private final ResourceLocation loc;
    private final IRecipeType.BuilderFactory<B> builderFactory;

    @Nullable
    private String prefix = null;
    @Nullable
    private Class<R> recipeClass = null;
    private Transformer<B> defaults = $ -> $;

    @Nullable
    protected RecipeTypeEntry<?, ?, B> entry;

    public RecipeTypeBuilderBase(Registrate registrate, P parent, String id,
        IRecipeType.BuilderFactory<B> builderFactory) {
        super(parent);
        this.handler = registrate.recipeTypeHandler;
        this.loc = new ResourceLocation(registrate.modid, id);
        this.builderFactory = builderFactory;
    }

    @Override
    public S recipeClass(Class<R> clazz) {
        recipeClass = clazz;
        return self();
    }

    @Override
    public S defaults(Transformer<B> trans) {
        defaults = defaults.chain(trans);
        return self();
    }

    @Override
    protected SmartRecipeType<C, R, B> createObject() {
        if (prefix == null) {
            prefix = id();
        }
        assert recipeClass != null;
        return new SmartRecipeType<>(builderFactory, prefix, defaults, recipeClass);
    }

    protected abstract RecipeSerializer<?> createSerializer();

    @Override
    @SuppressWarnings("unchecked")
    public SmartRecipeType<C, R, B> buildObject() {
        return (SmartRecipeType<C, R, B>) super.buildObject();
    }

    public void registerSerializer(IForgeRegistry<RecipeSerializer<?>> registry) {
        assert entry != null;
        LOGGER.trace("register object {} {}", registry.getRegistryName(), loc);
        var object = createSerializer();
        object.setRegistryName(loc);
        registry.register(object);
        entry.setSerializer(object);
    }

    @Override
    public ResourceLocation loc() {
        return loc;
    }

    public IRecipeType<B> register() {
        LOGGER.trace("create recipe type {} {}", getClass().getSimpleName(), loc);
        entry = handler.register(this);
        return entry;
    }
}
