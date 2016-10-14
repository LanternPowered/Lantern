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
package org.lanternpowered.server.data.property;

import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.property.DirectionRelativePropertyHolder;
import org.spongepowered.api.data.property.PropertyHolder;
import org.spongepowered.api.data.property.PropertyStore;
import org.spongepowered.api.util.Direction;

import java.util.Optional;

public interface AbstractDirectionRelativePropertyHolder extends AbstractPropertyHolder, DirectionRelativePropertyHolder {

    @Override
    default <T extends Property<?, ?>> Optional<T> getProperty(Direction direction, Class<T> clazz) {
        return getPropertyFor(this, direction, clazz);
    }

    static <T extends Property<?, ?>> Optional<T> getPropertyFor(PropertyHolder propertyHolder, Direction direction, PropertyStore<T> propertyStore) {
        if (propertyStore instanceof DirectionRelativePropertyStore) {
            return ((DirectionRelativePropertyStore<T>) propertyStore).getFor(propertyHolder, direction);
        } else {
            return Optional.empty();
        }
    }

    static <T extends Property<?, ?>> Optional<T> getPropertyFor(PropertyHolder propertyHolder, Direction direction, Class<T> clazz) {
        final Optional<PropertyStore<T>> optional = LanternPropertyRegistry.getInstance().getStore(clazz);
        if (optional.isPresent()) {
            return getPropertyFor(propertyHolder, direction, optional.get());
        }
        return Optional.empty();
    }
}
