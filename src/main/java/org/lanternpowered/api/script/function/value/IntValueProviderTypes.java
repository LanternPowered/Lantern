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

public final class IntValueProviderTypes {

    public static final IntValueProviderType CONSTANT = DummyObjectProvider.createFor(IntValueProviderType.class, "CONSTANT");
    public static final IntValueProviderType RANGE = DummyObjectProvider.createFor(IntValueProviderType.class, "RANGE");

    private IntValueProviderTypes() {
    }
}
