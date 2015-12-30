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
package org.lanternpowered.server.util.cache;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Equivalence;
import com.google.common.cache.CacheBuilder;
import org.lanternpowered.server.game.LanternGame;

import java.lang.reflect.Method;

public final class CacheBuilderHelper {

    private static Method keyEquivalenceMethod;
    private static Method valueEquivalenceMethod;

    /**
     * Sets a custom {@code Equivalence} strategy for comparing keys.
     *
     * <p>By default, the cache uses {@link Equivalence#identity} to determine key equality when
     * {@link CacheBuilder#weakKeys} is specified, and {@link Equivalence#equals} otherwise.
     * 
     * @param builder the builder to apply the equivalence to
     * @param equivalence the equivalence strategy to apply
     * @return the builder for chaining
     */
    public static <K, V> CacheBuilder<K, V> keyEquivalence(CacheBuilder<K, V> builder,
            Equivalence<K> equivalence) {
        checkNotNull(builder, "cacheBuilder");
        try {
            if (keyEquivalenceMethod == null) {
                keyEquivalenceMethod = CacheBuilder.class.getDeclaredMethod(
                        "keyEquivalence", Equivalence.class);
                keyEquivalenceMethod.setAccessible(true);
            }
            keyEquivalenceMethod.invoke(builder, equivalence);
        } catch (IllegalStateException | NullPointerException e) {
            throw e;
        } catch (Throwable t) {
            LanternGame.log().error("Unable to set the keyEquivalence of a CacheBuilder", t);
        }
        return builder;
    }

    /**
     * Sets a custom {@code Equivalence} strategy for comparing values.
     *
     * <p>By default, the cache uses {@link Equivalence#identity} to determine value equality when
     * {@link CacheBuilder#weakValues} or {@link CacheBuilder#softValues} is specified,
     * and {@link Equivalence#equals} otherwise.
     * 
     * @param builder the builder to apply the equivalence to
     * @param equivalence the equivalence strategy to apply
     * @return the builder for chaining
     */
    public static <K, V> CacheBuilder<K, V> valueEquivalence(CacheBuilder<K, V> builder,
            Equivalence<V> equivalence) {
        checkNotNull(builder, "cacheBuilder");
        try {
            if (valueEquivalenceMethod == null) {
                valueEquivalenceMethod = CacheBuilder.class.getDeclaredMethod(
                        "valueEquivalence", Equivalence.class);
                valueEquivalenceMethod.setAccessible(true);
            }
            valueEquivalenceMethod.invoke(builder, equivalence);
        } catch (IllegalStateException | NullPointerException e) {
            throw e;
        } catch (Throwable t) {
            LanternGame.log().error("Unable to set the valueEquivalence of a CacheBuilder", t);
        }
        return builder;
    }

    private CacheBuilderHelper() {
    }
}
