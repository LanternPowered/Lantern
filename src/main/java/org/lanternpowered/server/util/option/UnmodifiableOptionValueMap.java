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

import java.util.Map;
import java.util.Optional;

public class UnmodifiableOptionValueMap extends AbstractOptionValueMap {

    final OptionValueMap optionValueMap;

    public UnmodifiableOptionValueMap(OptionValueMap optionValueMap) {
        this.optionValueMap = optionValueMap;
    }

    @Override
    public <V> Optional<V> get(Option<V> option) {
        return this.optionValueMap.get(option);
    }

    @Override
    public <V> Optional<V> put(Option<V> option, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void copyTo(OptionValueMap valueMap) {
        this.optionValueMap.copyTo(valueMap);
    }

    @Override
    void put(Map<String, Object> map) {
        throw new UnsupportedOperationException();
    }
}
