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
package org.lanternpowered.server.plugin;

import com.google.inject.Injector;
import com.google.inject.Scopes;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KClass;
import org.lanternpowered.server.inject.plugin.PluginModule;
import org.spongepowered.plugin.meta.PluginMetadata;

import java.nio.file.Path;
import java.util.Optional;

import javax.annotation.Nullable;

@SuppressWarnings("ALL")
public final class LanternPluginContainer extends InfoPluginContainer {

    private final Injector injector;
    private final Object instance;

    @Nullable private final Path source;

    LanternPluginContainer(String id, Injector injector, Class<?> pluginClass, PluginMetadata pluginMetadata, @Nullable Path source) {
        super(id, pluginMetadata);
        this.source = source;

        final KClass<?> kClass = JvmClassMappingKt.getKotlinClass(pluginClass);
        // Check for object classes, in this case is the object already instantiated
        final Object objectInstance = kClass.getObjectInstance();
        if (objectInstance != null) {
            this.instance = objectInstance;
            this.injector = injector.createChildInjector(new PluginModule(this) {
                @Override
                protected void configure() {
                    super.configure();
                    bind((Class) pluginClass).toInstance(objectInstance);
                }
            });
            // Just inject the members
            this.injector.injectMembers(objectInstance);
        } else {
            this.injector = injector.createChildInjector(new PluginModule(this) {
                @Override
                protected void configure() {
                    super.configure();
                    bind(pluginClass).in(Scopes.SINGLETON);
                }
            });
            this.instance = this.injector.getInstance(pluginClass);
        }
    }

    @Override
    public Optional<Path> getSource() {
        return Optional.ofNullable(this.source);
    }

    @Override
    public Optional<?> getInstance() {
        return Optional.of(this.instance);
    }

    public Injector getInjector() {
        return this.injector;
    }
}
