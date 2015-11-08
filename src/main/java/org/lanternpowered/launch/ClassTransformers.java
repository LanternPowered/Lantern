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
package org.lanternpowered.launch;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public final class ClassTransformers {

    final static List<ClassTransformer> transformers = Lists.newCopyOnWriteArrayList();
    final static Set<Exclusion> loaderExclusions = Sets.newConcurrentHashSet();
    final static Set<Exclusion> transformerExclusions = Sets.newConcurrentHashSet();

    /**
     * Initializes the transformers.
     */
    static void init() {
        addTransformerExclusion(new Exclusion.Package("ninja.leaping.configurate"));
        addTransformerExclusion(new Exclusion.Package("com.google"));
        addTransformerExclusion(new Exclusion.Package("com.typesafe.config"));
        addTransformerExclusion(new Exclusion.Package("com.flowpowered.noise"));
        addTransformerExclusion(new Exclusion.Package("com.flowpowered.math"));
        addTransformerExclusion(new Exclusion.Package("com.zaxxer.hikari"));
        addTransformerExclusion(new Exclusion.Package("org.yaml.snakeyaml"));
        addTransformerExclusion(new Exclusion.Package("org.sqlite"));
        addTransformerExclusion(new Exclusion.Package("org.mariadb.jdbc"));
        addTransformerExclusion(new Exclusion.Package("org.objectweb.asm"));
        addTransformerExclusion(new Exclusion.Package("org.fusesource"));
        addTransformerExclusion(new Exclusion.Package("org.apache"));
        addTransformerExclusion(new Exclusion.Package("org.aopalliance"));
        addTransformerExclusion(new Exclusion.Package("io.netty"));
        addTransformerExclusion(new Exclusion.Package("gnu.trove"));
        addLoaderExclusion(new Exclusion.Package("org.lanternpowered.launch"));
        addLoaderExclusion(new Exclusion.Package("java"));
        addLoaderExclusion(new Exclusion.Package("sun"));
    }

    /**
     * Adds a new class transformer.
     * 
     * @param classTransformer the class transformer
     */
    public static void addTransformer(ClassTransformer classTransformer) {
        transformers.add(classTransformer);
        // All the transformer classes should be excluded
        transformerExclusions.add(new Exclusion.Class(classTransformer.getClass().getName()));
    }

    /**
     * Adds a new transformer exclusion.
     * 
     * @param exclusion the exclusion
     */
    public static void addLoaderExclusion(Exclusion exclusion) {
        transformerExclusions.add(exclusion);
    }

    /**
     * Adds a new transformer exclusion.
     * 
     * @param exclusion the exclusion
     */
    public static void addTransformerExclusion(Exclusion exclusion) {
        transformerExclusions.add(exclusion);
    }

    private ClassTransformers() {
    }
}
