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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * A helper class to provide access to {@link Field}s.
 */
@SuppressWarnings("unchecked")
public final class FieldAccess {

    /**
     * The modifiers field.
     */
    private static final Field modifiersField = loadModifiersField();

    /**
     * Makes the given {@link Field} accessible to allow getting
     * and setting values to any kind of field. Even if it's a
     * final field.
     *
     * @param field The field
     * @deprecated Reflective access to java packages is no longer supported by default in Java 9+,
     *             in a context that you have control over the complete application you could export
     *             these packages to avoid this limitation. Since there is no guarantee that this will
     *             work, this method is deprecated and will be removed in version {@code 2.0.0}.
     *             If you desire to modify final fields, you can always copy this class to your
     *             project. This is at your own risk and be aware of the possible problems.
     *             See https://stackoverflow.com/questions/41265266/how-to-solve-inaccessibleobjectexception-unable-to-make-member-accessible-m
     *             for more information.
     */
    @Deprecated
    public static void makeAccessible(Field field) {
        field.setAccessible(true);

        // Mark the field as non final, if it's final
        final int modifiers = field.getModifiers();
        if (Modifier.isFinal(modifiers)) {
            AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
                try {
                    modifiersField.setInt(field, modifiers & ~Modifier.FINAL);
                    return null;
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            });
        }
    }

    /**
     * Loads the {@code modifiers} field of a {@link Field}.
     *
     * @return The modifiers field
     */
    private static Field loadModifiersField() {
        return AccessController.doPrivileged((PrivilegedAction<Field>) () -> {
            try {
                final Field field = Field.class.getDeclaredField("modifiers");
                field.setAccessible(true);
                return field;
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
    }

    private FieldAccess() {
    }
}
