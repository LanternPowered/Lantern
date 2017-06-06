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
package org.lanternpowered.server.data.processor;

import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.value.BaseValue;

@SuppressWarnings("unchecked")
public final class ValueProcessorHandlers {

    public static final class Remove {

        private static ValueProcessorBuilder.RemoveFunction FAIL_ALWAYS_REMOVE_HANDLER =
                (key, container) -> DataTransactionResult.failNoData();

        /**
         * Gets a remove handler that will always fail.
         *
         * @param <V> The type of the value
         * @return The remove handler
         */
        public static <V extends BaseValue<E>, E> ValueProcessorBuilder.RemoveFunction<V, E> failAlways() {
            return (ValueProcessorBuilder.RemoveFunction<V, E>) FAIL_ALWAYS_REMOVE_HANDLER;
        }
    }

    private ValueProcessorHandlers() {
    }
}
