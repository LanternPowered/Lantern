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
package org.lanternpowered.server.block.trait;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

import com.google.common.collect.ImmutableSet;
import org.spongepowered.api.block.trait.BooleanTrait;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.util.Coerce;
import org.spongepowered.api.util.OptBool;

import java.util.Optional;

public final class LanternBooleanTrait extends LanternBlockTrait<Boolean> implements BooleanTrait {

    private final static ImmutableSet<Boolean> STATES = ImmutableSet.of(true, false);

    private LanternBooleanTrait(String name, Key<? extends Value<Boolean>> key) {
        super(name, key, Boolean.class, STATES);
    }

    /**
     * Creates a new boolean trait with the specified name.
     * 
     * @param name the name
     * @param key the key that should be attached to the trait
     * @return the boolean trait
     */
    public static BooleanTrait of(String name, Key<? extends Value<Boolean>> key) {
        return new LanternBooleanTrait(checkNotNullOrEmpty(name, "name"), checkNotNull(key, "key"));
    }

    @Override
    public Optional<Boolean> parseValue(String value) {
        value = value.toLowerCase();
        return value.equals("true") ? OptBool.TRUE : value.equals("false") ? OptBool.FALSE : Optional.empty();
    }
}
