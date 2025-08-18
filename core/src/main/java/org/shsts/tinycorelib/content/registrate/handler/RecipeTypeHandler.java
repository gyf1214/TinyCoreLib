package org.shsts.tinycorelib.content.registrate.handler;

import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.shsts.tinycorelib.api.recipe.IRecipe;
import org.shsts.tinycorelib.api.recipe.IRecipeBuilderBase;
import org.shsts.tinycorelib.api.registrate.entry.IRecipeType;
import org.shsts.tinycorelib.content.recipe.SmartRecipeType;
import org.shsts.tinycorelib.content.registrate.Registrate;
import org.shsts.tinycorelib.content.registrate.builder.RecipeTypeBuilderBase;
import org.shsts.tinycorelib.content.registrate.entry.RecipeTypeEntry;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RecipeTypeHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final String modid;
    private final List<RecipeTypeBuilderBase<?, ?, ?, ?, ?>> builders = new ArrayList<>();
    private final DeferredRegister<RecipeType<?>> recipeTypeRegister;

    public RecipeTypeHandler(Registrate registrate) {
        this.modid = registrate.modid;
        this.recipeTypeRegister = DeferredRegister.create(
            Registry.RECIPE_TYPE_REGISTRY, registrate.modid);
    }

    @SuppressWarnings("unchecked")
    public <C, R extends IRecipe<C>,
        B extends IRecipeBuilderBase<R>> IRecipeType<B> getRecipeType(ResourceLocation loc) {
        Supplier<SmartRecipeType<C, R, B>> supplier = () -> {
            var type = RegistryObject.create(loc, Registry.RECIPE_TYPE_REGISTRY, modid).get();
            return (SmartRecipeType<C, R, B>) type;
        };
        return new RecipeTypeEntry<>(loc, supplier);
    }

    public <C, R extends IRecipe<C>,
        B extends IRecipeBuilderBase<R>> IRecipeType<B> getRecipeType(String id) {
        return getRecipeType(new ResourceLocation(modid, id));
    }

    public <C, R extends IRecipe<C>,
        B extends IRecipeBuilderBase<R>> RecipeTypeEntry<?, ?, B> register(
        RecipeTypeBuilderBase<C, R, B, ?, ?> builder) {
        builders.add(builder);
        var recipeType = recipeTypeRegister.register(builder.id(), builder::buildObject);
        return new RecipeTypeEntry<>(builder.loc(), recipeType);
    }

    public void onRegisterSerializer(RegistryEvent.Register<RecipeSerializer<?>> event) {
        if (builders.isEmpty()) {
            return;
        }
        var registry = event.getRegistry();
        LOGGER.info("Mod {} registry {} register {} objects", modid,
            registry.getRegistryName(), builders.size());
        for (var builder : builders) {
            builder.registerSerializer(registry);
        }
        builders.clear();
    }

    public void addListeners(IEventBus modEventBus) {
        modEventBus.addGenericListener(RecipeSerializer.class, this::onRegisterSerializer);
        recipeTypeRegister.register(modEventBus);
    }
}
