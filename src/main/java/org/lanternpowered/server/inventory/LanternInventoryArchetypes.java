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
package org.lanternpowered.server.inventory;

import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider;

public final class LanternInventoryArchetypes {

    public static final InventoryArchetype EMPTY = DummyObjectProvider.createFor(InventoryArchetype.class, "EMPTY");

    private LanternInventoryArchetypes() {
    }
}
