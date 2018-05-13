/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
