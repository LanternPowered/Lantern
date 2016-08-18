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
package org.lanternpowered.server.script;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.api.script.ObjectType;
import org.lanternpowered.server.game.registry.PluginCatalogRegistryModule;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

public abstract class AbstractObjectTypeRegistryModule<O, T extends ObjectType<O>> extends PluginCatalogRegistryModule<T> {

    private final Map<Class<?>, T> byClass = new HashMap<>();

    public AbstractObjectTypeRegistryModule(@Nullable Class<?> catalogClass) {
        super(catalogClass);
    }

    @Override
    protected void register(T catalogType, boolean disallowInbuiltPluginIds) {
        super.register(catalogType, disallowInbuiltPluginIds);
        this.byClass.put(catalogType.getType(), catalogType);
    }

    /**
     * Gets the {@link T} for the specified type class of the type {@link O}.
     *
     * @param clazz The value class
     * @return The object type
     */
    public Optional<T> getByClass(Class<? extends O> clazz) {
        return Optional.ofNullable(this.byClass.get(checkNotNull(clazz, "clazz")));
    }
}
