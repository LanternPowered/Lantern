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
package org.lanternpowered.server.util

import java.nio.file.Files
import java.nio.file.Path

object SafeIO {

    fun <R> write(targetFile: Path, fn: (path: Path) -> R): R {
        val newFile = targetFile.parent.resolve(targetFile.fileName.toString() + "_new")
        val oldFile = targetFile.parent.resolve(targetFile.fileName.toString() + "_old")
        val result = fn(newFile)
        if (Files.exists(oldFile))
            Files.delete(oldFile)
        if (Files.exists(targetFile))
            Files.move(targetFile, oldFile)
        Files.move(newFile, targetFile)
        return result
    }

    fun <R> read(targetFile: Path, fn: (path: Path) -> R): R? {
        if (Files.exists(targetFile))
            return fn(targetFile)
        val oldFile = targetFile.parent.resolve(targetFile.fileName.toString() + "_old")
        return if (Files.exists(oldFile)) fn(oldFile) else null
    }
}
