package org.lanternpowered.server.advancement.criteria;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import org.spongepowered.api.advancement.criteria.AdvancementCriterion;
import org.spongepowered.api.advancement.criteria.AndCriterion;
import org.spongepowered.api.advancement.criteria.OperatorCriterion;
import org.spongepowered.api.advancement.criteria.OrCriterion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public abstract class AbstractCriterion implements AdvancementCriterion {

    private final String name;

    AbstractCriterion(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public AdvancementCriterion and(AdvancementCriterion... criteria) {
        return and(Arrays.asList(criteria));
    }

    @Override
    public AdvancementCriterion and(Iterable<AdvancementCriterion> criteria) {
        return build(AndCriterion.class, LanternAndCriterion::new, this, criteria);
    }

    @Override
    public AdvancementCriterion or(AdvancementCriterion... criteria) {
        return or(Arrays.asList(criteria));
    }

    @Override
    public AdvancementCriterion or(Iterable<AdvancementCriterion> criteria) {
        return build(OrCriterion.class, LanternOrCriterion::new, this, criteria);
    }

    @Override
    public String toString() {
        return toStringHelper().toString();
    }

    MoreObjects.ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
                .add("name", this.name);
    }

    private static AdvancementCriterion build(Class<? extends OperatorCriterion> type,
            Function<Set<AdvancementCriterion>, AdvancementCriterion> function,
            AdvancementCriterion criterion, Iterable<AdvancementCriterion> criteria) {
        checkNotNull(criteria, "criteria");
        final List<AdvancementCriterion> builder = new ArrayList<>();
        build(type, criterion, builder);
        for (AdvancementCriterion criterion1 : criteria) {
            build(type, criterion1, builder);
        }
        return builder.isEmpty() ? EmptyCriterion.INSTANCE : builder.size() == 1 ? builder.get(0) : function.apply(ImmutableSet.copyOf(builder));
    }

    private static void build(Class<? extends OperatorCriterion> type, AdvancementCriterion criterion, List<AdvancementCriterion> criteria) {
        if (criterion == EmptyCriterion.INSTANCE) {
            return;
        }
        checkNotNull(criterion, "criterion");
        if (type.isInstance(criterion)) {
            criteria.addAll(((OperatorCriterion) criterion).getCriteria());
        } else {
            criteria.add(criterion);
        }
    }
}
