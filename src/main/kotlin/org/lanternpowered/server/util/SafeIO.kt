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
import java.nio.file.StandardCopyOption

object SafeIO {

    fun <R> write(targetPath: Path, fn: (tmpPath: Path) -> R): R {
        val tmpFile = targetPath.parent.resolve(targetPath.fileName.toString() + ".tmp")
        Files.deleteIfExists(tmpFile)
        val result = fn(tmpFile)
        Files.move(tmpFile, targetPath, StandardCopyOption.REPLACE_EXISTING)
        return result
    }
}
