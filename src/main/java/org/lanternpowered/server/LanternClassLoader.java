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
package org.lanternpowered.server;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link ClassLoader} that gives complete control over all the libraries used by
 * {@link LanternServer}. Mainly designed for Java 9+, the System ClassLoader no longer
 * extends {@link URLClassLoader}. This {@link ClassLoader} should be used instead.
 * <p>
 * All {@link Class#forName(String)} operations will be delegated through this
 * {@link ClassLoader}.
 */
public final class LanternClassLoader extends URLClassLoader {

    private static final LanternClassLoader classLoader;

    static {
        ClassLoader.registerAsParallelCapable();
        final List<URL> urls = new ArrayList<>();

        final String classPath = System.getProperty("java.class.path");
        final String[] libraries = classPath.split(";");
        for (String library : libraries) {
            if (!library.contains("java") && !library.contains("lib")) {
                try {
                    urls.add(Paths.get(library).toUri().toURL());
                } catch (MalformedURLException ignored) {
                    System.out.println("Invalid library found in the class path: " + library);
                }
            }
        }

        classLoader = new LanternClassLoader(urls.toArray(new URL[urls.size()]),
                ClassLoader.getSystemClassLoader());
        Thread.currentThread().setContextClassLoader(classLoader);
    }

    /**
     * Gets the {@link LanternClassLoader}.
     *
     * @return The class loader
     */
    public static LanternClassLoader get() {
        return classLoader;
    }

    private LanternClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    /**
     * The same as {@link Class#forName(String, boolean, ClassLoader)},
     * but called for this {@link ClassLoader}.
     *
     * @see Class#forName(String, boolean, ClassLoader)
     */
    public Class<?> forName(String name, boolean initialize) throws ClassNotFoundException {
        return Class.forName(name, initialize, this);
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // Make sure that no new class loaders are created
        final Class<?> thisClass = getClass();
        if (name.equals(thisClass.getName())) {
            return thisClass;
        }
        synchronized (getClassLoadingLock(name)) {
            // First, check if the class has already been loaded
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                if (name.startsWith("java.") ||
                        name.startsWith("javax.") ||
                        name.startsWith("sun.")) {
                    c = getParent().loadClass(name);
                } else {
                    try {
                        c = findClass(name);
                    } catch (ClassNotFoundException ignored) {
                    }
                    if (c == null) {
                        c = getParent().loadClass(name);
                    }
                }
            }
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }

    @Override
    public URL getResource(String name) {
        URL url = findResource(name);
        if (url == null) {
            url = getParent().getResource(name);
        }
        return url;
    }
}
