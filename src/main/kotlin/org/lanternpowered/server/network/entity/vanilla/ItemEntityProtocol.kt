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
package org.lanternpowered.server.network.entity.vanilla

import org.lanternpowered.api.data.Keys
import org.lanternpowered.api.item.inventory.ItemStackSnapshot
import org.lanternpowered.api.item.inventory.emptyItemStack
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.key.minecraftKey
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.server.network.entity.parameter.ParameterList

class ItemEntityProtocol<E : LanternEntity>(entity: E) : ObjectEntityProtocol<E>(entity) {

    companion object {
        private val TYPE = minecraftKey("item")
    }

    override val objectType: NamespacedKey get() = TYPE
    override val objectData: Int get() = 1

    private var lastItemSnapshot: ItemStackSnapshot? = null

    private val itemSnapshot: ItemStackSnapshot?
        get() = this.entity.get(Keys.ITEM_STACK_SNAPSHOT).orNull()

    override fun spawn(parameterList: ParameterList) {
        super.spawn(parameterList)
        parameterList.add(EntityParameters.Item.ITEM, this.itemSnapshot?.createStack() ?: emptyItemStack())
    }

    override fun update(parameterList: ParameterList) {
        super.update(parameterList)
        val itemSnapshot = this.itemSnapshot
        if (this.itemSnapshot != itemSnapshot) {
            // Ignore the NoAI tag, isn't used on the client
            parameterList.add(EntityParameters.Item.ITEM, itemSnapshot?.createStack() ?: emptyItemStack())
            this.lastItemSnapshot = itemSnapshot
        }
    }
}
