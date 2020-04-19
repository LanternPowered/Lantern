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

public final class DoubleValueProviderTypes {

    public static final DoubleValueProviderType CONSTANT = DummyObjectProvider.createFor(DoubleValueProviderType.class, "CONSTANT");
    public static final DoubleValueProviderType RANGE = DummyObjectProvider.createFor(DoubleValueProviderType.class, "RANGE");

    private DoubleValueProviderTypes() {
    }
}
