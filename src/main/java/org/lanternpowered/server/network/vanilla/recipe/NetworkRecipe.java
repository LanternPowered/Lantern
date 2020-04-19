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
package org.lanternpowered.server.network.vanilla.recipe;

import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.codec.CodecContext;

public abstract class NetworkRecipe {

    private final String id;
    private final String type;

    NetworkRecipe(String id, String type) {
        this.type = type;
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public String getType() {
        return this.type;
    }

    abstract void write(CodecContext ctx, ByteBuffer buf);
}
