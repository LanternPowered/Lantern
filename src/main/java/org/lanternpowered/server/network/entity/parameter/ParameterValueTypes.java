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
package org.lanternpowered.server.network.entity.parameter;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecUtils;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Direction;

import java.util.Optional;
import java.util.UUID;

public final class ParameterValueTypes {

    public static final ParameterValueType<Byte> BYTE = new ParameterValueType<>(ByteBuffer::writeByte);
    public static final ParameterValueType<Integer> INTEGER = new ParameterValueType<>(ByteBuffer::writeVarInt);
    public static final ParameterValueType<Float> FLOAT = new ParameterValueType<>(ByteBuffer::writeFloat);
    public static final ParameterValueType<String> STRING = new ParameterValueType<>(ByteBuffer::writeString);
    public static final ParameterValueType<Text> TEXT = new ParameterValueType<>(
            (ctx, buf, value) -> ctx.write(buf, ContextualValueTypes.TEXT, value));
    public static final ParameterValueType<ItemStack> ITEM_STACK = new ParameterValueType<>(
            (ctx, buf, value) -> ctx.write(buf, ContextualValueTypes.ITEM_STACK, value));
    public static final ParameterValueType<Boolean> BOOLEAN = new ParameterValueType<>(ByteBuffer::writeBoolean);
    public static final ParameterValueType<Vector3f> VECTOR_3F = new ParameterValueType<>(ByteBuffer::writeVector3f);
    public static final ParameterValueType<Vector3i> VECTOR_3I = new ParameterValueType<>(ByteBuffer::writeVector3i);
    public static final ParameterValueType<Optional<Vector3i>> OPTIONAL_VECTOR_3I = new ParameterValueType<>((buf, value) -> {
        buf.writeBoolean(value.isPresent());
        value.ifPresent(buf::writeVector3i);
    });
    public static final ParameterValueType<Direction> DIRECTION = new ParameterValueType<>(
            (buf, value) -> buf.writeVarInt(CodecUtils.encodeDirection(value)));
    public static final ParameterValueType<Optional<UUID>> OPTIONAL_UUID = new ParameterValueType<>((buf, value) -> {
        buf.writeBoolean(value.isPresent());
        value.ifPresent(buf::writeUniqueId);
    });
    public static final ParameterValueType<Optional<BlockState>> OPTIONAL_BLOCK_STATE = new ParameterValueType<>(
            (buf, value) -> buf.writeVarInt(value.map(v -> BlockRegistryModule.get().getStateInternalId(v)).orElse((short) 0)));
    public static final ParameterValueType<Optional<DataView>> NBT_TAG = new ParameterValueType<>(
            (buf, value) -> buf.writeDataView(value.orElse(null)));
}
