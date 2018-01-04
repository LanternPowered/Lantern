package org.lanternpowered.server.advancement.criteria.trigger;

import com.google.common.base.MoreObjects;
import com.google.gson.JsonObject;
import org.lanternpowered.server.catalog.PluginCatalogType;
import org.lanternpowered.server.event.CauseStack;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.advancement.criteria.trigger.FilteredTriggerConfiguration;
import org.spongepowered.api.advancement.criteria.trigger.Trigger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.advancement.CriterionEvent;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nullable;

@SuppressWarnings("ConstantConditions")
public class LanternTrigger<C extends FilteredTriggerConfiguration> extends PluginCatalogType.Base implements Trigger<C> {

    private final Class<C> configType;
    private final Function<JsonObject, C> configConstructor;
    @Nullable private final Consumer<CriterionEvent.Trigger<C>> eventHandler;

    LanternTrigger(LanternTriggerBuilder<C> builder) {
        super(CauseStack.current().first(PluginContainer.class).get().getId(), builder.id,
                builder.name == null ? builder.id : builder.name);
        this.configType = builder.configType;
        this.configConstructor = builder.constructor;
        this.eventHandler = builder.eventHandler;
    }

    @Override
    public Class<C> getConfigurationType() {
        return this.configType;
    }

    @Override
    public void trigger() {
        trigger(Sponge.getServer().getOnlinePlayers());
    }

    @Override
    public void trigger(Iterable<Player> players) {
        players.forEach(this::trigger);
    }

    @Override
    public void trigger(Player player) {
    }

    public Function<JsonObject, C> getConfigConstructor() {
        return this.configConstructor;
    }

    @Nullable
    public Consumer<CriterionEvent.Trigger<C>> getEventHandler() {
        return this.eventHandler;
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("configType", this.configType.getName());
    }
}
