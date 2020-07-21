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
import org.lanternpowered.server.network.message.Packet;
import org.spongepowered.api.entity.Entity;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Supplier;

final class EmptyEntityUpdateContext implements EntityProtocolUpdateContext {

    static final EmptyEntityUpdateContext INSTANCE = new EmptyEntityUpdateContext();

    private EmptyEntityUpdateContext() {
    }

    @Override
    public Optional<LanternEntity> getById(int entityId) {
        return Optional.empty();
    }

    @Override
    public OptionalInt getId(Entity entity) {
        return OptionalInt.empty();
    }

    @Override
    public void sendToSelf(Packet packet) {
    }

    @Override
    public void sendToSelf(Supplier<Packet> messageSupplier) {
    }

    @Override
    public void sendToAll(Packet packet) {
    }

    @Override
    public void sendToAll(Supplier<Packet> message) {
    }

    @Override
    public void sendToAllExceptSelf(Packet packet) {
    }

    @Override
    public void sendToAllExceptSelf(Supplier<Packet> messageSupplier) {
    }
}
