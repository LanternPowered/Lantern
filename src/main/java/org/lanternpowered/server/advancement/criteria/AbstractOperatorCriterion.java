package org.lanternpowered.server.advancement.criteria;

import com.google.common.collect.ImmutableSet;
import org.spongepowered.api.advancement.criteria.AdvancementCriterion;
import org.spongepowered.api.advancement.criteria.OperatorCriterion;
import org.spongepowered.api.advancement.criteria.trigger.FilteredTrigger;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import javax.annotation.Nullable;

public abstract class AbstractOperatorCriterion extends AbstractCriterion implements OperatorCriterion {

    private final Collection<AdvancementCriterion> criteria;

    @Nullable private Collection<AdvancementCriterion> recursiveChildrenCriteria;
    @Nullable private Collection<AdvancementCriterion> leafChildrenCriteria;

    AbstractOperatorCriterion(String namePrefix, Collection<AdvancementCriterion> criteria) {
        super(namePrefix + Arrays.toString(criteria.stream().map(AdvancementCriterion::getName).toArray(String[]::new)));
        this.criteria = criteria;
    }

    @Override
    public Optional<FilteredTrigger<?>> getTrigger() {
        return Optional.empty();
    }

    private Collection<AdvancementCriterion> getAllChildrenCriteria0(boolean onlyLeaves) {
        final ImmutableSet.Builder<AdvancementCriterion> criteria = ImmutableSet.builder();
        if (!onlyLeaves) {
            criteria.add(this);
        }
        for (AdvancementCriterion criterion : this.criteria) {
            if (criterion instanceof OperatorCriterion) {
                criteria.addAll(((AbstractOperatorCriterion) criterion).getAllChildrenCriteria0(onlyLeaves));
            }
        }
        return criteria.build();
    }

    private Collection<AdvancementCriterion> getRecursiveChildren() {
        if (this.recursiveChildrenCriteria == null) {
            this.recursiveChildrenCriteria = getAllChildrenCriteria0(false);
        }
        return this.recursiveChildrenCriteria;
    }

    @Override
    public Collection<AdvancementCriterion> getCriteria() {
        return this.criteria;
    }

    @Override
    public Collection<AdvancementCriterion> getLeafCriteria() {
        if (this.leafChildrenCriteria == null) {
            this.leafChildrenCriteria = getAllChildrenCriteria0(true);
        }
        return this.leafChildrenCriteria;
    }

    @Override
    public Collection<AdvancementCriterion> find(String name) {
        return getRecursiveChildren().stream()
                .filter(c -> c.getName().equals(name)).collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public Optional<AdvancementCriterion> findFirst(String name) {
        return getRecursiveChildren().stream()
                .filter(c -> c.getName().equals(name)).findFirst();
    }
}
