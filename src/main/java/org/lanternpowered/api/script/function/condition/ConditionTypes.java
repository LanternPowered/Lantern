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
package org.lanternpowered.api.script.function.condition;

import org.spongepowered.api.util.generator.dummy.DummyObjectProvider;

public final class ConditionTypes {

    public static final ConditionType AND = DummyObjectProvider.createFor(ConditionType.class, "AND");
    public static final ConditionType OR = DummyObjectProvider.createFor(ConditionType.class, "OR");

    private ConditionTypes() {
    }
}
