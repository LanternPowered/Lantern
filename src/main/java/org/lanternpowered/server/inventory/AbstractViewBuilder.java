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

import org.spongepowered.api.item.inventory.Carrier;

import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings("unchecked")
public class AbstractViewBuilder<R extends T, T extends AbstractInventory, B extends AbstractViewBuilder<R, T, B>>
        extends AbstractBuilder<R, T, B> {

    @Nullable private Carrier carrier;

    public B withCarrier(@Nullable Carrier carrier) {
        this.carrier = carrier;
        return (B) this;
    }

    @Override
    protected void build(R inventory) {
        if (this.carrier != null && inventory instanceof AbstractMutableInventory) {
            inventory.setCarrier(this.carrier, false);
        }
    }
}
