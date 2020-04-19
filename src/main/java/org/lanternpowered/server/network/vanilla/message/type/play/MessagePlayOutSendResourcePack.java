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
package org.lanternpowered.server.network.vanilla.message.type.play;

import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

import org.lanternpowered.server.network.message.Message;

public final class MessagePlayOutSendResourcePack implements Message {

    private final String url;
    private final String hash;

    public MessagePlayOutSendResourcePack(String url, String hash) {
        this.hash = checkNotNullOrEmpty(hash, "hash");
        this.url = checkNotNullOrEmpty(url, "url");
    }

    public String getUrl() {
        return this.url;
    }

    public String getHash() {
        return this.hash;
    }

}
