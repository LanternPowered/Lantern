package org.lanternpowered.server.advancement;

import com.google.common.base.MoreObjects;
import org.lanternpowered.server.catalog.PluginCatalogType;
import org.lanternpowered.server.event.CauseStack;
import org.spongepowered.api.advancement.Advancement;
import org.spongepowered.api.advancement.AdvancementTree;
import org.spongepowered.api.advancement.DisplayInfo;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

public class LanternAdvancementTree extends PluginCatalogType.Base implements AdvancementTree {

    private final Advancement rootAdvancement;
    private final String background;

    LanternAdvancementTree(LanternAdvancementTreeBuilder builder) {
        super(CauseStack.current().first(PluginContainer.class).get().getId(), builder.id,
                builder.name == null ? builder.rootAdvancement.getDisplayInfo()
                        .map(DisplayInfo::getTitle).map(Text::toPlain).orElse(builder.id) : builder.name);
        this.rootAdvancement = builder.rootAdvancement;
        this.background = builder.background;
    }

    @Override
    public Advancement getRootAdvancement() {
        return this.rootAdvancement;
    }

    @Override
    public String getBackgroundPath() {
        return this.background;
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("rootAdvancement", this.rootAdvancement.getId())
                .add("background", this.background);
    }
}
