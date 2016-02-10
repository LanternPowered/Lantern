/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
package org.lanternpowered.launch;

import org.spongepowered.api.util.annotation.NonnullByDefault;

import javax.annotation.Nullable;

@NonnullByDefault
public interface Exclusion {

    /**
     * Gets whether this exclusion applicable is for the class name.
     * 
     * @param className the class name
     * @return is applicable
     */
    boolean isApplicableFor(String className);

    /**
     * A exclusion that will exclude a package.
     */
    final class Package implements Exclusion {

        private final String name;

        /**
         * Creates a new package exclusion.
         * 
         * @param name the package name
         */
        public Package(String name) {
            this.name = name.isEmpty() ? name : name + '.';
        }

        @Override
        public boolean isApplicableFor(String className) {
            return className.startsWith(this.name);
        }
    }

    /**
     * A exclusion that will exclude a class.
     */
    final class Class implements Exclusion {

        private final String name;
        @Nullable private final String excludeInnerPref;

        /**
         * Creates a new class exclusion.
         * 
         * @param name the class name (path)
         * @param excludeInnerClasses whether the inner classes should also be excluded
         */
        public Class(String name, boolean excludeInnerClasses) {
            this.excludeInnerPref = excludeInnerClasses ? name + "$" : null;
            this.name = name;
        }

        @Override
        public boolean isApplicableFor(String className) {
            return this.name.equals(className) || (this.excludeInnerPref != null &&
                    className.startsWith(this.excludeInnerPref));
        }
    }

}
