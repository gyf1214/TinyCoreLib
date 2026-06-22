package org.shsts.tinycorelib.content.registrate.handler;

import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.shsts.tinycorelib.api.recipe.IRecipe;
import org.shsts.tinycorelib.api.registrate.entry.IRecipeType;
import org.shsts.tinycorelib.content.recipe.SmartRecipeType;
import org.shsts.tinycorelib.content.registrate.Registrate;
import org.shsts.tinycorelib.content.registrate.builder.RecipeTypeBuilder;
import org.shsts.tinycorelib.content.registrate.entry.RecipeTypeEntry;
import org.slf4j.Logger;

import java.util.NoSuchElementException;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RecipeTypeHandler extends EntryHandler<RecipeType<?>> {
    private static final Logger LOGGER = LogUtils.getLogger();

    private boolean typeRegistered = false;
    private boolean serializerRegistered = false;

    public RecipeTypeHandler(Registrate registrate) {
        super(registrate, Registries.RECIPE_TYPE, BuiltInRegistries.RECIPE_TYPE);
    }

    public IRecipeType<?> getRecipeType(ResourceLocation loc) {
        return new RecipeTypeEntry<>(loc, () -> {
            var type = BuiltInRegistries.RECIPE_TYPE.get(loc);
            if (type == null) {
                throw new NoSuchElementException();
            }
            return (SmartRecipeType<?, ?>) type;
        });
    }

    public IRecipeType<?> getRecipeType(String id) {
        return getRecipeType(ResourceLocation.fromNamespaceAndPath(modid, id));
    }

    public <C, R extends IRecipe<C>> RecipeTypeEntry<C, R> register(
        RecipeTypeBuilder<C, R, ?> builder) {
        builders.add(builder);
        return new RecipeTypeEntry<>(builder.loc());
    }

    public void onRegisterEvent(RegisterEvent event) {
        if (builders.isEmpty()) {
            return;
        }

        var registry = event.getRegistry(registryKey);
        if (!typeRegistered && registry != null) {
            registerBuilders(registry);
            typeRegistered = true;
        }

        var serializerRegistry = event.getRegistry(Registries.RECIPE_SERIALIZER);
        if (!serializerRegistered && serializerRegistry != null) {
            LOGGER.info("Mod {} registry {} register {} objects", modid,
                Registries.RECIPE_SERIALIZER.location(), builders.size());
            for (var builder : builders) {
                ((RecipeTypeBuilder<?, ?, ?>) builder).registerSerializer(serializerRegistry);
            }
            serializerRegistered = true;
        }

        if (typeRegistered && serializerRegistered) {
            builders.clear();
        }
    }
}
