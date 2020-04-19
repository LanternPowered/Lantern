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
import org.spongepowered.api.data.persistence.DataView;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class MessagePlayOutDataResponse implements Message {

    private final int transactionId;
    @Nullable private final DataView data;

    public MessagePlayOutDataResponse(int transactionId, @Nullable DataView data) {
        this.transactionId = transactionId;
        this.data = data;
    }

    public int getTransactionId() {
        return this.transactionId;
    }

    @Nullable
    public DataView getData() {
        return this.data;
    }
}
