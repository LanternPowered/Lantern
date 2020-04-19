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
        parameterList.add(EntityParameters.Item.ITEM, this.entity.get(Keys.ITEM_STACK_SNAPSHOT)
                .map(ItemStackSnapshot::createStack).orElseGet(() -> ItemStack.of(ItemTypes.APPLE, 1)));
    }

    @Override
    protected void update(ParameterList parameterList) {
        super.update(parameterList);
        final ItemStackSnapshot itemStackSnapshot = this.entity.get(Keys.ITEM_STACK_SNAPSHOT).orElse(null);
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
