package org.shsts.tinycorelib.content.meta;

import com.mojang.logging.LogUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import org.shsts.tinycorelib.api.meta.IMetaConsumer;
import org.shsts.tinycorelib.api.meta.IMetaExecutor;
import org.shsts.tinycorelib.api.meta.MetaLoadingException;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MetaExecutor implements IMetaExecutor {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final MetaLocator locator;
    private final String folder;
    private final IMetaConsumer consumer;

    public MetaExecutor(MetaLocator locator, String folder, IMetaConsumer consumer) {
        this.locator = locator;
        this.folder = folder;
        this.consumer = consumer;
    }

    @Override
    public void execute() {
        LOGGER.debug("{}: wait for locator to finish", this);
        locator.await();

        var metas = locator.getFolder(folder);
        LOGGER.debug("{}: execute {} meta files", this, metas.size());
        for (var meta : metas) {
            try {
                consumer.acceptMeta(meta.loc(), meta.jo());
            } catch (MetaLoadingException e) {
                LOGGER.error("{}: error consumer meta {}, skip", this, meta.loc());
            }
        }
    }

    @Override
    public String toString() {
        return "MetaExecutor[folder=" + folder + ", name=" + consumer.name() + "]";
    }
}
