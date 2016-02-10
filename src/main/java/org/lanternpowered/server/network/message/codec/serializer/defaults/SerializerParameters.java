/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.network.message.codec.serializer.defaults;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.game.registry.Registries;
import org.lanternpowered.server.network.message.codec.serializer.SerializerContext;
import org.lanternpowered.server.network.message.codec.serializer.Types;
import org.lanternpowered.server.network.message.codec.serializer.ValueSerializer;
import org.lanternpowered.server.network.objects.Parameter;
import org.lanternpowered.server.network.objects.ParameterType;
import org.lanternpowered.server.network.objects.ParameterTypes;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * This is already for 1.9, but we probably won't use it anyway before it's released.
 */
public final class SerializerParameters implements ValueSerializer<List<Parameter<?>>> {

    private final static int BYTE = 0;
    private final static int INTEGER = 1;
    private final static int FLOAT = 2;
    private final static int STRING = 3;
    private final static int TEXT = 4;
    private final static int ITEM_STACK = 5;
    private final static int BOOLEAN = 6;
    private final static int VECTOR_F = 7;
    private final static int VECTOR_I = 8;
    private final static int OPTIONAL_VECTOR_I = 9;
    private final static int DIRECTION = 10;
    private final static int OPTIONAL_UUID = 11;
    private final static int BLOCK_STATE = 12;

    private final TObjectIntMap<ParameterType<?>> idByParameterType = new TObjectIntHashMap<>();

    {
        this.idByParameterType.put(ParameterTypes.BYTE, BYTE);
        this.idByParameterType.put(ParameterTypes.INTEGER, INTEGER);
        this.idByParameterType.put(ParameterTypes.FLOAT, FLOAT);
        this.idByParameterType.put(ParameterTypes.STRING, STRING);
        this.idByParameterType.put(ParameterTypes.TEXT, TEXT);
        this.idByParameterType.put(ParameterTypes.ITEM_STACK, ITEM_STACK);
        this.idByParameterType.put(ParameterTypes.BOOLEAN, BOOLEAN);
        this.idByParameterType.put(ParameterTypes.VECTOR_F, VECTOR_F);
        this.idByParameterType.put(ParameterTypes.VECTOR_I, VECTOR_I);
        this.idByParameterType.put(ParameterTypes.OPTIONAL_VECTOR_I, OPTIONAL_VECTOR_I);
        this.idByParameterType.put(ParameterTypes.DIRECTION, DIRECTION);
        this.idByParameterType.put(ParameterTypes.OPTIONAL_UUID, OPTIONAL_UUID);
        this.idByParameterType.put(ParameterTypes.BLOCK_STATE, BLOCK_STATE);
    }

    @Override
    public void write(SerializerContext context, ByteBuf buf, List<Parameter<?>> object) throws CodecException {
        for (Parameter<?> parameter : object) {
            final int type = this.idByParameterType.get(parameter.getParameterType());
            buf.writeByte(type);
            buf.writeByte(parameter.getIndex());
            switch (type) {
                case BYTE:
                    buf.writeByte((Byte) parameter.getObject());
                    break;
                case INTEGER:
                    context.writeVarInt(buf, (Integer) parameter.getObject());
                    break;
                case FLOAT:
                    buf.writeFloat((Float) parameter.getObject());
                    break;
                case STRING:
                    context.write(buf, Types.STRING, (String) parameter.getObject());
                    break;
                case TEXT:
                    context.write(buf, Types.TEXT, (Text) parameter.getObject());
                    break;
                case ITEM_STACK:
                    context.write(buf, Types.ITEM_STACK, (ItemStack) parameter.getObject());
                    break;
                case BOOLEAN:
                    buf.writeBoolean((Boolean) parameter.getObject());
                    break;
                case VECTOR_F:
                    final Vector3f vector3f = (Vector3f) parameter.getObject();
                    buf.writeFloat(vector3f.getX());
                    buf.writeFloat(vector3f.getY());
                    buf.writeFloat(vector3f.getZ());
                    break;
                case VECTOR_I:
                    context.write(buf, Types.POSITION, (Vector3i) parameter.getObject());
                    break;
                case OPTIONAL_VECTOR_I:
                    final Vector3i position = ((Optional<Vector3i>) parameter.getObject()).orElse(null);
                    buf.writeBoolean(position != null);
                    if (position != null) {
                        context.write(buf, Types.POSITION, position);
                    }
                    break;
                case DIRECTION:
                    context.writeVarInt(buf, 0); // TODO
                    break;
                case OPTIONAL_UUID:
                    final UUID uuid = ((Optional<UUID>) parameter.getObject()).orElse(null);
                    buf.writeBoolean(uuid != null);
                    if (uuid != null) {
                        context.write(buf, Types.UNIQUE_ID, uuid);
                    }
                    break;
                case BLOCK_STATE:
                    context.writeVarInt(buf, Registries.getBlockRegistry().getStateInternalId((BlockState) parameter.getObject()));
                    break;
            }
        }
        buf.writeByte(0xff);
    }

    @Override
    public List<Parameter<?>> read(SerializerContext context, ByteBuf buf) throws CodecException {
        throw new CodecException();
    }
}
