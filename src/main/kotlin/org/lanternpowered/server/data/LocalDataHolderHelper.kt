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
        if (keyRegistryA.keys.size != keyRegistryB.keys.size) {
            return false
        }

        for (registrationA in keyRegistryA.registrations) {
            val registrationB = keyRegistryB[registrationA.key.uncheckedCast<Key<Value<Any>>>()] ?: return false

            // Get the values from both of the containers and match them
            val valueA = registrationA.anyDataProvider().get(dataHolderA).orNull()
            val valueB = registrationB.anyDataProvider().get(dataHolderB).orNull()
            if (valueA != valueB) {
                return false
            }
        }

        return true
    }
}
