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
package org.lanternpowered.api.script.function.value;

import org.spongepowered.api.util.generator.dummy.DummyObjectProvider;

public final class FloatValueProviderTypes {

    public static final FloatValueProviderType CONSTANT = DummyObjectProvider.createFor(FloatValueProviderType.class, "CONSTANT");
    public static final FloatValueProviderType RANGE = DummyObjectProvider.createFor(FloatValueProviderType.class, "RANGE");

    private FloatValueProviderTypes() {
    }
}
