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
package org.lanternpowered.server.data.io;

import org.lanternpowered.server.util.function.ThrowableFunction;
import org.lanternpowered.server.util.function.ThrowablePredicate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class IOHelper {

    public static boolean write(Path targetFile, ThrowablePredicate<Path, IOException> fileWritePredicate) throws IOException {
        Path newFile = targetFile.getParent().resolve(targetFile.getFileName() + "_new");
        Path oldFile = targetFile.getParent().resolve(targetFile.getFileName() + "_old");
        if (fileWritePredicate.test(newFile)) {
            if (Files.exists(oldFile)) {
                Files.delete(oldFile);
            }
            if (Files.exists(targetFile)) {
                Files.move(targetFile, oldFile);
            }
            Files.move(newFile, targetFile);
            return true;
        }
        return false;
    }

    public static <T> Optional<T> read(Path targetFile, ThrowableFunction<Path, T, IOException> fileReadPredicate) throws IOException {
        if (Files.exists(targetFile)) {
            return Optional.of(fileReadPredicate.apply(targetFile));
        }
        Path oldFile = targetFile.getParent().resolve(targetFile.getFileName() + "_old");
        if (Files.exists(oldFile)) {
            return Optional.of(fileReadPredicate.apply(oldFile));
        }
        return Optional.empty();
    }
}
