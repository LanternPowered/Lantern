/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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

import static org.lanternpowered.launch.ClassTransformers.loaderExclusions;
import static org.lanternpowered.launch.ClassTransformers.transformerExclusions;
import static org.lanternpowered.launch.ClassTransformers.transformers;

import com.google.common.io.ByteStreams;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class LaunchClassLoader extends URLClassLoader {

    private final Map<String, Class<?>> cachedClasses = new ConcurrentHashMap<>();
    private final Set<String> invalidClasses = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final ClassLoader parent = this.getClass().getClassLoader();

    LaunchClassLoader(URL[] urls) {
        super(urls, null);
    }

    @Override
    public Class<?> findClass(final String name) throws ClassNotFoundException {
        if (this.invalidClasses.contains(name)) {
            throw new ClassNotFoundException(name);
        }

        // Skip the classes that are excluded
        for (Exclusion exclusion : loaderExclusions) {
            if (exclusion.isApplicableFor(name)) {
                return this.parent.loadClass(name);
            }
        }

        // Use the cached class if possible, avoid transforming the
        // classes multiple times
        if (this.cachedClasses.containsKey(name)) {
            return this.cachedClasses.get(name);
        }

        // Skip the classes that are excluded
        for (Exclusion exclusion : transformerExclusions) {
            if (exclusion.isApplicableFor(name)) {
                try {
                    Class<?> clazz = super.findClass(name);
                    this.cachedClasses.put(name, clazz);
                    return clazz;
                } catch (ClassNotFoundException e) {
                    this.invalidClasses.add(name);
                    throw e;
                }
            }
        }

        // Search for the class file
        String fileName = name.replace('.', '/').concat(".class");
        URL resource = this.findResource(fileName);

        // The class file could not be found
        if (resource == null) {
            this.invalidClasses.add(name);
            throw new ClassNotFoundException(name);
        }

        try {
            // Get the package name
            int lastDot = name.lastIndexOf('.');
            String packageName = lastDot == -1 ? "" : name.substring(0, lastDot);

            // Make sure that there is a package defined
            Package pkg = this.getPackage(packageName);
            if (pkg == null) {
                this.definePackage(packageName, null, null, null, null, null, null, null);
            }

            InputStream is = resource.openStream();
            byte[] bytes = new byte[is.available()];
            ByteStreams.readFully(is, bytes);
            is.close();

            for (ClassTransformer transformer : transformers) {
                try {
                    bytes = transformer.transform(this, name, bytes);
                } catch (Exception e) {
                    System.err.println("An error occurred while transforming " + name + ": " + e);
                }
            }

            // Define the new class and cache it
            Class<?> clazz = this.defineClass(name, bytes, 0, bytes.length);
            this.cachedClasses.put(name, clazz);
            return clazz;
        } catch (Throwable e) {
            // An error occurred while reading the class
            this.invalidClasses.add(name);
            throw new ClassNotFoundException(name, e);
        }
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }
}