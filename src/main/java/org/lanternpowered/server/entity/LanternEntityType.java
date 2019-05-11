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
package org.lanternpowered.server.entity;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.text.translation.TranslationHelper.tr;
import static org.lanternpowered.server.util.UncheckedThrowables.doUnchecked;
import static org.lanternpowered.server.util.UncheckedThrowables.throwUnchecked;

import org.lanternpowered.server.catalog.DefaultCatalogType;
import org.lanternpowered.api.util.ToStringHelper;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.text.translation.Translation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.UUID;
import java.util.function.Function;

public final class LanternEntityType<E extends Entity> extends DefaultCatalogType implements EntityType<E> {

    public static <E extends LanternEntity> LanternEntityType of(CatalogKey key, String translation,
            Class<E> entityClass, Function<UUID, E> entityConstructor) {
        return new LanternEntityType<>(key, translation, entityClass, entityConstructor);
    }

    public static <E extends LanternEntity> LanternEntityType of(CatalogKey key, Translation translation,
            Class<E> entityClass, Function<UUID, E> entityConstructor) {
        return new LanternEntityType<>(key, translation, entityClass, entityConstructor);
    }

    public static <E extends LanternEntity> LanternEntityType of(CatalogKey key, String translation,
            Class<E> entityClass) {
        return new LanternEntityType<>(key, translation, entityClass, getEntityConstructor(entityClass));
    }

    public static <E extends LanternEntity> LanternEntityType of(CatalogKey key, Translation translation,
            Class<E> entityClass) {
        return new LanternEntityType<>(key, translation, entityClass, getEntityConstructor(entityClass));
    }

    public static <E extends LanternEntity> LanternEntityType of(CatalogKey key, String translation,
            Function<UUID, E> entityConstructor) {
        return new LanternEntityType<>(key, translation, getEntityClass(entityConstructor), entityConstructor);
    }

    public static <E extends LanternEntity> LanternEntityType of(CatalogKey key, Translation translation,
            Function<UUID, E> entityConstructor) {
        return new LanternEntityType<>(key, translation, getEntityClass(entityConstructor), entityConstructor);
    }

    private static <E extends LanternEntity> Function<UUID, E> getEntityConstructor(Class<E> entityClass) {
        checkNotNull(entityClass, "entityClass");
        final Constructor<E> constructor;
        try {
            constructor = entityClass.getDeclaredConstructor(UUID.class);
            constructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("The entity class is missing the constructor: "
                    + entityClass.getSimpleName() + "(UUID uniqueId)");
        }
        return uuid -> doUnchecked(() -> constructor.newInstance(uuid));
    }

    private static <E extends LanternEntity> Class<E> getEntityClass(Function<UUID, E> entityConstructor) {
        return doUnchecked(() -> {
            BYPASS_FIELD.set(null, true);
            //noinspection unchecked
            final Class<E> clazz = (Class<E>) entityConstructor.apply(UUID.randomUUID()).getClass();
            BYPASS_FIELD.set(null, false);
            return clazz;
        });
    }

    private static final Field BYPASS_FIELD;

    static {
        try {
            BYPASS_FIELD = LanternEntity.class.getDeclaredField("bypassEntityTypeLookup");
            BYPASS_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw throwUnchecked(e);
        }
    }

    private final Class<E> entityClass;
    private final Function<UUID, E> entityConstructor;
    private final Translation translation;

    private LanternEntityType(CatalogKey key, String translation,
            Class<E> entityClass, Function<UUID, E> entityConstructor) {
        super(key);
        this.translation = tr(translation);
        this.entityConstructor = checkNotNull(entityConstructor, "entityConstructor");
        this.entityClass = checkNotNull(entityClass, "entityClass");
    }

    private LanternEntityType(CatalogKey key, Translation translation,
            Class<E> entityClass, Function<UUID, E> entityConstructor) {
        super(key);
        this.translation = translation;
        this.entityConstructor = checkNotNull(entityConstructor, "entityConstructor");
        this.entityClass = checkNotNull(entityClass, "entityClass");
    }

    public Function<UUID, E> getEntityConstructor() {
        return this.entityConstructor;
    }

    @Override
    public Class<E> getEntityClass() {
        return this.entityClass;
    }

    @Override
    public ToStringHelper toStringHelper() {
        return super.toStringHelper().add("entityClass", this.entityClass);
    }

    @Override
    public Translation getTranslation() {
        return this.translation;
    }
}
