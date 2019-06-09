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
                val exception = RuntimeException("There was error attempting to update some data for the content updater:"
                        + updater.javaClass.name + "\nThe original data is being returned, possibly causing"
                        + "issues later on, \nbut the original data should not be lost. Please notify the developer"
                        + "of this exception with the stacktrace.", e)
                exception.printStackTrace()
                return copied
            }
        }
        return updated
    }
}
