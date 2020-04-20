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
package org.lanternpowered.server.registry.type.data

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.server.data.type.LanternNotePitch
import org.lanternpowered.server.registry.internalCatalogTypeRegistry
import org.spongepowered.api.data.type.NotePitch

val NotePitchRegistry = internalCatalogTypeRegistry<NotePitch> {
    val sortedNotePitches = arrayOf(
            "F_SHARP0",
            "G0",
            "G_SHARP0",
            "A1",
            "A_SHARP1",
            "B1",
            "C1",
            "C_SHARP1",
            "D1",
            "D_SHARP1",
            "E1",
            "F1",
            "F_SHARP1",
            "G1",
            "G_SHARP1",
            "A2",
            "A_SHARP2",
            "B2",
            "C2",
            "C_SHARP2",
            "D2",
            "D_SHARP2",
            "E2",
            "F2",
            "F_SHARP2"
    )
    val entries = ArrayList<LanternNotePitch>(sortedNotePitches.size)
    for (noteName in sortedNotePitches) {
        val notePitch = LanternNotePitch(CatalogKey.minecraft(noteName.toLowerCase()))
        register(entries.size, notePitch)
        entries.add(notePitch)
    }
    for (i in entries.indices) {
        entries[i].setNext(entries[(i + 1) % entries.size])
    }
}