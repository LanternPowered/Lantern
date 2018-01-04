package org.lanternpowered.server.advancement.criteria;

import org.spongepowered.api.advancement.criteria.trigger.FilteredTrigger;

import java.util.Optional;

public final class EmptyCriterion extends AbstractCriterion {

    public static final EmptyCriterion INSTANCE = new EmptyCriterion();

    private EmptyCriterion() {
        super("empty");
    }

    @Override
    public Optional<FilteredTrigger<?>> getTrigger() {
        return Optional.empty();
    }
}
