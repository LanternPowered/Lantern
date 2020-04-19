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
package org.lanternpowered.server.plugin;

import com.google.inject.Injector;
import com.google.inject.Scopes;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KClass;
import org.lanternpowered.server.inject.plugin.PluginModule;
import org.spongepowered.plugin.meta.PluginMetadata;

import java.nio.file.Path;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

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
