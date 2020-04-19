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
package org.lanternpowered.server.network.item;

import org.spongepowered.api.data.persistence.DataView;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class RawItemStack {

    private final String itemType;
    private final int amount;

    @Nullable private final DataView dataView;

    public RawItemStack(String itemType, int amount, @Nullable DataView dataView) {
        this.dataView = dataView;
        this.itemType = itemType;
        this.amount = amount;
    }

    public String getType() {
        return this.itemType;
    }

    @Nullable
    public DataView getDataView() {
        return this.dataView;
    }

    public int getAmount() {
        return this.amount;
    }
}
