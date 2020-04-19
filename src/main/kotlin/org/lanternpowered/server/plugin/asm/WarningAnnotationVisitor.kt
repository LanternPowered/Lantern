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

import org.lanternpowered.server.game.Lantern
import org.objectweb.asm.AnnotationVisitor

internal abstract class WarningAnnotationVisitor(api: Int, val className: String) : AnnotationVisitor(api) {

    internal abstract val annotation: String

    override fun visit(name: String?, value: Any) {
        logger.warn("Found unknown $annotation annotation element in $className: $name = $value")
    }

    override fun visitEnum(name: String?, desc: String, value: String) {
        logger.warn("Found unknown $annotation annotation element in $className: $name ($desc) = $value")
    }

    override fun visitAnnotation(name: String?, desc: String): AnnotationVisitor? {
        logger.warn("Found unknown $annotation annotation element in $className: $name ($desc)")
        return null
    }

    override fun visitArray(name: String?): AnnotationVisitor? {
        logger.warn("Found unknown $annotation annotation element in $className: $name")
        return null
    }

    companion object {

        private val logger = Lantern.getLogger()
    }
}
