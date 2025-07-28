package org.shsts.tinycorelib.api.registrate.builder;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.shsts.tinycorelib.api.core.DistLazy;
import org.shsts.tinycorelib.api.gui.MenuBase;
import org.shsts.tinycorelib.api.gui.client.IMenuScreenFactory;
import org.shsts.tinycorelib.api.gui.client.MenuScreenBase;
import org.shsts.tinycorelib.api.network.IChannel;
import org.shsts.tinycorelib.api.registrate.entry.IMenuType;

import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IMenuBuilder<M extends MenuBase, P>
    extends IEntryBuilder<MenuType<?>, MenuType<?>, P, IMenuBuilder<M, P>> {
    IMenuBuilder<M, P> channel(IChannel value);

    IMenuBuilder<M, P> title(Function<BlockEntity, Component> value);

    IMenuBuilder<M, P> title(String key);

    IMenuBuilder<M, P> screen(DistLazy<IMenuScreenFactory<M, ? extends MenuScreenBase<M>>> factory);

    @Override
    IMenuType register();
}
