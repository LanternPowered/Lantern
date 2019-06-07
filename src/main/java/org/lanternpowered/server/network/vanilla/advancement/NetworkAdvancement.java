/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.network.vanilla.advancement;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Iterables;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.codec.CodecContext;

import java.util.Collection;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class NetworkAdvancement {

    private final String id;
    @Nullable private final String parentId;
    @Nullable private final NetworkAdvancementDisplay display;
    private final Collection<String> criteria;
    private final String[][] requirements;

    public NetworkAdvancement(String id, @Nullable String parentId, @Nullable NetworkAdvancementDisplay display,
            Collection<String> criteria, String[][] requirements) {
        this.requirements = requirements;
        this.parentId = parentId;
        this.criteria = criteria;
        this.display = display;
        this.id = id;
    }

    void write(CodecContext ctx, ByteBuffer buf) {
        buf.writeString(this.id);
        buf.writeBoolean(this.parentId != null);
        if (this.parentId != null) {
            buf.writeString(this.parentId);
        }
        buf.writeBoolean(this.display != null);
        if (this.display != null) {
            this.display.write(ctx, buf);
        }
        buf.writeVarInt(this.criteria.size());
        this.criteria.forEach(buf::writeString);
        buf.writeVarInt(this.requirements.length);
        for (String[] requirements : this.requirements) {
            buf.writeVarInt(requirements.length);
            for (String requirement : requirements) {
                buf.writeString(requirement);
            }
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("id", this.id)
                .add("parentId", this.parentId)
                .add("display", this.display)
                .add("criteria", Iterables.toString(this.criteria))
                .toString();
    }
}
