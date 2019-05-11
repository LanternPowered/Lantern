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
package org.lanternpowered.server.network.entity;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.catalog.DefaultCatalogType;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.api.util.ToStringHelper;
import org.spongepowered.api.CatalogKey;

import java.util.function.Function;

@SuppressWarnings("unchecked")
public final class LanternEntityProtocolType<E extends LanternEntity> extends DefaultCatalogType implements EntityProtocolType<E> {

    public static <E extends LanternEntity> EntityProtocolType<E> of(CatalogKey key,
            Class<E> entityType, Function<E, ? extends AbstractEntityProtocol<E>> entityProtocolSupplier) {
        checkNotNull(key, "key");
        checkNotNull(entityProtocolSupplier, "entityProtocolSupplier");
        return new LanternEntityProtocolType<>(key, entityType, entityProtocolSupplier);
    }

    public static <E extends LanternEntity> EntityProtocolType<E> of(CatalogKey key,
            Function<E, ? extends AbstractEntityProtocol<E>> entityProtocolSupplier) {
        return of(key, (Class<E>) LanternEntity.class, entityProtocolSupplier);
    }

    private final Class<E> entityType;
    private final Function<E, AbstractEntityProtocol<E>> entityProtocolSupplier;

    private LanternEntityProtocolType(CatalogKey key, Class<E> entityType,
            Function<E, ? extends AbstractEntityProtocol<E>> entityProtocolSupplier) {
        super(key);
        this.entityProtocolSupplier = (Function<E, AbstractEntityProtocol<E>>) entityProtocolSupplier;
        this.entityType = entityType;
    }

    @Override
    public Class<E> getEntityType() {
        return this.entityType;
    }

    @Override
    public Function<E, AbstractEntityProtocol<E>> getSupplier() {
        return this.entityProtocolSupplier;
    }

    @Override
    public ToStringHelper toStringHelper() {
        return super.toStringHelper().add("entityType", this.entityType);
    }
}
