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
package org.lanternpowered.server.network.entity.vanilla;

import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.network.entity.parameter.ParameterList;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.Objects;

public class ItemEntityProtocol<E extends LanternEntity> extends ObjectEntityProtocol<E> {

    private ItemStackSnapshot lastItemStackSnapshot;

    public ItemEntityProtocol(E entity) {
        super(entity);
    }

    @Override
    protected void spawn(ParameterList parameterList) {
        super.spawn(parameterList);
        parameterList.add(EntityParameters.Item.ITEM, this.entity.get(Keys.REPRESENTED_ITEM)
                .map(ItemStackSnapshot::createStack).orElseGet(() -> ItemStack.of(ItemTypes.APPLE, 1)));
    }

    @Override
    protected void update(ParameterList parameterList) {
        super.update(parameterList);
        final ItemStackSnapshot itemStackSnapshot = this.entity.get(Keys.REPRESENTED_ITEM).orElse(null);
        if (!Objects.equals(this.lastItemStackSnapshot, itemStackSnapshot)) {
            // Ignore the NoAI tag, isn't used on the client
            parameterList.add(EntityParameters.Item.ITEM, itemStackSnapshot.createStack());
            this.lastItemStackSnapshot = itemStackSnapshot;
        }
    }

    @Override
    protected String getObjectType() {
        return "minecraft:item";
    }

    @Override
    protected int getObjectData() {
        return 1;
    }
}
