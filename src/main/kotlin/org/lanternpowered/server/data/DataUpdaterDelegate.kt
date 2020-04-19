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

import com.google.common.collect.ImmutableList
import org.spongepowered.api.data.persistence.DataContentUpdater
import org.spongepowered.api.data.persistence.DataView

internal class DataUpdaterDelegate(
        private val updaters: ImmutableList<DataContentUpdater>,
        private val from: Int,
        private val to: Int
) : DataContentUpdater {

    override fun getInputVersion() = this.from
    override fun getOutputVersion() = this.to

    override fun update(content: DataView): DataView {
        val copied = content.copy() // backup
        var updated: DataView = copied
        for (updater in this.updaters) {
            try {
                updated = updater.update(updated)
            } catch (e: Exception) {
                val exception = RuntimeException("There was error attempting to update some data for the content updater: "
                        + "${updater.javaClass.name}\nThe original data is being returned, possibly causing "
                        + "issues later on, \nbut the original data should not be lost. Please notify the developer "
                        + "of this exception with the stacktrace.", e)
                exception.printStackTrace()
                return copied
            }
        }
        return updated
    }
}
