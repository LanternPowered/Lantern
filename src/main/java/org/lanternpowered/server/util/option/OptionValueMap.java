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

import java.util.Optional;

public interface OptionValueMap {

    /**
     * Gets the value of the option if present.
     *
     * @param option The option
     * @param <V> The value type
     * @return The value
     */
    <V> Optional<V> get(Option<V> option);

    default <V> Optional<V> getOrDefault(Option<V> option) {
        final Optional<V> opt = this.get(option);
        return opt.isPresent() ? opt : option.getDefaultValue();
    }

    /**
     * Puts the value for the specified option into
     * the list.
     *
     * @param option The option
     * @param value The value
     * @param <V> The value type
     * @return The old value
     */
    <V> Optional<V> put(Option<V> option, V value);

    void copyTo(OptionValueMap valueMap);
}
