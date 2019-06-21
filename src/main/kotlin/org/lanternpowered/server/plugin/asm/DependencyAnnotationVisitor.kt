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
package org.lanternpowered.server.plugin.asm

import org.lanternpowered.server.plugin.InvalidPluginException
import org.objectweb.asm.Opcodes.ASM5
import org.spongepowered.plugin.meta.PluginDependency
import org.spongepowered.plugin.meta.PluginMetadata

internal class DependencyAnnotationVisitor(className: String, private val metadata: PluginMetadata) :
        WarningAnnotationVisitor(ASM5, className) {

    private var id: String? = null
    private var version: String? = null
    private var optional: Boolean = false

    override val annotation: String get() = "@Dependency"

    override fun visit(name: String?, value: Any) {
        if (name == null) {
            throw InvalidPluginException("Dependency annotation has null element")
        }
        when (name) {
            "id" -> {
                if (value !is String) {
                    throw InvalidPluginException("Dependency annotation has invalid element 'id'")
                }
                this.id = value
                return
            }
            "version" -> {
                if (value !is String) {
                    throw InvalidPluginException("Dependency annotation has invalid element 'version'")
                }
                this.version = value
                return
            }
            "optional" -> {
                if (value !is Boolean) {
                    throw InvalidPluginException("Dependency annotation has invalid element 'optional'")
                }
                this.optional = value
                return
            }
            else -> super.visit(name, value)
        }
    }

    override fun visitEnd() {
        val id = this.id ?: throw InvalidPluginException("Dependency plugin ID is required")
        this.metadata.addDependency(PluginDependency(PluginDependency.LoadOrder.BEFORE, id, this.version, this.optional))
    }
}
