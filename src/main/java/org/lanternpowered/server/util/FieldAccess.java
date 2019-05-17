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
