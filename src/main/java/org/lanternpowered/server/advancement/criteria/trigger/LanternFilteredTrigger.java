package org.lanternpowered.server.advancement.criteria.trigger;

import com.google.common.base.MoreObjects;
import org.spongepowered.api.advancement.criteria.trigger.FilteredTrigger;
import org.spongepowered.api.advancement.criteria.trigger.FilteredTriggerConfiguration;
import org.spongepowered.api.advancement.criteria.trigger.Trigger;

public final class LanternFilteredTrigger<C extends FilteredTriggerConfiguration> implements FilteredTrigger<C> {

    private final Trigger<C> type;
    private final C config;

    LanternFilteredTrigger(Trigger<C> type, C config) {
        this.config = config;
        this.type = type;
    }

    @Override
    public Trigger<C> getType() {
        return this.type;
    }

    @Override
    public C getConfiguration() {
        return this.config;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("type", this.type.getId())
                .add("config", this.config)
                .toString();
    }
}
