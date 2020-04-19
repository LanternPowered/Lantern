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

import com.google.common.collect.ImmutableList;
import org.spongepowered.api.item.inventory.InventoryTransformation;
import org.spongepowered.api.item.inventory.query.QueryOperation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LanternQueryTransformationBuilder implements InventoryTransformation.Builder {

    private final List<QueryOperation> operationList = new ArrayList<>();

    @Override
    public InventoryTransformation.Builder append(QueryOperation... operations) {
        Collections.addAll(this.operationList, operations);
        return this;
    }

    @Override
    public InventoryTransformation build() {
        return new LanternQueryTransformation(ImmutableList.copyOf(this.operationList));
    }

    @Override
    public InventoryTransformation.Builder from(InventoryTransformation value) {
        this.operationList.clear();
        if (value instanceof LanternQueryTransformation) {
            this.operationList.addAll(((LanternQueryTransformation) value).operations);
        }
        return this;
    }

    @Override
    public InventoryTransformation.Builder reset() {
        this.operationList.clear();
        return this;
    }
}
