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
package org.lanternpowered.server.advancement;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import org.lanternpowered.server.util.collect.Lists2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Represents a criterion that should be acquired
 * to unlock a {@link Advancement}.
 */
public class AdvancementCriterion {

    public static final AdvancementCriterion EMPTY = new AdvancementCriterion("empty", "empty");

    private static final AtomicInteger COUNTER = new AtomicInteger();
    static final String DUMMY = newIdentifier();

    static String newIdentifier() {
        return Integer.toUnsignedString(COUNTER.getAndIncrement(), 36);
    }

    /**
     * The identifier that is used to send the advancement progress to the client.
     */
    final String id;
    private final String name;

    public AdvancementCriterion(String name) {
        this(newIdentifier(), name);
    }

    AdvancementCriterion(String id, String name) {
        checkNotNull(name, "name");
        checkNotNull(id, "id");
        this.name = name;
        this.id = id;
    }

    @Override
    public String toString() {
        return this.name;
    }

    Set<AdvancementCriterion> getCriteria(boolean onlyLeaves) {
        return this == EMPTY ? Collections.emptySet() : Collections.singleton(this);
    }

    /**
     * Gets the name of this criterion.
     *
     * @return The name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the leaf {@link AdvancementCriterion}s. This means that
     * there won't be any AND or OR operations present in this list.
     *
     * @return The leaf criteria
     */
    public Set<AdvancementCriterion> getLeafCriteria() {
        return getCriteria(true);
    }

    /**
     * Creates a {@link And} operation.
     *
     * @param criteria The criteria
     * @return The and operation
     */
    public AdvancementCriterion and(AdvancementCriterion... criteria) {
        return build(And.class, And::new, this, criteria);
    }

    /**
     * Creates a {@link Or} operation.
     *
     * @param criteria The criteria
     * @return The or operation
     */
    public AdvancementCriterion or(AdvancementCriterion... criteria) {
        return  build(Or.class, Or::new, this, criteria);
    }

    /**
     * Builds a new {@link And} operation with the given {@link AdvancementCriterion}s.
     *
     * @param criteria The other criteria
     * @return The and operation
     */
    public static AdvancementCriterion buildAnd(AdvancementCriterion... criteria) {
        return build(And.class, And::new, EMPTY, criteria);
    }

    /**
     * Builds a new {@link Or} operation with the given {@link AdvancementCriterion}s.
     *
     * @param criteria The other criteria
     * @return The or operation
     */
    public static AdvancementCriterion buildOr(AdvancementCriterion... criteria) {
        return build(Or.class, Or::new, EMPTY, criteria);
    }

    private static AdvancementCriterion build(Class<? extends Multi> type,
            Function<List<AdvancementCriterion>, AdvancementCriterion> function,
            AdvancementCriterion criterion,
            AdvancementCriterion... criteria) {
        checkNotNull(criteria, "criteria");
        final List<AdvancementCriterion> builder = new ArrayList<>();
        build(type, criterion, builder);
        for (AdvancementCriterion criterion1 : criteria) {
            build(type, criterion1, builder);
        }
        return builder.isEmpty() ? EMPTY : builder.size() == 1 ? builder.get(0) : function.apply(builder);
    }

    private static void build(Class<? extends Multi> type, AdvancementCriterion criterion, List<AdvancementCriterion> criteria) {
        if (criterion == EMPTY) {
            return;
        }
        checkNotNull(criterion, "criterion");
        if (type.isInstance(criterion)) {
            criteria.addAll(((Multi) criterion).criteria);
        } else {
            criteria.add(criterion);
        }
    }

    static abstract class Multi extends AdvancementCriterion {

        private final List<AdvancementCriterion> criteria;

        private Multi(String id, List<AdvancementCriterion> criteria) {
            super(id, id + Lists2.toString(criteria.stream().map(AdvancementCriterion::getName).collect(Collectors.toList())));
            this.criteria = criteria;
        }

        /**
         * Gets the {@link AdvancementCriterion}s.
         *
         * @return The criteria
         */
        public List<AdvancementCriterion> getCriteria() {
            return this.criteria;
        }

        @Override
        Set<AdvancementCriterion> getCriteria(boolean onlyLeaves) {
            final ImmutableSet.Builder<AdvancementCriterion> criteria = ImmutableSet.builder();
            if (!onlyLeaves) {
                criteria.add(this);
            }
            for (AdvancementCriterion criterion : this.criteria) {
                criteria.addAll(criterion.getCriteria(onlyLeaves));
            }
            return criteria.build();
        }
    }

    /**
     * Creates a {@link AdvancementCriterion} with an "and" operation. All the criteria
     * should be {@code true} in order for the final result to be {@code true}.
     */
    public static final class And extends Multi {

        private And(List<AdvancementCriterion> criteria) {
            super("and", criteria);
        }
    }

    /**
     * Creates a {@link AdvancementCriterion} with an "or" operation. One the criteria
     * should be {@code true} in order for the final result to be {@code true}.
     */
    public static final class Or extends Multi {

        private Or(List<AdvancementCriterion> criteria) {
            super("or", criteria);
        }
    }
}
