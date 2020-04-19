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
package org.lanternpowered.server.inventory.query;

import com.google.common.base.MoreObjects;
import org.lanternpowered.server.inventory.AbstractInventory;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.query.QueryOperation;
import org.spongepowered.api.item.inventory.query.QueryOperationType;

@SuppressWarnings("unchecked")
public final class LanternQueryOperation<T> implements QueryOperation<T> {

    private final LanternQueryOperationType<T> type;
    private final T arg;

    LanternQueryOperation(LanternQueryOperationType<T> type, T arg) {
        this.type = type;
        this.arg = arg;
    }

    @Override
    public QueryOperationType<T> getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("type", this.type.getKey())
                .add("arg", this.arg)
                .toString();
    }

    public boolean test(Inventory inventory) {
        return this.type.getQueryOperator().test(this.arg, (AbstractInventory) inventory);
    }
}
