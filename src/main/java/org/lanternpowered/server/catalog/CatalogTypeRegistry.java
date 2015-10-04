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
