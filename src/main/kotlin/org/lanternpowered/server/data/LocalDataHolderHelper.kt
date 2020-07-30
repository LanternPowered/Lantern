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
package org.lanternpowered.server.data

import org.lanternpowered.api.data.Key
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.api.util.uncheckedCast
import org.spongepowered.api.data.value.Value

object LocalDataHolderHelper {

    /**
     * Matches the contents of the two [LocalDataHolder]s.
     *
     * @param dataHolderA The first data holder
     * @param dataHolderB The second data holder
     * @return Whether the contents match
     */
    @JvmStatic
    fun matchContents(dataHolderA: LocalDataHolder, dataHolderB: LocalDataHolder): Boolean {
        val keyRegistryA = dataHolderA.keyRegistry
        val keyRegistryB = dataHolderB.keyRegistry

        // The same keys have to be present in both of the containers
        if (keyRegistryA.keys.size != keyRegistryB.keys.size)
            return false

        for (registrationA in keyRegistryA.registrations) {
            val registrationB = keyRegistryB[registrationA.key.uncheckedCast<Key<Value<Any>>>()] ?: return false

            // Get the values from both of the containers and match them
            val valueA = registrationA.anyDataProvider().get(dataHolderA).orNull()
            val valueB = registrationB.anyDataProvider().get(dataHolderB).orNull()
            if (valueA != valueB)
                return false
        }

        return true
    }
}
