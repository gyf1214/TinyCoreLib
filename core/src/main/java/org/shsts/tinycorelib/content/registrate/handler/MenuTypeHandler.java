package org.shsts.tinycorelib.content.registrate.handler;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import org.shsts.tinycorelib.content.registrate.Registrate;
import org.shsts.tinycorelib.content.registrate.builder.MenuBuilder;
import org.shsts.tinycorelib.content.registrate.entry.MenuTypeEntry;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MenuTypeHandler extends EntryHandler<MenuType<?>> {
    public MenuTypeHandler(Registrate registrate) {
        super(registrate, Registries.MENU, BuiltInRegistries.MENU);
    }

    public MenuTypeEntry getTypeEntry(ResourceLocation loc) {
        return new MenuTypeEntry(loc, () -> getEntry(loc).get());
    }

    public MenuTypeEntry getTypeEntry(String id) {
        return getTypeEntry(ResourceLocation.fromNamespaceAndPath(modid, id));
    }

    public MenuTypeEntry registerType(MenuBuilder<?, ?> builder) {
        builders.add(builder);
        return new MenuTypeEntry(builder.loc());
    }
}
