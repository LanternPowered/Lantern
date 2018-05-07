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
package org.lanternpowered.server.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.PUTSTATIC;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_8;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.spongepowered.api.util.generator.GeneratorUtils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A factory to create getters and setters for {@link Field}s.
 */
@SuppressWarnings("unchecked")
public final class FieldAccessFactory {

    /**
     * A counter for all the generated getter classes.
     */
    private static final AtomicInteger getterCounter = new AtomicInteger();

    /**
     * A counter for all the generated setter classes.
     */
    private static final AtomicInteger setterCounter = new AtomicInteger();

    /**
     * The modifiers field.
     */
    private static final Field modifiersField = loadModifiersField();

    /**
     * The setter method type.
     */
    private static final MethodType setterMethodType = MethodType.methodType(void.class, Object.class, Object.class);

    /**
     * Makes the given {@link Field} accessible to allow getting
     * and setting values to any kind of field. Even if it's a
     * final field.
     *
     * @param field The field
     */
    public static void makeAccessible(Field field) {
        field.setAccessible(true);

        // Mark the field as non final, if it's final
        final int modifiers = field.getModifiers();
        if (Modifier.isFinal(modifiers)) {
            try {
                modifiersField.setInt(field, modifiers & ~Modifier.FINAL);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    /**
     * Creates a getter {@link Supplier} for the given static {@link Field}.
     *
     * @param field The field
     * @param <V> The field value type
     * @return The function
     */
    public static <V> Supplier<V> createStaticGetter(Field field) {
        checkState(Modifier.isStatic(field.getModifiers()), "Field must be static");
        final Function<?, V> function = createGetter(field);
        return () -> function.apply(null);
    }

    /**
     * Creates a getter {@link Function} for the given {@link Field}.
     *
     * @param field The field
     * @param <T> The target object type
     * @param <V> The field value type
     * @return The function
     */
    public static <T, V> Function<T, V> createGetter(Field field) {
        checkNotNull(field, "field");
        field.setAccessible(true);

        final ClassWriter cw = new ClassWriter(0);
        final String className = field.getName().replace('.', '/') + "$$LanternGetter$" + getterCounter.incrementAndGet();
        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, className,
                "Ljava/lang/Object;Ljava/util/function/Function<Ljava/lang/Object;Ljava/lang/Object;>;",
                "java/lang/Object", new String[] { "java/util/function/Function" });

        // Add a empty constructor
        BytecodeUtils.visitEmptyConstructor(cw);

        // Generate the apply method
        final MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "apply",
                "(Ljava/lang/Object;)Ljava/lang/Object;", null, null);
        mv.visitAnnotation("Ljava/lang/invoke/LambdaForm$Hidden;", true);
        mv.visitCode();
        final String descriptor = Type.getDescriptor(field.getType());
        final String targetName = Type.getInternalName(field.getDeclaringClass());
        if (Modifier.isStatic(field.getModifiers())) {
            // Get the static field from the class
            mv.visitFieldInsn(GETSTATIC, targetName, field.getName(), descriptor);
        } else {
            // Load the target parameter
            mv.visitVarInsn(ALOAD, 1);
            // Cast it
            mv.visitTypeInsn(CHECKCAST, targetName);
            // Get the field from it
            mv.visitFieldInsn(GETFIELD, targetName, field.getName(), descriptor);
        }
        // Box the values in case they are primitives
        GeneratorUtils.visitBoxingMethod(mv, Type.getType(field.getType()));
        // Return it
        mv.visitInsn(ARETURN);
        mv.visitMaxs(1, 2);
        mv.visitEnd();

        // Finish class generation
        cw.visitEnd();

        // Define the class and create a function instance
        final MethodHandles.Lookup lookup = MethodHandleMagic.trustedLookup().in(field.getDeclaringClass());
        final Class<?> functionClass = MethodHandleMagic.defineNestmateClass(lookup, cw.toByteArray());

        try {
            return (Function<T, V>) functionClass.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Something went wrong!", e);
        }
    }

    /**
     * Creates a setter {@link Consumer} for the given static {@link Field}.
     *
     * @param field The field
     * @param <V> The field value type
     * @return The consumer
     */
    public static <V> Consumer<V> createStaticSetter(Field field) {
        checkState(Modifier.isStatic(field.getModifiers()), "Field must be static");
        final BiConsumer<?, V> function = createSetter(field);
        return value -> function.accept(null, value);
    }

    /**
     * Creates a setter {@link BiConsumer} for the given {@link Field}.
     *
     * @param field The field
     * @param <T> The target object type
     * @param <V> The field value type
     * @return The bi consumer
     */
    public static <T, V> BiConsumer<T, V> createSetter(Field field) {
        checkNotNull(field, "field");
        field.setAccessible(true);

        boolean isFinal = Modifier.isFinal(field.getModifiers());
        // Better check is somebody changed the final modifier already
        if (!isFinal) {
            final Field[] fields = field.getDeclaringClass().getDeclaredFields();
            boolean isFound = false;
            for (Field field1 : fields) {
                // The same signature, now check if somebody tinkered with the field
                if (field.getName().equals(field1.getName()) &&
                        field.getType().equals(field1.getType())) {
                    isFinal = Modifier.isFinal(field1.getModifiers());
                    isFound = true;
                    break;
                }
            }
            if (!isFound) {
                throw new IllegalStateException("Something funky happened with: " + field.getName());
            }
        } else {
            try {
                modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }

        // Final fields don't allow direct access, so MethodHandles will do the trick.
        if (isFinal) {
            try {
                final MethodHandle methodHandle = MethodHandleMagic.trustedLookup()
                        .in(field.getDeclaringClass()).unreflectSetter(field).asType(setterMethodType);
                return (a, b) -> {
                    try {
                        methodHandle.invokeExact(a, b);
                    } catch (Throwable throwable) {
                        throw new IllegalStateException(throwable);
                    }
                };
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }

        final ClassWriter cw = new ClassWriter(0);
        final String className = field.getName().replace('.', '/') + "$$LanternSetter$" + setterCounter.incrementAndGet();
        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, className,
                "Ljava/lang/Object;Ljava/util/function/BiConsumer<Ljava/lang/Object;Ljava/lang/Object;>;",
                "java/lang/Object", new String[] { "java/util/function/BiConsumer" });

        // Add a empty constructor
        BytecodeUtils.visitEmptyConstructor(cw);
        // Generate the apply method
        final MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "accept",
                "(Ljava/lang/Object;Ljava/lang/Object;)V", null, null);
        mv.visitAnnotation("Ljava/lang/invoke/LambdaForm$Hidden;", true);
        mv.visitCode();
        final String descriptor = Type.getDescriptor(field.getType());
        final String targetName = Type.getInternalName(field.getDeclaringClass());
        final boolean isStatic = Modifier.isStatic(field.getModifiers());
        if (!isStatic) {
            // Load the target parameter
            mv.visitVarInsn(ALOAD, 1);
            // Cast it
            mv.visitTypeInsn(CHECKCAST, targetName);
        }
        // Load the value parameter
        mv.visitVarInsn(ALOAD, 2);
        // Unbox the values in case they are primitives, otherwise cast
        GeneratorUtils.visitUnboxingMethod(mv, Type.getType(field.getType()));
        // Put the value into the field
        if (isStatic) {
            mv.visitFieldInsn(PUTSTATIC, targetName, field.getName(), descriptor);
        } else {
            mv.visitFieldInsn(PUTFIELD, targetName, field.getName(), descriptor);
        }
        // Return
        mv.visitInsn(RETURN);
        mv.visitMaxs(2, 3);
        mv.visitEnd();

        // Finish class generation
        cw.visitEnd();

        // Define the class and create a function instance
        final MethodHandles.Lookup lookup = MethodHandleMagic.trustedLookup().in(field.getDeclaringClass());
        final Class<?> functionClass = MethodHandleMagic.defineNestmateClass(lookup, cw.toByteArray());

        try {
            return (BiConsumer<T, V>) functionClass.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Something went wrong!", e);
        }
    }

    /**
     * Loads the {@code modifiers} field of a {@link Field}.
     *
     * @return The modifiers field
     */
    private static Field loadModifiersField() {
        try {
            final Field mField = Field.class.getDeclaredField("modifiers");
            mField.setAccessible(true);
            return mField;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private FieldAccessFactory() {
    }
}
