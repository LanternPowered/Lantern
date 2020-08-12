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
package org.lanternpowered.server.entity.player

import org.lanternpowered.api.data.persistence.DataContainer
import org.lanternpowered.api.data.persistence.DataView
import org.lanternpowered.server.data.io.store.ObjectStoreRegistry

object PlayerSerializer {

    // TODO: Handle statistics and advancements
    
    fun load(player: AbstractPlayer, data: DataView) {
        val store = ObjectStoreRegistry.get().get(AbstractPlayer::class.java).get()
        store.deserialize(player, data)
    }

    fun save(player: AbstractPlayer): DataContainer {
        val data = DataContainer.createNew()
        val store = ObjectStoreRegistry.get().get(AbstractPlayer::class.java).get()
        store.deserialize(player, data)
        return data
    }
}
