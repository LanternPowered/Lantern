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
package org.lanternpowered.server.inject.provider;

import com.google.inject.name.Named;

import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

final class ProviderHelper {

    static String provideNameOrFail(AnnotatedElement annotatedElement) {
        return provideName(annotatedElement).orElseThrow(() -> new IllegalStateException("Missing @Named annotation."));
    }

    static Optional<String> provideName(AnnotatedElement annotatedElement) {
        String name = null;

        final Named named = annotatedElement.getAnnotation(Named.class);
        if (named != null) {
            name = named.value();
        } else {
            final javax.inject.Named named1 = annotatedElement.getAnnotation(javax.inject.Named.class);
            if (named1 != null) {
                name = named1.value();
            }
        }

        return Optional.ofNullable(name);
    }

    private ProviderHelper() {
    }
}
