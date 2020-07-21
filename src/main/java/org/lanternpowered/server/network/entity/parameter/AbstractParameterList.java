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
package org.lanternpowered.server.network.entity.parameter;

import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.packet.codec.CodecContext;

public abstract class AbstractParameterList implements ParameterList {

    /**
     * Writes the {@link ParameterList} to the {@link ByteBuffer}.
     *
     * @param buf The byte buffer
     */
    void write(CodecContext ctx, ByteBuffer buf) {
        buf.writeByte((byte) 0xff);
    }
}
