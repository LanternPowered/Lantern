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
package org.lanternpowered.server.data.type

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.data.type.NotePitch

class LanternNotePitch(key: CatalogKey) : DefaultCatalogType(key), NotePitch {

    private lateinit var next: NotePitch

    override fun cycleNext(): NotePitch = this.next

    fun setNext(next: NotePitch) { this.next = next }
}
