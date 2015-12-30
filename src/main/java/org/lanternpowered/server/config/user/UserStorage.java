/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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
