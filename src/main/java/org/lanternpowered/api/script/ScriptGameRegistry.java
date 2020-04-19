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
package org.lanternpowered.api.script;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.asset.Asset;

public interface ScriptGameRegistry {

    /**
     * Registers a new object of the specified type
     * and built from the asset.
     * <p>
     * The id will be generated based on the asset
     * file name and the plugin that owns the asset.
     *
     * @param asset The asset
     * @param objectType The object type
     * @param <T> The object type that will be built
     * @return The object
     */
    <T extends CatalogType> T register(Asset asset, Class<T> objectType);

    <T extends CatalogType> T register(Asset asset, String id, Class<T> objectType);

    <T extends CatalogType> T register(Object plugin, String asset, Class<T> objectType);

    <T extends CatalogType> T register(Object plugin, String asset, String id, Class<T> objectType);

    <T> Script<T> compile(Asset asset, Class<T> function);

    <T> Script<T> compile(String scriptSource, Class<T> function);

    <T> Script<T> compile(Object plugin, String asset, Class<T> function);

    Script<Object> compile(Asset asset);

    Script<Object> compile(String scriptSource);

    Script<Object> compile(Object plugin, String asset);
}
