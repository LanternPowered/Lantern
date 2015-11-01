/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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
package org.lanternpowered.server.catalog;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.spongepowered.api.CatalogType;

public interface CatalogTypeRegistry<T extends CatalogType> {

    /**
     * Registers a new catalog type in the registry.
     * 
     * @param catalogType the catalog type
     */
    void register(T catalogType);

    /**
     * Gets whether the registry the catalog type contains.
     * 
     * @param catalogType the catalog type
     * @return is present
     */
    boolean has(T catalogType);

    /**
     * Gets whether the registry a catalog type contains
     * with the specified id.
     * 
     * @param identifier the identifier
     * @return is present
     */
    boolean has(String identifier);

    /**
     * Gets a catalog type by using the specified identifier.
     * 
     * @param identifier the identifier
     * @return the catalog type if present, otherwise {@link Optional#absent()}.
     */
    Optional<T> get(String identifier);

    /**
     * Gets a catalog type by using the specified identifier and the found
     * catalog type must also match the specified type.
     * 
     * @param identifier the identifier
     * @return the catalog type if present and matching the type, otherwise {@link Optional#absent()}.
     */
    <V extends T> Optional<V> getOf(String identifier, Class<V> type);

    /**
     * Gets all the catalog types that are registered.
     * 
     * @return the catalog types
     */
    Set<T> getAll();

    /**
     * Gets all the catalog types that are registered and that are matching the
     * specified type.
     * 
     * @param type the type
     * @return the catalog types
     */
    <V extends T> Set<V> getAllOf(Class<V> type);

    /**
     * Gets the delegate map of the registry, this shouldn't be touched. This is only
     * used for the game registry.
     * 
     * @return the delegate map
     */
    Map<String, T> getDelegateMap();
}
