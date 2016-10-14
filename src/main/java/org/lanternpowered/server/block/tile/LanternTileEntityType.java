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
package org.lanternpowered.server.block.tile;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import org.lanternpowered.server.catalog.PluginCatalogType;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.TileEntityType;

import java.lang.reflect.Field;
import java.util.function.Supplier;

public final class LanternTileEntityType extends PluginCatalogType.Base implements TileEntityType {

    private static final Field BYPASS_FIELD;

    static {
        try {
            BYPASS_FIELD = LanternTileEntity.class.getDeclaredField("bypassEntityTypeLookup");
            BYPASS_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends TileEntity> LanternTileEntityType of(String pluginId, String name,
            Class<T> tileEntityClass, Supplier<T> tileEntitySupplier) {
        //noinspection unchecked
        return new LanternTileEntityType(pluginId, name, tileEntityClass, (Supplier) tileEntitySupplier);
    }

    public static <T extends TileEntity> LanternTileEntityType of(String pluginId, String id, String name,
            Class<T> tileEntityClass, Supplier<T> tileEntitySupplier) {
        //noinspection unchecked
        return new LanternTileEntityType(pluginId, id, name, tileEntityClass, (Supplier) tileEntitySupplier);
    }

    public static <T extends TileEntity> LanternTileEntityType of(String pluginId, String name,
            Supplier<T> tileEntitySupplier) {
        //noinspection unchecked
        return new LanternTileEntityType(pluginId, name, getClass(tileEntitySupplier), (Supplier) tileEntitySupplier);
    }

    public static <T extends TileEntity> LanternTileEntityType of(String pluginId, String id, String name,
            Supplier<T> tileEntitySupplier) {
        //noinspection unchecked
        return new LanternTileEntityType(pluginId, id, name, getClass(tileEntitySupplier), (Supplier) tileEntitySupplier);
    }

    private static Class<? extends TileEntity> getClass(Supplier<? extends TileEntity> tileEntitySupplier) {
        try {
            BYPASS_FIELD.set(null, true);
            final Class<? extends TileEntity> tileEntityClass = tileEntitySupplier.get().getClass();
            BYPASS_FIELD.set(null, false);
            //noinspection unchecked
            return tileEntityClass;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private final Class<? extends TileEntity> tileEntityClass;
    private final Supplier<TileEntity> tileEntityConstructor;

    private LanternTileEntityType(String pluginId, String name, Class<? extends TileEntity> tileEntityClass,
            Supplier<TileEntity> tileEntityConstructor) {
        super(pluginId, name);
        this.tileEntityClass = checkNotNull(tileEntityClass, "tileEntityClass");
        this.tileEntityConstructor = tileEntityConstructor;
    }

    private LanternTileEntityType(String pluginId, String id, String name, Class<? extends TileEntity> tileEntityClass,
            Supplier<TileEntity> tileEntityConstructor) {
        super(pluginId, id, name);
        this.tileEntityClass = checkNotNull(tileEntityClass, "tileEntityClass");
        this.tileEntityConstructor = tileEntityConstructor;
    }

    @Override
    public Class<? extends TileEntity> getTileEntityType() {
        return this.tileEntityClass;
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper().add("tileEntityClass", this.tileEntityClass);
    }

    public Supplier<TileEntity> getTileEntityConstructor() {
        return this.tileEntityConstructor;
    }
}
