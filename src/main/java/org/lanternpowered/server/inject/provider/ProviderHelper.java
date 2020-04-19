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
