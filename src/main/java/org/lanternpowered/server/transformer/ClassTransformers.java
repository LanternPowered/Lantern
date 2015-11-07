/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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
package org.lanternpowered.server.transformer;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public final class ClassTransformers {

    final static List<ClassTransformer> transformers = Lists.newCopyOnWriteArrayList();
    final static Set<Exclusion> exclusions = Sets.newConcurrentHashSet();

    /**
     * Initializes the transformers.
     */
    static void init() {
        addExclusion(new Exclusion.Package("ninja.leaping.configurate"));
        addExclusion(new Exclusion.Package("com.google"));
        addExclusion(new Exclusion.Package("com.typesafe.config"));
        addExclusion(new Exclusion.Package("com.flowpowered.noise"));
        addExclusion(new Exclusion.Package("com.flowpowered.math"));
        addExclusion(new Exclusion.Package("com.zaxxer.hikari"));
        addExclusion(new Exclusion.Package("org.yaml.snakeyaml"));
        addExclusion(new Exclusion.Package("org.sqlite"));
        addExclusion(new Exclusion.Package("org.mariadb.jdbc"));
        addExclusion(new Exclusion.Package("org.objectweb.asm"));
        addExclusion(new Exclusion.Package("org.fusesource"));
        addExclusion(new Exclusion.Package("org.apache"));
        addExclusion(new Exclusion.Package("org.aopalliance"));
        addExclusion(new Exclusion.Package("io.netty"));
        addExclusion(new Exclusion.Package("gnu.trove"));
        addExclusion(new Exclusion.Package("javax"));
        addExclusion(new Exclusion.Package("jline"));
    }

    /**
     * Adds a new class transformer.
     * 
     * @param classTransformer the class transformer
     */
    public static void addTransformer(ClassTransformer classTransformer) {
        transformers.add(classTransformer);
    }

    /**
     * Adds a new exclusion that is parsed from the provided string.
     * 
     * @param exclusion the exclusion
     */
    public static void addExclusion(String exclusion) {
        if (exclusion.endsWith(".")) {
            addExclusion(new Exclusion.Package(exclusion.substring(0, exclusion.length() - 1)));
        } else {
            addExclusion(new Exclusion.Class(exclusion));
        }
    }

    /**
     * Adds a new exclusion.
     * 
     * @param exclusion the exclusion
     */
    public static void addExclusion(Exclusion exclusion) {
        exclusions.add(exclusion);
    }

    private ClassTransformers() {
    }
}
