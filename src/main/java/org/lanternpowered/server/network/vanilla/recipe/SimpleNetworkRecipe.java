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

public final class SimpleNetworkRecipe extends NetworkRecipe {

    public SimpleNetworkRecipe(String id, String type) {
        super(id, type);
    }

    @Override
    public void write(CodecContext ctx, ByteBuffer buf) {
    }
}
