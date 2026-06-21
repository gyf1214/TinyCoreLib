package org.shsts.tinycorelib.content.registrate.handler;

import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;
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

    public RecipeTypeHandler(Registrate registrate) {
        this.modid = registrate.modid;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public IRecipeType<?> getRecipeType(ResourceLocation loc) {
        Supplier<SmartRecipeType> supplier = () -> {
            var type = BuiltInRegistries.RECIPE_TYPE.get(loc);
            if (type == null) {
                throw new IllegalStateException("Missing recipe type " + loc);
            }
            return (SmartRecipeType<?, ?, ?>) type;
        };
        return new RecipeTypeEntry(loc, supplier);
    }

    public IRecipeType<?> getRecipeType(String id) {
        return getRecipeType(ResourceLocation.fromNamespaceAndPath(modid, id));
    }

    public <C, R extends IRecipe<C>,
        B extends IRecipeBuilderBase<R>> RecipeTypeEntry<?, ?, B> register(
        RecipeTypeBuilderBase<C, R, B, ?, ?> builder) {
        builders.add(builder);
        return new RecipeTypeEntry<>(builder.loc(), () -> {
            var type = BuiltInRegistries.RECIPE_TYPE.get(builder.loc());
            if (type == null) {
                throw new IllegalStateException("Missing recipe type " + builder.loc());
            }
            return (SmartRecipeType<C, R, B>) type;
        });
    }

    public void onRegisterEvent(RegisterEvent event) {
        if (builders.isEmpty()) {
            return;
        }
        event.register(Registries.RECIPE_TYPE, helper -> {
            LOGGER.info("Mod {} registry {} register {} objects", modid,
                Registries.RECIPE_TYPE.location(), builders.size());
            for (var builder : builders) {
                helper.register(builder.loc(), builder.buildObject());
            }
        });
        event.register(Registries.RECIPE_SERIALIZER, helper -> {
            LOGGER.info("Mod {} registry {} register {} objects", modid,
                Registries.RECIPE_SERIALIZER.location(), builders.size());
            for (var builder : builders) {
                builder.registerSerializer(helper);
            }
            builders.clear();
        });
    }

    public void addListeners(IEventBus modEventBus) {
        modEventBus.addListener(this::onRegisterEvent);
    }
}
