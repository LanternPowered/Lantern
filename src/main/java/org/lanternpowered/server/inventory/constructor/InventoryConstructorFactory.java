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
package org.lanternpowered.server.inventory.constructor;

import static org.objectweb.asm.Opcodes.ACC_BRIDGE;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PROTECTED;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ACC_SYNTHETIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_8;

import org.lanternpowered.server.inventory.AbstractInventory;
import org.lanternpowered.server.inventory.CarrierReference;
import org.lanternpowered.server.util.DefineableClassLoader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.type.CarriedInventory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public final class InventoryConstructorFactory {

    public static InventoryConstructorFactory get() {
        return instance;
    }

    private final static InventoryConstructorFactory instance = new InventoryConstructorFactory();

    private final static String CARRIER_REFERENCE_NAME = Type.getInternalName(CarrierReference.class);
    private final static String CARRIER_REFERENCE_DESC = Type.getDescriptor(CarrierReference.class);

    private final static String CARRIED_INVENTORY_NAME = Type.getInternalName(CarriedInventory.class);
    private final static String CARRIED_INVENTORY_DESC = Type.getDescriptor(CarriedInventory.class);

    private final static String INVENTORY_CONSTRUCTOR_NAME = Type.getInternalName(InventoryConstructor.class);
    private final static String INVENTORY_CONSTRUCTOR_DESC = Type.getDescriptor(InventoryConstructor.class);

    private final static String CARRIER_NAME = Type.getInternalName(Carrier.class);
    private final static String CARRIER_DESC = Type.getDescriptor(Carrier.class);

    private final static String CARRIER_REF_FIELD = "carrierReference";

    private final DefineableClassLoader classLoader = new DefineableClassLoader();
    private final Map<Class<?>, InventoryConstructor<?>> inventoryConstructors = new HashMap<>();

    /**
     * Gets the {@link InventoryConstructor} for the given
     * {@link AbstractInventory} type.
     *
     * @param inventoryType The inventory type
     * @param <T> The inventory type
     * @return The constructor
     */
    public <T extends AbstractInventory> InventoryConstructor<T> getConstructor(Class<T> inventoryType) {
        return (InventoryConstructor<T>) this.inventoryConstructors.computeIfAbsent(inventoryType, type -> {
            try {
                if (!Modifier.isPublic(inventoryType.getModifiers())) {
                    throw new IllegalStateException("The inventory type '" + inventoryType.getName() + "' must be public.");
                }
                if (Modifier.isAbstract(inventoryType.getModifiers()) ||
                        Modifier.isInterface(inventoryType.getModifiers())) {
                    throw new IllegalStateException("The inventory type '" + inventoryType.getName() + "' may not be abstract.");
                }
                if (Modifier.isFinal(inventoryType.getModifiers()) &&
                        !CarriedInventory.class.isAssignableFrom(inventoryType)) {
                    throw new IllegalStateException("The inventory type '" + inventoryType.getName() + "' may not be final.");
                }
                final Constructor<?> constructor = inventoryType.getDeclaredConstructor();
                if (!(Modifier.isPublic(constructor.getModifiers()) ||
                        Modifier.isProtected(constructor.getModifiers()))) {
                    throw new IllegalStateException("The constructor of the inventory type '" + inventoryType.getName() +
                            "' must be public or protected.");
                }
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException("The inventory type '" + inventoryType.getName() + "' must have a empty constructor.");
            }
            final Class<? extends InventoryConstructor<?>> constructorClass = generateConstructorClass(
                    inventoryType, CarriedInventory.class.isAssignableFrom(inventoryType) ? inventoryType :
                            generateCarriedClass(inventoryType));
            try {
                return constructorClass.newInstance();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
    }

    private Class<? extends InventoryConstructor<?>> generateConstructorClass(
            Class<? extends AbstractInventory> inventoryType, Class<? extends AbstractInventory> carriedInventoryType) {
        final String name = inventoryType.getName() + "$$Constructor";
        final String className = name.replace('.', '/');

        // Generate the class bytecode and load it
        final ClassWriter cw = new ClassWriter(0);

        final String superInventoryName = Type.getInternalName(inventoryType);
        final String superInventoryDesc = Type.getDescriptor(inventoryType);

        final String carriedInventoryName = Type.getInternalName(carriedInventoryType);
        final String carriedInventoryDesc = Type.getDescriptor(carriedInventoryType);

        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, className,
                String.format("L%s<L%s;>;", INVENTORY_CONSTRUCTOR_NAME, superInventoryName), INVENTORY_CONSTRUCTOR_NAME, null);

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitLdcInsn(Type.getType(superInventoryDesc));
        mv.visitLdcInsn(Type.getType(carriedInventoryDesc));
        mv.visitMethodInsn(INVOKESPECIAL, INVENTORY_CONSTRUCTOR_NAME, "<init>", "(Ljava/lang/Class;Ljava/lang/Class;)V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(3, 1);
        mv.visitEnd();

        mv = cw.visitMethod(ACC_PUBLIC, "construct", String.format("(Z)L%s;", superInventoryName), null, null);
        mv.visitCode();
        if (carriedInventoryType != inventoryType) {
            mv.visitVarInsn(ILOAD, 1);
            final Label jumpLabel = new Label();
            mv.visitJumpInsn(IFEQ, jumpLabel);
            mv.visitTypeInsn(NEW, carriedInventoryName);
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, carriedInventoryName, "<init>", "()V", false);
            mv.visitInsn(ARETURN);
            mv.visitLabel(jumpLabel);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        }
        mv.visitTypeInsn(NEW, superInventoryName);
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, superInventoryName, "<init>", "()V", false);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();

        mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "construct",
                String.format("(Z)L%s;", Type.getInternalName(AbstractInventory.class)), null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ILOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, className, "construct", String.format("(Z)L%s;", superInventoryName), false);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();

        cw.visitEnd();
        return this.classLoader.defineClass(name, cw.toByteArray());
    }

    /**
     * Gets the generated class for the target class that
     * also implements {@link CarriedInventory}.
     *
     * @param inventoryType The inventory type
     * @return The carried inventory type
     */
    private Class<? extends AbstractInventory> generateCarriedClass(Class<? extends AbstractInventory> inventoryType) {
        final String name = inventoryType.getName() + "$$Carried";
        final String className = name.replace('.', '/');

        // Generate the class bytecode and load it
        final ClassWriter cw = new ClassWriter(0);

        final String superName = Type.getInternalName(inventoryType);
        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, className,
                String.format("L%s;L%s<L%s;>;", superName, CARRIED_INVENTORY_NAME, CARRIER_NAME), superName, new String[] { CARRIED_INVENTORY_NAME });

        final FieldVisitor fv = cw.visitField(ACC_PRIVATE + ACC_FINAL, CARRIER_REF_FIELD,
                CARRIER_REFERENCE_DESC, String.format("L%s<L%s;>;", CARRIER_REFERENCE_NAME, CARRIER_NAME), null);
        fv.visitEnd();

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, superName, "<init>", "()V", false);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitLdcInsn(Type.getType(CARRIER_DESC));
        mv.visitMethodInsn(INVOKESTATIC, CARRIER_REFERENCE_NAME, "of",
                String.format("(Ljava/lang/Class;)L%s;", CARRIER_REFERENCE_NAME), true);
        mv.visitFieldInsn(PUTFIELD, className, CARRIER_REF_FIELD, CARRIER_REFERENCE_DESC);
        mv.visitInsn(RETURN);
        mv.visitMaxs(2, 1);
        mv.visitEnd();

        mv = cw.visitMethod(ACC_PROTECTED, "setCarrier", String.format("(L%s;)V", CARRIER_NAME), null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESPECIAL, superName, "setCarrier", String.format("(L%s;)V", CARRIER_NAME), false);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, className, CARRIER_REF_FIELD, CARRIER_REFERENCE_DESC);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEINTERFACE, CARRIER_REFERENCE_NAME, "set", String.format("(L%s;)V", CARRIER_NAME), true);
        mv.visitInsn(RETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();

        mv = cw.visitMethod(ACC_PUBLIC, "getCarrier", "()Ljava/util/Optional;",
                String.format("()Ljava/util/Optional<L%s;>;", CARRIER_NAME), null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, className, CARRIER_REF_FIELD, CARRIER_REFERENCE_DESC);
        mv.visitMethodInsn(INVOKEINTERFACE, CARRIER_REFERENCE_NAME, "get", "()Ljava/util/Optional;", true);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        cw.visitEnd();
        return this.classLoader.defineClass(name, cw.toByteArray());
    }
}
