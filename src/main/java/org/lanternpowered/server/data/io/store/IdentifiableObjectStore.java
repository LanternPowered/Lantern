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
package org.lanternpowered.server.data.io.store;

import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.util.Identifiable;

import java.util.UUID;

public interface IdentifiableObjectStore<T extends Identifiable> extends ObjectStore<T> {

    /**
     * Deserializes the {@link UUID} that will be used to the {@link Identifiable}.
     *
     * @param dataView The data view
     * @return The unique id
     */
    UUID deserializeUniqueId(DataView dataView);

    /**
     * Serializes the {@link UUID} for the {@link Identifiable}.
     *
     * @param dataView The data view
     * @param uniqueId The unique id
     */
    void serializeUniqueId(DataView dataView, UUID uniqueId);
}
