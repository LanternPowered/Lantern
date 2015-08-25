package org.lanternpowered.server.network.vanilla.message.type.play;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

import org.lanternpowered.server.network.message.Message;
import org.spongepowered.api.event.entity.player.PlayerResourcePackStatusEvent.ResourcePackStatus;

public final class MessagePlayInResourcePackStatus implements Message {

    private final String hash;
    private final ResourcePackStatus status;

    public MessagePlayInResourcePackStatus(String hash, ResourcePackStatus status) {
        this.status = checkNotNull(status, "status");
        this.hash = checkNotNullOrEmpty(hash, "hash");
    }

    public String getHash() {
        return this.hash;
    }

    public ResourcePackStatus getStatus() {
        return this.status;
    }
}
