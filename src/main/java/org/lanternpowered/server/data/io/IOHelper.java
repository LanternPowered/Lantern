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
package org.lanternpowered.server.data.io;

import org.lanternpowered.server.util.functions.ThrowableFunction;
import org.lanternpowered.server.util.functions.ThrowablePredicate;

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
