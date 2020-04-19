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

import jdk.internal.org.objectweb.asm.Type
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes.ASM7
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.plugin.meta.PluginMetadata

class PluginClassVisitor : ClassVisitor(ASM7) {

    lateinit var className: String
        private set

    private var annotationVisitor: PluginAnnotationVisitor? = null

    val metadata: PluginMetadata?
        get() = this.annotationVisitor?.metadata

    override fun visit(version: Int, access: Int, name: String, signature: String, superName: String, interfaces: Array<String>) {
        this.className = name
    }

    override fun visitAnnotation(desc: String, visible: Boolean): AnnotationVisitor? {
        return if (visible && desc == pluginDescriptor) {
            PluginAnnotationVisitor(this.className).also { this.annotationVisitor = it }
        } else null
    }

    companion object {

        private val pluginDescriptor = Type.getDescriptor(Plugin::class.java)
    }
}
