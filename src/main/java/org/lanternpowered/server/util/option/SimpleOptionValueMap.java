/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
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
