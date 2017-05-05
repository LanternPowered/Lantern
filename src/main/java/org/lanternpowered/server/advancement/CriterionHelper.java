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

import org.lanternpowered.server.util.collect.Lists2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

final class CriterionHelper {

    static List<List<String>> simplifyToIds(AdvancementCriterion criterion) {
        final Wrapper wrapper = toWrapper(criterion);

        final List<Wrapper> list;
        if (wrapper instanceof And) {
            list = merge((And) wrapper);
        } else if (wrapper instanceof Or) {
            list = merge((Or) wrapper);
        } else {
            return Collections.singletonList(Collections.singletonList(((One) wrapper).id));
        }

        final List<List<String>> ids = new ArrayList<>();
        for (Wrapper wrapper1 : list) {
            if (wrapper1 instanceof Or) {
                throw new IllegalStateException("Unexpected Or.");
            } else if (wrapper1 instanceof And) {
                ids.add(((And) wrapper1).wrappers.stream().map(wrapper2 -> ((One) wrapper2).id).collect(Collectors.toList()));
            } else {
                ids.add(Collections.singletonList(((One) wrapper1).id));
            }
        }

        return ids;
    }

    private static List<Wrapper> merge(Or or) {
        final List<Wrapper> stack = new ArrayList<>();
        for (Wrapper wrapper : or.wrappers) {
            if (wrapper instanceof Or) {
                throw new IllegalStateException("Or cannot be directly nested into Or.");
            } else if (wrapper instanceof And) {
                stack.addAll(merge((And) wrapper));
            } else {
                stack.add(wrapper);
            }
        }
        return stack;
    }

    private static List<Wrapper> merge(And and) {
        final List<Wrapper> stack = new ArrayList<>();
        for (Wrapper wrapper : and.wrappers) {
            if (wrapper instanceof Or) {
                final List<Wrapper> wrappers = merge((Or) wrapper);
                if (stack.isEmpty()) {
                    stack.addAll(wrappers);
                } else {
                    final List<Wrapper> temp = new ArrayList<>(stack);
                    stack.clear();
                    for (Wrapper tempWrapper : temp) {
                        //noinspection Convert2streamapi
                        for (Wrapper criterion1 : wrappers) {
                            stack.add(tempWrapper.and(criterion1));
                        }
                    }
                }
            } else if (wrapper instanceof And) {
                throw new IllegalStateException("And cannot be directly nested into And.");
            } else {
                if (stack.isEmpty()) {
                    stack.add(wrapper);
                } else {
                    final List<Wrapper> temp = new ArrayList<>(stack);
                    stack.clear();
                    //noinspection Convert2streamapi
                    for (Wrapper tempWrapper : temp) {
                        stack.add(tempWrapper.and(wrapper));
                    }
                }
            }
        }
        return stack;
    }

    private static Wrapper toWrapper(AdvancementCriterion criterion) {
        if (criterion instanceof AdvancementCriterion.And) {
            return new And(((AdvancementCriterion.And) criterion).getCriteria().stream()
                    .map(CriterionHelper::toWrapper).collect(Collectors.toList()));
        } else if (criterion instanceof AdvancementCriterion.Or) {
            return new Or(((AdvancementCriterion.Or) criterion).getCriteria().stream()
                    .map(CriterionHelper::toWrapper).collect(Collectors.toList()));
        } else if (criterion instanceof ScoreAdvancementCriterion) {
            final List<Wrapper> wrappers = new ArrayList<>();
            for (String id : ((ScoreAdvancementCriterion) criterion).ids) {
                wrappers.add(new One(id));
            }
            return new And(wrappers);
        } else {
            return new One(criterion.id);
        }
    }

    private static abstract class Wrapper {

        public static Wrapper EMPTY = new Wrapper() {};

        Wrapper and(Wrapper wrapper) {
            if (this == EMPTY && wrapper == EMPTY) {
                return EMPTY;
            } else if (wrapper == EMPTY) {
                return this;
            } else if (this == EMPTY) {
                return wrapper;
            }
            final List<Wrapper> wrappers = new ArrayList<>();
            if (this instanceof And) {
                wrappers.addAll(((And) this).wrappers);
            } else {
                wrappers.add(this);
            }
            if (wrapper instanceof And) {
                wrappers.addAll(((And) wrapper).wrappers);
            } else {
                wrappers.add(wrapper);
            }
            return new And(wrappers);
        }
    }

    private static class One extends Wrapper {

        final String id;

        private One(String id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return this.id;
        }
    }

    private static abstract class Multi extends Wrapper {

        final List<Wrapper> wrappers;

        private Multi(List<Wrapper> wrappers) {
            this.wrappers = wrappers;
        }

        @Override
        public String toString() {
            return Lists2.toString(this.wrappers);
        }
    }

    private static class And extends Multi {

        private And(List<Wrapper> wrappers) {
            super(wrappers);
        }
    }

    private static class Or extends Multi {

        private Or(List<Wrapper> wrappers) {
            super(wrappers);
        }
    }

    private CriterionHelper() {
    }
}
