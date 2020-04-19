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

import org.checkerframework.checker.nullness.qual.Nullable;

abstract class GroupedNetworkRecipe extends NetworkRecipe {

    @Nullable private final String group;

    GroupedNetworkRecipe(String id, String type, @Nullable String group) {
        super(id, type);
        this.group = group;
    }

    @Override
    void write(CodecContext ctx, ByteBuffer buf) {
        buf.writeString(this.group == null ? "" : this.group);
    }
}
