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
