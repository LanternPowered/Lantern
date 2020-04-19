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
package org.lanternpowered.server.config.user;

import org.spongepowered.api.profile.GameProfile;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface UserStorage<T> {

    /**
     * Gets a collection with all the entries.
     * 
     * @return the entries
     */
    Collection<T> getEntries();

    /**
     * Gets the user entry for the specified unique id.
     * 
     * @param uniqueId the unique id
     * @return the entry if present, otherwise {@link Optional#empty()}
     */
    Optional<T> getEntryByUUID(UUID uniqueId);

    /**
     * Gets the user entry for the specified username.
     * 
     * @param username the username
     * @return the entry if present, otherwise {@link Optional#empty()}
     */
    Optional<T> getEntryByName(String username);

    /**
     * Gets the user entry for the specified game profile.
     * 
     * @param gameProfile the game profile
     * @return the entry if present, otherwise {@link Optional#empty()}
     */
    Optional<T> getEntryByProfile(GameProfile gameProfile);

    /**
     * Adds the op entry and replaces any present ones.
     * 
     * @param entry the entry
     */
    void addEntry(T entry);

    /**
     * Removes the op entry for the specified player unique id.
     * 
     * @param uniqueId the unique id
     * @return whether a entry was removed
     */
    boolean removeEntry(UUID uniqueId);

}
