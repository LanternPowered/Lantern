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
import org.lanternpowered.server.network.entity.parameter.ParameterList
import org.lanternpowered.server.registry.type.data.DyeColorRegistry
import org.spongepowered.api.data.type.DyeColors

class SheepEntityProtocol<E : LanternEntity>(entity: E) : AnimalEntityProtocol<E>(entity) {

    companion object {
        private val TYPE = minecraftKey("sheep")
    }

    private var lastSheepFlags: Byte? = null

    override val mobType: NamespacedKey get() = TYPE

    private val sheepFlags: Byte
        get() {
            var flags = DyeColorRegistry.getId(this.entity.get(Keys.DYE_COLOR).orElseGet(DyeColors.WHITE))
            if (this.entity.get(Keys.IS_SHEARED).orElse(false))
                flags += 0x10
            return flags.toByte()
        }

    override fun spawn(parameterList: ParameterList) {
        super.spawn(parameterList)
        parameterList.add(EntityParameters.Sheep.FLAGS, this.sheepFlags)
    }

    override fun update(parameterList: ParameterList) {
        super.update(parameterList)
        val sheepFlags = this.sheepFlags
        if (sheepFlags != this.lastSheepFlags) {
            parameterList.add(EntityParameters.Sheep.FLAGS, sheepFlags)
            this.lastSheepFlags = sheepFlags
        }
    }
}
