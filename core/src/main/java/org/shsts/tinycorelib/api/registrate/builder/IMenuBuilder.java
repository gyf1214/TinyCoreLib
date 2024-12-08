package org.shsts.tinycorelib.api.registrate.builder;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.shsts.tinycorelib.api.core.DistLazy;
import org.shsts.tinycorelib.api.gui.IMenu;
import org.shsts.tinycorelib.api.gui.IMenuPlugin;
import org.shsts.tinycorelib.api.gui.client.IMenuScreenFactory;
import org.shsts.tinycorelib.api.network.IChannel;
import org.shsts.tinycorelib.api.registrate.entry.IMenuType;

import java.util.function.Consumer;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IMenuBuilder<P> extends IEntryBuilder<MenuType<?>, MenuType<?>, P, IMenuBuilder<P>> {
    IMenuBuilder<P> channel(IChannel value);

    IMenuBuilder<P> title(Function<BlockEntity, Component> value);

    IMenuBuilder<P> title(String key);

    IMenuBuilder<P> screen(DistLazy<IMenuScreenFactory<?>> value);

    IMenuBuilder<P> plugin(Function<IMenu, IMenuPlugin<?>> factory);

    IMenuBuilder<P> dummyPlugin(Consumer<IMenu> cons);

    @Override
    IMenuType register();
}
