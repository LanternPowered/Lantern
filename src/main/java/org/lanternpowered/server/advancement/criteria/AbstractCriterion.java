/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.advancement.criteria;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import org.spongepowered.api.advancement.criteria.AdvancementCriterion;
import org.spongepowered.api.advancement.criteria.AndCriterion;
import org.spongepowered.api.advancement.criteria.OperatorCriterion;
import org.spongepowered.api.advancement.criteria.OrCriterion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
        return build(AndCriterion.class, LanternAndCriterion::new, Iterables.concat(Collections.singleton(this), criteria));
    }

    @Override
    public AdvancementCriterion or(AdvancementCriterion... criteria) {
        return or(Arrays.asList(criteria));
    }

    @Override
    public AdvancementCriterion or(Iterable<AdvancementCriterion> criteria) {
        return build(OrCriterion.class, LanternOrCriterion::new, Iterables.concat(Collections.singleton(this), criteria));
    }

    @Override
    public String toString() {
        return toStringHelper().toString();
    }

    MoreObjects.ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
                .add("name", this.name);
    }

    public static Collection<AdvancementCriterion> getRecursiveCriteria(AdvancementCriterion criterion) {
        return criterion instanceof AbstractOperatorCriterion ? ((AbstractOperatorCriterion) criterion).getRecursiveChildren() :
                Collections.singletonList(criterion);
    }

    public static AdvancementCriterion build(Class<? extends OperatorCriterion> type,
            Function<Set<AdvancementCriterion>, AdvancementCriterion> function, Iterable<AdvancementCriterion> criteria) {
        final List<AdvancementCriterion> builder = new ArrayList<>();
        for (AdvancementCriterion criterion : criteria) {
            build(type, criterion, builder);
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
