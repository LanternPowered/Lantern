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
package org.lanternpowered.server.data.io

import org.lanternpowered.server.util.function.ThrowableFunction
import org.lanternpowered.server.util.function.ThrowablePredicate
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.Optional

object IOHelper {

    @JvmStatic
    @Throws(IOException::class)
    fun write(targetFile: Path, fileWritePredicate: ThrowablePredicate<Path, IOException>): Boolean {
        val newFile = targetFile.parent.resolve(targetFile.fileName.toString() + "_new")
        val oldFile = targetFile.parent.resolve(targetFile.fileName.toString() + "_old")
        if (fileWritePredicate.test(newFile)) {
            if (Files.exists(oldFile)) {
                Files.delete(oldFile)
            }
            if (Files.exists(targetFile)) {
                Files.move(targetFile, oldFile)
            }
            Files.move(newFile, targetFile)
            return true
        }
        return false
    }

    @JvmStatic
    @Throws(IOException::class)
    fun <T> read(targetFile: Path, fileReadPredicate: ThrowableFunction<Path, T, IOException>): Optional<T> {
        if (Files.exists(targetFile)) {
            return Optional.of(fileReadPredicate.apply(targetFile))
        }
        val oldFile = targetFile.parent.resolve(targetFile.fileName.toString() + "_old")
        return if (Files.exists(oldFile)) {
            Optional.of(fileReadPredicate.apply(oldFile))
        } else Optional.empty()
    }
}
