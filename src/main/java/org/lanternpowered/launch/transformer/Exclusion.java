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
package org.lanternpowered.launch.transformer;

import static java.util.Objects.requireNonNull;

@FunctionalInterface
public interface Exclusion {

    /**
     * Creates a {@link Exclusion} that will exclude a package.
     *
     * @param packageName The package name
     * @return The exclusion
     */
    static Exclusion forPackage(String packageName) {
        requireNonNull(packageName, "packageName");
        final String pref = packageName.endsWith(".") || packageName.isEmpty() ? packageName : packageName + ".";
        return name -> name.startsWith(pref);
    }

    /**
     * Creates a {@link Exclusion} that will exclude a class.
     *
     * @param className The class name
     * @return The exclusion
     */
    static Exclusion forClass(String className) {
        return forClass(className, true);
    }

    /**
     * Creates a {@link Exclusion} that will exclude a class.
     *
     * @param className The class name
     * @param excludeInnerClasses Whether inner classes should also be excluded
     * @return The exclusion
     */
    static Exclusion forClass(String className, boolean excludeInnerClasses) {
        requireNonNull(className, "className");
        final String pref = excludeInnerClasses ? className + "$" : null;
        return name -> name.equals(className) || (excludeInnerClasses && className.startsWith(pref));
    }

    /**
     * Gets whether this exclusion is applicable to the specified class name.
     *
     * @param className The class name
     * @return Is applicable
     */
    boolean isApplicable(String className);
}
