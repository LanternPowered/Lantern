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

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@NonnullByDefault
public final class ClassTransformers {

    final static List<ClassTransformer> transformers = new CopyOnWriteArrayList<>();
    final static Set<Exclusion> loaderExclusions = Collections.newSetFromMap(new ConcurrentHashMap<>());
    final static Set<Exclusion> transformerExclusions = Collections.newSetFromMap(new ConcurrentHashMap<>());

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
        addTransformerExclusion(new Exclusion.Package("org.apache"));
        addTransformerExclusion(new Exclusion.Package("org.aopalliance"));
        addTransformerExclusion(new Exclusion.Package("io.netty"));
        addTransformerExclusion(new Exclusion.Package("gnu.trove"));
        addLoaderExclusion(new Exclusion.Class("com.google.common.io.ByteStreams", true));
        addLoaderExclusion(new Exclusion.Package("org.apache.logging.log4j"));
        addLoaderExclusion(new Exclusion.Package("org.fusesource"));
        addLoaderExclusion(new Exclusion.Package("org.lanternpowered.launch"));
        addLoaderExclusion(new Exclusion.Package("jline"));
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
        transformerExclusions.add(new Exclusion.Class(classTransformer.getClass().getName(), true));
    }

    /**
     * Adds a new transformer exclusion.
     * 
     * @param exclusion the exclusion
     */
    public static void addLoaderExclusion(Exclusion exclusion) {
        loaderExclusions.add(exclusion);
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
