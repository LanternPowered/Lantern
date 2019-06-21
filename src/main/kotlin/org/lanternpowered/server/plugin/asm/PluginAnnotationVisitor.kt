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

import org.objectweb.asm.Opcodes.ASM7

import org.lanternpowered.server.plugin.InvalidPluginException
import org.objectweb.asm.AnnotationVisitor
import org.spongepowered.plugin.meta.PluginMetadata

internal class PluginAnnotationVisitor(className: String) : WarningAnnotationVisitor(ASM7, className) {

    val metadata = PluginMetadata("unknown")

    private var state = State.DEFAULT
    private var hasId: Boolean = false

    private enum class State {
        DEFAULT, AUTHORS, DEPENDENCIES
    }

    override val annotation: String get() = "@Plugin"

    override fun visit(name: String?, value: Any) {
        if (this.state == State.AUTHORS) {
            if (value !is String) {
                throw InvalidPluginException("Plugin annotation has invalid element 'author'")
            }
            this.metadata.addAuthor(value)
            return
        }

        if (name == null) {
            throw InvalidPluginException("Plugin annotation attribute name is null")
        }

        if (this.state == State.DEPENDENCIES) {
            throw InvalidPluginException("Plugin annotation has invalid element 'dependencies'")
        }

        when (name) {
            "id" -> {
                if (value !is String) {
                    throw InvalidPluginException("Plugin annotation has invalid element 'id'")
                }
                this.hasId = true
                this.metadata.id = value
            }
            "name" -> {
                if (value !is String) {
                    throw InvalidPluginException("Plugin annotation has invalid element 'name'")
                }
                this.metadata.name = value
            }
            "version" -> {
                if (value !is String) {
                    throw InvalidPluginException("Plugin annotation has invalid element 'version'")
                }
                this.metadata.version = value
            }
            "description" -> {
                if (value !is String) {
                    throw InvalidPluginException("Plugin annotation has invalid element 'description'")
                }
                this.metadata.description = value
            }
            "url" -> {
                if (value !is String) {
                    throw InvalidPluginException("Plugin annotation has invalid element 'url'")
                }
                this.metadata.url = value
            }
            else -> super.visit(name, value)
        }
    }

    override fun visitAnnotation(name: String?, desc: String): AnnotationVisitor? {
        return if (this.state == State.DEPENDENCIES) {
            DependencyAnnotationVisitor(this.className, this.metadata)
        } else super.visitAnnotation(name, desc)
    }

    override fun visitArray(name: String?): AnnotationVisitor? {
        if (name == null) {
            throw InvalidPluginException("Plugin annotation has null element")
        }
        return when (name) {
            "authors" -> {
                this.state = State.AUTHORS
                this
            }
            "dependencies" -> {
                this.state = State.DEPENDENCIES
                this
            }
            else -> super.visitArray(name)
        }
    }

    override fun visitEnd() {
        if (this.state != State.DEFAULT) {
            this.state = State.DEFAULT
            return
        }

        if (!this.hasId) {
            throw InvalidPluginException("Plugin annotation is missing required element 'id'")
        }
    }
}
