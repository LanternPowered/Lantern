/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) Contributors
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

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Optional;

public final class RuleType<T> {

    private static final Map<String, RuleType> rulesByName = Maps.newHashMap();

    public static Optional<RuleType<?>> get(String name) {
        return Optional.ofNullable(rulesByName.get(name));
    }

    /**
     * Gets the registered {@link RuleType<T>} or creates a new one for the specified name, data type and default value.
     *
     * @param name the name
     * @param dataType the data type
     * @param defaultValue the default value
     * @return the rule
     * @throws IllegalArgumentException if there is already a rule registered with that name
     */
    public static <T> RuleType<?> getOrCreate(String name, RuleDataType<T> dataType, T defaultValue) throws IllegalArgumentException {
        checkNotNull(dataType, "dataType");
        checkNotNull(defaultValue, "defaultValue");
        return rulesByName.computeIfAbsent(checkNotNull(name, "name"), rule0 -> new RuleType<>(name, dataType, defaultValue));
    }

    /**
     * Creates a new {@link RuleType<T>} for the specified name, data type and default value.
     *
     * @param name the name
     * @param dataType the data type
     * @param defaultValue the default value
     * @return the rule
     * @throws IllegalArgumentException if there is already a rule registered with that name
     */
    public static <T> RuleType<T> create(String name, RuleDataType<T> dataType, T defaultValue) throws IllegalArgumentException {
        checkNotNull(dataType, "dataType");
        checkNotNull(defaultValue, "defaultValue");
        if (rulesByName.containsKey(checkNotNull(name, "name"))) {
            throw new IllegalArgumentException("There is already a game rule with the name: " + name);
        }
        RuleType<T> ruleType = new RuleType<>(name, dataType, defaultValue);
        rulesByName.put(name, ruleType);
        return ruleType;
    }

    private final RuleDataType<T> dataType;
    private final T defaultValue;
    private final String name;

    private RuleType(String name, RuleDataType<T> dataType, T defaultValue) {
        this.defaultValue = defaultValue;
        this.dataType = dataType;
        this.name = name;
    }

    /**
     * Gets the name of the rule type.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the default value of this rule type.
     *
     * @return the value
     */
    public T getDefaultValue() {
        return this.defaultValue;
    }

    /**
     * Gets the data type of this rule.
     *
     * @return the data type
     */
    public RuleDataType<T> getDataType() {
        return this.dataType;
    }

}
