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
package org.lanternpowered.server.transformer.data;

import org.lanternpowered.launch.transformer.ClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public final class FastValueContainerClassTransformer implements ClassTransformer {

    @Override
    public byte[] transform(ClassLoader classLoader, String className, byte[] byteCode) {
        // We don't want to get stuck in a loop, just ignore everything in the data package,
        // we can assume that everything in that package is optimized already or should be.
        if (className.startsWith("org.lanternpowered.server.data.") ||
                className.startsWith("org.spongepowered.api.data.")) {
            return byteCode;
        }
        final ClassReader classReader = new ClassReader(byteCode);
        final ClassWriter classWriter = new ClassWriter(Opcodes.ASM5);
        final FastValueContainerClassVisitor classVisitor = new FastValueContainerClassVisitor(classWriter);
        classReader.accept(classVisitor, 0);
        return classWriter.toByteArray();
    }
}
