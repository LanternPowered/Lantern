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
