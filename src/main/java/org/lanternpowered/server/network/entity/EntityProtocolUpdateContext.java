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
package org.lanternpowered.server.network.entity;

import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.network.message.Message;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Supplier;

public interface EntityProtocolUpdateContext {

    static EntityProtocolUpdateContext empty() {
        return EmptyEntityUpdateContext.INSTANCE;
    }

    /**
     * Gets the {@link LanternEntity} that is assigned to
     * the entity id if present.
     *
     * @param entityId The entity id
     * @return The entity id present
     */
    Optional<LanternEntity> getById(int entityId);

    /**
     * Gets the entity id that is assigned to the {@link Entity}.
     *
     * @param entity The entity
     * @return The entity id
     */
    OptionalInt getId(Entity entity);

    /**
     * Sends the {@link Message} to the owner, will only do something
     * if the owner is a {@link Player}.
     *
     * @param message The message
     */
    void sendToSelf(Message message);

    /**
     * Sends the {@link Message} to the owner, will only do something
     * if the owner is a {@link Player}.
     *
     * @param messageSupplier The message supplier
     */
    void sendToSelf(Supplier<Message> messageSupplier);

    /**
     * Sends the {@link Message} to all the trackers.
     *
     * @param message The message
     */
    void sendToAll(Message message);

    /**
     * Sends the {@link Message} to all the trackers.
     *
     * @param message The message
     */
    void sendToAll(Supplier<Message> message);

    /**
     * Sends the {@link Message} to all the trackers except the owner.
     *
     * @param message The message
     */
    void sendToAllExceptSelf(Message message);

    /**
     * Sends the {@link Message} to all the trackers except the owner.
     *
     * @param messageSupplier The message supplier
     */
    void sendToAllExceptSelf(Supplier<Message> messageSupplier);
}
