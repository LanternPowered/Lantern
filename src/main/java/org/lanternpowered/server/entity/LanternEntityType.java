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
package org.lanternpowered.server.entity;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import org.lanternpowered.server.catalog.PluginCatalogType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.text.translation.Translation;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.function.Function;

public final class LanternEntityType extends PluginCatalogType.Base.Translatable implements EntityType {

    private static final Field BYPASS_FIELD;

    static {
        try {
            BYPASS_FIELD = LanternEntity.class.getDeclaredField("bypassEntityTypeLookup");
            BYPASS_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private final Class<? extends Entity> entityClass;
    private final Function<UUID, Entity> entityConstructor;

    public LanternEntityType(String pluginId, String name, String translation,
            Function<UUID, Entity> entityConstructor) {
        super(pluginId, name, translation);
        this.entityConstructor = checkNotNull(entityConstructor, "entityConstructor");
        this.entityClass = getEntityClass(entityConstructor);
    }

    public LanternEntityType(String pluginId, String name, Translation translation,
            Function<UUID, Entity> entityConstructor) {
        super(pluginId, name, translation);
        this.entityConstructor = checkNotNull(entityConstructor, "entityConstructor");
        this.entityClass = getEntityClass(entityConstructor);
    }

    public LanternEntityType(String pluginId, String id, String name, String translation,
            Function<UUID, Entity> entityConstructor) {
        super(pluginId, id, name, translation);
        this.entityConstructor = checkNotNull(entityConstructor, "entityConstructor");
        this.entityClass = getEntityClass(entityConstructor);
    }

    public LanternEntityType(String pluginId, String id, String name, Translation translation,
            Function<UUID, Entity> entityConstructor) {
        super(pluginId, id, name, translation);
        this.entityConstructor = checkNotNull(entityConstructor, "entityConstructor");
        this.entityClass = getEntityClass(entityConstructor);
    }

    public LanternEntityType(String pluginId, String id, String translation,
            Class<? extends Entity> entityClass) {
        super(pluginId, id, translation);
        this.entityConstructor = uuid -> { throw new UnsupportedOperationException("You cannot construct a " + id); };
        this.entityClass = entityClass;
    }

    public LanternEntityType(String pluginId, String id, Translation translation,
            Class<? extends Entity> entityClass) {
        super(pluginId, id, translation);
        this.entityConstructor = uuid -> { throw new UnsupportedOperationException("You cannot construct a " + id); };
        this.entityClass = entityClass;
    }

    public LanternEntityType(String pluginId, String id, String name, String translation,
            Class<? extends Entity> entityClass) {
        super(pluginId, id, name, translation);
        this.entityConstructor = uuid -> { throw new UnsupportedOperationException("You cannot construct a " + id); };
        this.entityClass = entityClass;
    }

    public LanternEntityType(String pluginId, String id, String name, Translation translation,
            Class<? extends Entity> entityClass) {
        super(pluginId, id, name, translation);
        this.entityConstructor = uuid -> { throw new UnsupportedOperationException("You cannot construct a " + id); };
        this.entityClass = entityClass;
    }

    private static Class<? extends Entity> getEntityClass(Function<UUID, ? extends Entity> entityConstructor) {
        try {
            BYPASS_FIELD.set(null, true);
            //noinspection unchecked
            final Class<? extends Entity> clazz = entityConstructor.apply(UUID.randomUUID()).getClass();
            BYPASS_FIELD.set(null, false);
            return clazz;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Function<UUID, Entity> getEntityConstructor() {
        return this.entityConstructor;
    }

    @Override
    public Class<? extends Entity> getEntityClass() {
        return this.entityClass;
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper().add("entityClass", this.entityClass);
    }
}