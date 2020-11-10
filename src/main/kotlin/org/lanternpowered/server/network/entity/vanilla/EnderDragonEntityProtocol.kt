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
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.key.minecraftKey
import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.server.network.entity.EntityProtocolInitContext
import org.lanternpowered.server.network.entity.NetworkIdHolder
import org.lanternpowered.server.network.entity.parameter.ParameterList

class EnderDragonEntityProtocol<E : LanternEntity>(entity: E) : CreatureEntityProtocol<E>(entity) {

    companion object {
        private const val PART_HEAD = 0
        private const val PART_NECK = 1
        private const val PART_BODY = 2
        private const val PART_TAIL1 = 3
        private const val PART_TAIL2 = 4
        private const val PART_TAIL3 = 5
        private const val PART_WING1 = 6
        private const val PART_WING2 = 7
        private val TYPE = minecraftKey("ender_dragon")
    }

    override val mobType: NamespacedKey get() = TYPE

    private val partEntityIds = IntArray(8)

    override fun init(context: EntityProtocolInitContext) {
        check(this.entity !is NetworkIdHolder) { "EnderDragons cannot have a predefined network id." }
        // A ender dragon uses 9 entity ids
        val ids = IntArray(this.partEntityIds.size + 1)
        context.acquireSequence(ids)
        this.initRootId(ids[0])
        System.arraycopy(ids, 1, this.partEntityIds, 0, this.partEntityIds.size)
    }

    override fun remove(context: EntityProtocolInitContext) {
        super.remove(context)
        context.release(this.partEntityIds)
    }

    override fun spawn(parameterList: ParameterList) {
        super.spawn(parameterList)
        // TODO: Send phase
    }

    override fun update(parameterList: ParameterList) {
        super.update(parameterList)
        // TODO: Update phase
    }

    // Override the silent method, due the complexity of this
    // entity, it would be hard to play the sounds ourselves
    // The sounds have to be synced to the wing animations, etc.
    override val isSilent: Boolean
        get() = this.entity.get(Keys.IS_SILENT).orElse(false)
}
