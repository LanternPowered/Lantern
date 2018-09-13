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

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.V1_8;

import org.lanternpowered.lmbda.LambdaFactory;
import org.lanternpowered.lmbda.MethodHandlesX;
import org.lanternpowered.server.inventory.AbstractInventory;
import org.lanternpowered.server.inventory.ICarriedInventory;
import org.lanternpowered.server.inventory.IViewableInventory;
import org.lanternpowered.server.util.BytecodeUtils;
import org.lanternpowered.server.util.UncheckedThrowables;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.item.inventory.type.ViewableInventory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public final class InventoryConstructorFactory {

    public static InventoryConstructorFactory get() {
        return instance;
    }

    private final static InventoryConstructorFactory instance = new InventoryConstructorFactory();

    private final static String CARRIED_INVENTORY_NAME = Type.getInternalName(ICarriedInventory.class);
    private final static String CARRIER_NAME = Type.getInternalName(Carrier.class);
    private final static String VIEWABLE_INVENTORY_NAME = Type.getInternalName(IViewableInventory.class);

    private final Map<Class<?>, InventoryConstructor<?>> inventoryConstructors = new HashMap<>();
    private final MethodHandles.Lookup lookup = MethodHandles.lookup();

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
            final boolean isCarried = CarriedInventory.class.isAssignableFrom(inventoryType);
            final boolean isViewable = ViewableInventory.class.isAssignableFrom(inventoryType);
            if (Modifier.isAbstract(inventoryType.getModifiers()) ||
                    Modifier.isInterface(inventoryType.getModifiers())) {
                throw new IllegalStateException("The inventory type '" + inventoryType.getName() + "' may not be abstract.");
            }
            if (Modifier.isFinal(inventoryType.getModifiers()) && !(isCarried || isViewable)) {
                throw new IllegalStateException("The inventory type '" + inventoryType.getName() + "' may not be final.");
            }
            final MethodHandles.Lookup lookup = UncheckedThrowables.doUnchecked(() -> MethodHandlesX.privateLookupIn(inventoryType, this.lookup));
            final MethodHandle constructorHandle;
            try {
                constructorHandle = lookup.findConstructor(inventoryType, MethodType.methodType(void.class));
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException("The inventory type '" + inventoryType.getName() + "' must have a empty constructor.");
            } catch (IllegalAccessException e) {
                throw UncheckedThrowables.throwUnchecked(e);
            }

            final Supplier<T> supplier = LambdaFactory.createSupplier(constructorHandle);
            final List<Class<T>> classes = new ArrayList<>();
            Supplier<T> carriedSupplier = supplier;
            Supplier<T> carriedViewableSupplier = supplier;
            Supplier<T> viewableSupplier = supplier;
            if (!isCarried) {
                final Class<T> carriedType = (Class<T>) generateCarriedClass(lookup, inventoryType);
                classes.add(carriedType);
                try {
                    carriedSupplier = LambdaFactory.createSupplier(lookup.findConstructor(carriedType, MethodType.methodType(void.class)));
                } catch (NoSuchMethodException | IllegalAccessException e) {
                    throw UncheckedThrowables.throwUnchecked(e);
                }
                carriedViewableSupplier = carriedSupplier;
                if (!isViewable) {
                    final Class<T> carriedViewableType = (Class<T>) generateViewableClass(lookup, carriedType);
                    classes.add(carriedViewableType);
                    try {
                        carriedViewableSupplier = LambdaFactory.createSupplier(lookup.findConstructor(
                                carriedViewableType, MethodType.methodType(void.class)));
                    } catch (NoSuchMethodException | IllegalAccessException e) {
                        throw UncheckedThrowables.throwUnchecked(e);
                    }
                }
            } else if (!isViewable) {
                final Class<T> viewableType = (Class<T>) generateViewableClass(lookup, inventoryType);
                classes.add(viewableType);
                try {
                    viewableSupplier = LambdaFactory.createSupplier(lookup.findConstructor(
                            viewableType, MethodType.methodType(void.class)));
                } catch (NoSuchMethodException | IllegalAccessException e) {
                    throw UncheckedThrowables.throwUnchecked(e);
                }
            }
            final Supplier<T> carriedSupplier0 = carriedSupplier;
            final Supplier<T> carriedViewableSupplier0 = carriedViewableSupplier;
            final Supplier<T> viewableSupplier0 = viewableSupplier;
            return new InventoryConstructor(inventoryType, classes, flags -> {
                if ((flags & InventoryConstructor.CARRIED) != 0) {
                    if ((flags & InventoryConstructor.VIEWABLE) != 0) {
                        return carriedViewableSupplier0.get();
                    }
                    return carriedSupplier0.get();
                }
                if ((flags & InventoryConstructor.VIEWABLE) != 0) {
                    return viewableSupplier0.get();
                }
                return supplier.get();
            });
        });
    }

    // Will generate a viewable class like the following:
    // public class LanternGridInventory$$Viewable extends LanternGridInventory implements IViewableInventory {}

    /**
     * Gets the generated class for the target class that
     * also implements {@link CarriedInventory}.
     *
     * @param inventoryType The inventory type
     * @return The carried inventory type
     */
    private Class<? extends AbstractInventory> generateViewableClass(MethodHandles.Lookup lookup, Class<? extends AbstractInventory> inventoryType) {
        final String name = inventoryType.getName() + "$$Viewable";
        final String className = name.replace('.', '/');

        // Generate the class bytecode and load it
        final ClassWriter cw = new ClassWriter(0);

        final String superName = Type.getInternalName(inventoryType);
        cw.visit(V1_8, ACC_PUBLIC | ACC_SUPER, className, null, superName, new String[] { VIEWABLE_INVENTORY_NAME });

        // Add a empty constructor
        BytecodeUtils.visitEmptyConstructor(cw, inventoryType);

        cw.visitEnd();
        return (Class<? extends AbstractInventory>) UncheckedThrowables.doUnchecked(() -> MethodHandlesX.defineClass(lookup, cw.toByteArray()));
    }

    // Will generate a carried class like the following:
    // public class LanternGridInventory$$Carried extends LanternGridInventory implements ICarriedInventory<Carrier> {}

    /**
     * Gets the generated class for the target class that
     * also implements {@link CarriedInventory}.
     *
     * @param inventoryType The inventory type
     * @return The carried inventory type
     */
    private Class<? extends AbstractInventory> generateCarriedClass(MethodHandles.Lookup lookup, Class<? extends AbstractInventory> inventoryType) {
        final String name = inventoryType.getName() + "$$Carried";
        final String className = name.replace('.', '/');

        // Generate the class bytecode and load it
        final ClassWriter cw = new ClassWriter(0);

        final String superName = Type.getInternalName(inventoryType);
        cw.visit(V1_8, ACC_PUBLIC | ACC_SUPER, className,
                String.format("L%s;L%s<L%s;>;", superName, CARRIED_INVENTORY_NAME, CARRIER_NAME), superName, new String[] { CARRIED_INVENTORY_NAME });

        // Add a empty constructor
        BytecodeUtils.visitEmptyConstructor(cw, inventoryType);

        cw.visitEnd();
        return (Class<? extends AbstractInventory>) UncheckedThrowables.doUnchecked(() -> MethodHandlesX.defineClass(lookup, cw.toByteArray()));
    }
}
