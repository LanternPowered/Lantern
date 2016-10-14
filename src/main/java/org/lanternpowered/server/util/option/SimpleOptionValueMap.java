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
package org.lanternpowered.server.util.option;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SimpleOptionValueMap extends AbstractOptionValueMap {

    final Map<String, Object> values;

    public SimpleOptionValueMap() {
        this(new HashMap<>());
    }

    public SimpleOptionValueMap(Map<String, Object> values) {
        this.values = checkNotNull(values, "values");
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> Optional<V> get(Option<V> option) {
        checkNotNull(option, "option");
        return Optional.ofNullable((V) this.values.get(
                checkNotNull(option, "option").getId()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> Optional<V> put(Option<V> option, V value) {
        checkNotNull(option, "option");
        checkNotNull(value, "value");
        return Optional.ofNullable((V) this.values.put(
                checkNotNull(option, "option").getId(), checkNotNull(value, "value")));
    }

    @Override
    public void copyTo(OptionValueMap valueMap) {
        ((AbstractOptionValueMap) valueMap).put(this.values);
    }

    @Override
    void put(Map<String, Object> map) {
        this.values.putAll(map);
    }
}
