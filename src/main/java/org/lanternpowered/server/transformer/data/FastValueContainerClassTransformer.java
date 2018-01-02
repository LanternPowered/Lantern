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
import org.lanternpowered.server.data.FastCompositeValueStoreHelper;
import org.lanternpowered.server.data.ICompositeValueStore;
import org.lanternpowered.server.entity.LanternEntity;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.value.mutable.CompositeValueStore;
import org.spongepowered.api.entity.Entity;

/**
 * This {@link ClassTransformer} attempts to replace method calls inside {@link CompositeValueStore}s
 * that return {@link DataTransactionResult}. A lot of times are these {@link DataTransactionResult}s
 * discarded and this make it pointless to generate these. Replacing these methods with fast equivalents
 * in these cases that avoid {@link DataTransactionResult} object creations. Fast methods will always
 * return a {@code boolean} value that represents if the action was successful.
 * <p>All the fast methods are present in the {@link ICompositeValueStore}, every {@link CompositeValueStore}
 * should use this expansion interface to support fast methods. If this isn't the case it will fall back
 * to the original methods.
 * <p>Some examples:
 * <p>This is a simple example where you offer health to a entity. The offer method returns
 * a {@link DataTransactionResult} but is never used.
 * <pre>
 * {@code
 * final Entity entity = ...;
 * entity.offer(Keys.HEALTH, 2.0);
 * }
 * </pre>
 * The code above will be replaced by:
 * <pre>
 * {@code
 * final Entity entity = ...;
 * CompositeValueStoreHelper.offer(entity, Keys.HEALTH, 2.0);
 * }
 * </pre>
 * A helper class ({@link FastCompositeValueStoreHelper}) is here used to check if the {@link Entity} the
 * {@link ICompositeValueStore} value extends and then calls the fast method, otherwise it will fall
 * back to the original method. The helper class makes it easy to transform the code, without adding
 * extra lines in it, only the method call has to be modified.
 * <p>
 * Another example, we know in this case that the {@link Entity} is a {@link ICompositeValueStore},
 * because it is first casted to {@link LanternEntity} which implements that interface.
 * <pre>
 * {@code
 * final Entity entity = ...;
 * final LanternEntity entity1 = (LanternEntity) entity;
 * entity1.offer(Keys.HEALTH, 2.0);
 * }
 * </pre>
 * This will be replaced by:
 * <pre>
 * {@code
 * final Entity entity = ...;
 * final LanternEntity entity1 = (LanternEntity) entity;
 * entity1.offerFast(Keys.HEALTH, 2.0);
 * }
 * </pre>
 * <p>
 * Then is there also a case where the {@link DataTransactionResult} is actually used, but only to
 * check if it was successful ({@link DataTransactionResult#isSuccessful()}). This can also be represented
 * by the {@code boolean} value that is returned by the fast methods. In this case will the following
 * transformation occur.
 * <pre>
 * {@code
 * final Entity entity = ...;
 * final LanternEntity entity1 = (LanternEntity) entity;
 * boolean success = entity1.offer(Keys.HEALTH, 2.0).isSuccessful();
 * }
 * </pre>
 * This will be replaced by:
 * <pre>
 * {@code
 * final Entity entity = ...;
 * final LanternEntity entity1 = (LanternEntity) entity;
 * boolean success = entity1.offerFast(Keys.HEALTH, 2.0);
 * }
 * </pre>
 */
@SuppressWarnings("deprecation")
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
