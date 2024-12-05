package org.shsts.tinycorelib.content.registrate.handler;

import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import org.shsts.tinycorelib.content.registrate.Registrate;
import org.shsts.tinycorelib.content.registrate.builder.RecipeTypeBuilderBase;
import org.shsts.tinycorelib.content.registrate.entry.RecipeTypeEntry;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

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

    public <B> RecipeTypeEntry<?, ?, B> register(RecipeTypeBuilderBase<?, ?, B, ?, ?> builder) {
        builders.add(builder);
        return new RecipeTypeEntry<>(builder.loc(), recipeTypeRegister.register(
            builder.id(), builder::buildObject));
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
