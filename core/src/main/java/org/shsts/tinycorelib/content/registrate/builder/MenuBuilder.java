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
import org.shsts.tinycorelib.api.core.DistLazy;
import org.shsts.tinycorelib.api.gui.MenuBase;
import org.shsts.tinycorelib.api.gui.client.IMenuScreenFactory;
import org.shsts.tinycorelib.api.gui.client.MenuScreenBase;
import org.shsts.tinycorelib.api.network.IChannel;
import org.shsts.tinycorelib.api.registrate.builder.IMenuBuilder;
import org.shsts.tinycorelib.api.registrate.entry.IMenuType;
import org.shsts.tinycorelib.content.gui.SmartMenuType;
import org.shsts.tinycorelib.content.registrate.Registrate;
import org.shsts.tinycorelib.content.registrate.entry.MenuTypeEntry;
import org.shsts.tinycorelib.content.registrate.handler.MenuTypeHandler;

import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MenuBuilder<M extends MenuBase, P>
    extends EntryBuilder<MenuType<?>, MenuType<?>, P, IMenuBuilder<M, P>>
    implements IMenuBuilder<M, P> {
    private final Function<MenuBase.Properties, M> menuFactory;
    @Nullable
    private IChannel channel = null;
    private Function<BlockEntity, Component> title = $ -> TextComponent.EMPTY;
    @Nullable
    private DistLazy<IMenuScreenFactory<M, ?>> screenFactory = null;

    public MenuBuilder(Registrate registrate, P parent, String id,
        Function<MenuBase.Properties, M> menuFactory) {
        super(registrate, registrate.menuTypeHandler, parent, id);
        this.menuFactory = menuFactory;
    }

    @Override
    public IMenuBuilder<M, P> channel(IChannel value) {
        channel = value;
        return self();
    }

    @Override
    public IMenuBuilder<M, P> title(Function<BlockEntity, Component> value) {
        title = value;
        return self();
    }

    @Override
    public IMenuBuilder<M, P> title(String key) {
        var text = new TranslatableComponent(key);
        title = $ -> text;
        return self();
    }

    @Override
    public IMenuBuilder<M, P> screen(DistLazy<IMenuScreenFactory<M, ? extends MenuScreenBase<M>>> value) {
        screenFactory = value;
        return self();
    }

    @Override
    protected MenuTypeEntry createEntry() {
        return ((MenuTypeHandler) handler).registerType(this);
    }

    @Override
    protected SmartMenuType<M> createObject() {
        assert screenFactory != null;
        var type = new SmartMenuType<>(channel, title, menuFactory);
        screenFactory.runOnDist(Dist.CLIENT, () -> factory ->
            registrate.menuScreenHandler.setMenuScreen(type, factory));
        return type;
    }

    @Override
    public IMenuType register() {
        return (IMenuType) super.register();
    }
}
