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
package org.lanternpowered.server.util.collect;

import com.google.common.collect.Maps;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

@NonnullByDefault
public final class Sets2 {

    private Sets2() {
    }

    /**
     * Creates a new <strong>weak</strong> {@link java.util.HashSet}.
     * 
     * @return the weak hash set
     */
    public static <T> Set<T> newWeakHashSet() {
        return Collections.newSetFromMap(new WeakHashMap<>());
    }

    /**
     * Creates a new <strong>weak</strong> {@link java.util.HashSet} with the specified objects as
     * initial contents.
     *
     * @param objects the objects
     * @return the weak hash set
     */
    public static <T> Set<T> newWeakHashSet(Iterable<? extends T> objects) {
        Map<T, Boolean> map = Maps.newHashMap();
        for (T object : objects) {
            map.put(object, true);
        }
        return Collections.newSetFromMap(new WeakHashMap<>(map));
    }

}
