package org.shsts.tinycorelib.content.registrate.builder;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.shsts.tinycorelib.api.core.DistLazy;
import org.shsts.tinycorelib.api.gui.IMenu;
import org.shsts.tinycorelib.api.gui.IMenuPlugin;
import org.shsts.tinycorelib.api.gui.client.IMenuScreenFactory;
import org.shsts.tinycorelib.api.gui.client.MenuScreenBase;
import org.shsts.tinycorelib.api.network.IChannel;
import org.shsts.tinycorelib.api.registrate.builder.IMenuBuilder;
import org.shsts.tinycorelib.api.registrate.entry.IMenuType;
import org.shsts.tinycorelib.content.gui.SmartMenuType;
import org.shsts.tinycorelib.content.network.Channel;
import org.shsts.tinycorelib.content.registrate.Registrate;
import org.shsts.tinycorelib.content.registrate.entry.MenuTypeEntry;
import org.shsts.tinycorelib.content.registrate.handler.MenuTypeHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MenuBuilder<P> extends EntryBuilder<MenuType<?>, MenuType<?>, P, IMenuBuilder<P>>
    implements IMenuBuilder<P> {
    @Nullable
    private Channel channel = null;
    private Function<BlockEntity, Component> title = $ -> TextComponent.EMPTY;
    @Nullable
    private DistLazy<IMenuScreenFactory<?>> screenFactory = null;
    private final List<Function<IMenu, IMenuPlugin<?>>> plugins = new ArrayList<>();

    public MenuBuilder(Registrate registrate, P parent, String id) {
        super(registrate, registrate.menuTypeHandler, parent, id);
    }

    @Override
    public IMenuBuilder<P> channel(IChannel value) {
        channel = (Channel) value;
        return self();
    }

    @Override
    public IMenuBuilder<P> title(Function<BlockEntity, Component> value) {
        title = value;
        return self();
    }

    @Override
    public IMenuBuilder<P> title(String key) {
        var text = new TranslatableComponent(key);
        title = $ -> text;
        return self();
    }

    @Override
    public IMenuBuilder<P> screen(DistLazy<IMenuScreenFactory<?>> value) {
        screenFactory = value;
        return self();
    }

    @Override
    public IMenuBuilder<P> plugin(Function<IMenu, IMenuPlugin<?>> factory) {
        plugins.add(factory);
        return self();
    }

    @Override
    public IMenuBuilder<P> dummyPlugin(Consumer<IMenu> cons) {
        return plugin(menu -> {
            cons.accept(menu);
            return IMenuPlugin.EMPTY;
        });
    }

    @Override
    protected MenuTypeEntry createEntry() {
        return ((MenuTypeHandler) handler).registerType(this);
    }

    @OnlyIn(Dist.CLIENT)
    private static <S extends MenuScreenBase> void applyMenuScreen(IMenuPlugin<S> plugin,
        MenuScreenBase screen) {
        var clazz = plugin.menuScreenClass();
        if (clazz != null && clazz.isInstance(screen)) {
            plugin.applyMenuScreen(clazz.cast(screen));
        }
    }

    @Override
    protected SmartMenuType createObject() {
        assert screenFactory != null;
        var type = new SmartMenuType(channel, title, new ArrayList<>(plugins));
        screenFactory.runOnDist(Dist.CLIENT, () -> factory ->
            registrate.menuScreenHandler.setMenuScreen(type, (menu, inventory, title1) -> {
                var screen = factory.create(menu, inventory, title1);
                for (var plugin : menu.getPlugins()) {
                    applyMenuScreen(plugin, screen);
                }
                return screen;
            }));
        return type;
    }

    @Override
    public IMenuType register() {
        return (IMenuType) super.register();
    }
}
