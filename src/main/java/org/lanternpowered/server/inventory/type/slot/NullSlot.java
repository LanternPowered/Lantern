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
package org.lanternpowered.server.inventory.type.slot;

import org.lanternpowered.server.item.predicate.ItemPredicate;

public class NullSlot extends LanternFilteringSlot {

    @Override
    public void init() {
        super.init();
        // Nothing can be put in
        setFilter(ItemPredicate.ofTypePredicate(type -> false));
    }
}
