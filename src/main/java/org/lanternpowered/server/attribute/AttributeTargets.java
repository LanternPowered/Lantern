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
package org.lanternpowered.server.attribute;

import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider;

import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class AttributeTargets {

    public static final Predicate<DataHolder> GENERIC = DummyObjectProvider.createFor(Predicate.class, "GENERIC");
    public static final Predicate<DataHolder> HORSE = DummyObjectProvider.createFor(Predicate.class, "HORSE");
    public static final Predicate<DataHolder> ZOMBIE = DummyObjectProvider.createFor(Predicate.class, "ZOMBIE");
}
