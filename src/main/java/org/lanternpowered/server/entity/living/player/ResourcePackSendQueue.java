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
package org.lanternpowered.server.entity.living.player;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSendResourcePack;
import org.spongepowered.api.event.entity.living.humanoid.player.ResourcePackStatusEvent;
import org.spongepowered.api.resourcepack.ResourcePack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class ResourcePackSendQueue {

    // The delay before we resend the resource pack packet,
    // resending until the players clicks yes or no, closing
    // the window isn't an option.
    private static final int RESEND_DELAY = 10;

    private final List<ResourcePack> queue = new ArrayList<>();
    @Nullable private ResourcePack waitingForResponse;
    private final LanternPlayer player;

    private int counter;

    ResourcePackSendQueue(LanternPlayer player) {
        this.player = player;
    }

    public void offer(ResourcePack resourcePack) {
        checkNotNull(resourcePack, "resourcePack");
        synchronized (this.queue) {
            if (this.waitingForResponse == null) {
                send(resourcePack);
                this.counter = 0;
            } else {
                this.queue.add(resourcePack);
            }
        }
    }

    public Optional<ResourcePack> poll(ResourcePackStatusEvent.ResourcePackStatus status) {
        synchronized (this.queue) {
            final ResourcePack resourcePack = this.waitingForResponse;
            // Just return the status, we will still expect a next
            // status message for this resource pack
            if (!status.wasSuccessful().isPresent()) {
                this.counter = -1;
                return Optional.ofNullable(resourcePack);
            }
            if (!this.queue.isEmpty()) {
                send(this.queue.remove(0));
                this.counter = 0;
            } else {
                this.waitingForResponse = null;
            }
            return Optional.ofNullable(resourcePack);
        }
    }

    void pulse() {
        synchronized (this.queue) {
            if (this.waitingForResponse == null || this.counter == -1) {
                return;
            }
            this.counter++;
            this.counter %= RESEND_DELAY;
            if (this.counter == 0) {
                send(this.waitingForResponse);
            }
        }
    }

    private void send(ResourcePack resourcePack) {
        this.waitingForResponse = resourcePack;
        final String hash = resourcePack.getHash().orElse(resourcePack.getId());
        final String location = resourcePack.getUri().toString();
        this.player.getConnection().send(new MessagePlayOutSendResourcePack(location, hash));
    }
}
