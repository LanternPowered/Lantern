package org.lanternpowered.server.advancement.criteria;

import org.spongepowered.api.advancement.criteria.trigger.FilteredTrigger;

import java.util.Optional;

import javax.annotation.Nullable;

public class LanternCriterion extends AbstractCriterion {

    @Nullable private final FilteredTrigger<?> trigger;

    LanternCriterion(String name, @Nullable FilteredTrigger<?> trigger) {
        super(name);
        this.trigger = trigger;
    }

    @Override
    public Optional<FilteredTrigger<?>> getTrigger() {
        return Optional.ofNullable(this.trigger);
    }
}
