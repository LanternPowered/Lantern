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

import org.lanternpowered.server.network.message.Message;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class MessagePlayOutSelectAdvancementTree implements Message {

    @Nullable private final String id;

    public MessagePlayOutSelectAdvancementTree(@Nullable String id) {
        this.id = id;
    }

    @Nullable
    public String getId() {
        return this.id;
    }
}
