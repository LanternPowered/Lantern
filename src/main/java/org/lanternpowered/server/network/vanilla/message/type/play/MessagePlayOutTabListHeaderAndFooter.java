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
import org.spongepowered.api.text.Text;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class MessagePlayOutTabListHeaderAndFooter implements Message {

    @Nullable private final Text header;
    @Nullable private final Text footer;

    public MessagePlayOutTabListHeaderAndFooter(@Nullable Text header, @Nullable Text footer) {
        this.header = header;
        this.footer = footer;
    }

    @Nullable
    public Text getHeader() {
        return this.header;
    }

    @Nullable
    public Text getFooter() {
        return this.footer;
    }
}
