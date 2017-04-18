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
package org.lanternpowered.server.world.rules;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.lanternpowered.server.world.LanternWorld;
import org.lanternpowered.server.world.LanternWorldProperties;

import java.util.Map;
import java.util.Optional;

public final class Rules implements RuleHolder {

    private final Map<RuleType<?>, Rule<?>> rules = Maps.newHashMap();
    private final LanternWorldProperties worldProperties;

    public Rules(LanternWorldProperties worldProperties) {
        this.worldProperties = worldProperties;
    }

    public Map<RuleType<?>, Rule<?>> getRules() {
        return ImmutableMap.copyOf(this.rules);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<Rule<T>> removeRule(RuleType<T> ruleType) {
        return Optional.ofNullable((Rule<T>) this.rules.remove(checkNotNull(ruleType, "ruleType")));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<Rule<T>> getRule(RuleType<T> ruleType) {
        return Optional.ofNullable((Rule<T>) this.rules.get(checkNotNull(ruleType, "ruleType")));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Rule<T> getOrCreateRule(RuleType<T> ruleType) {
        return (Rule) this.rules.computeIfAbsent(checkNotNull(ruleType, "ruleType"), key -> new Rule(this, ruleType));
    }

    /**
     * Gets the world instance attached to this rules, may be absent.
     *
     * @return The world
     */
    public Optional<LanternWorld> getWorld() {
        return this.worldProperties.getWorld();
    }

}
