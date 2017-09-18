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

import org.lanternpowered.server.event.CauseStack;
import org.lanternpowered.server.world.LanternWorld;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.world.ChangeWorldGameRuleEvent;

import java.util.Optional;

import javax.annotation.Nullable;

public final class Rule<T> {

    private final RuleType<T> ruleType;
    private final Rules rules;
    private T value;

    Rule(Rules rules, RuleType<T> ruleType) {
        this.value = ruleType.getDefaultValue();
        this.ruleType = ruleType;
        this.rules = rules;
    }

    /**
     * Sets the raw value of this rule, the value will be automatically parsed
     * as the required type.
     *
     * @param value The raw value
     * @throws IllegalArgumentException if the parsing failed
     */
    public void setRawValue(String value) throws IllegalArgumentException {
        setValue0(this.ruleType.getDataType().parse(checkNotNull(value, "value")), value);
    }

    /**
     * Gets the raw value of this rule.
     *
     * @return The raw value
     */
    public String getRawValue() {
        return this.ruleType.getDataType().serialize(this.value);
    }

    /**
     * Sets the value of this rule.
     *
     * @param value The value
     */
    public void setValue(T value) {
        setValue0(checkNotNull(value, "value"), null);
    }

    /**
     * Gets the value of this rule.
     *
     * @return The value
     */
    public T getValue() {
        return this.value;
    }

    private void setValue0(T newValue, @Nullable String newRawValue) {
        final Optional<LanternWorld> optWorld = this.rules.getWorld();
        if (optWorld.isPresent() && !newValue.equals(this.value)) {
            final LanternWorld world = optWorld.get();
            if (newRawValue == null) {
                newRawValue = this.ruleType.getDataType().serialize(newValue);
            }
            final Cause cause = CauseStack.current().getCurrentCause();
            final ChangeWorldGameRuleEvent event = SpongeEventFactory.createChangeWorldGameRuleEvent(
                    cause, getRawValue(), newRawValue, this.ruleType.getName(), world);
            if (Sponge.getEventManager().post(event)) {
                return;
            }
        }
        this.value = newValue;
    }
}
