/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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
