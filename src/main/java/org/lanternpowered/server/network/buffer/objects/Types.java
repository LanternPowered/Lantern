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
package org.lanternpowered.server.network.buffer.objects;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.DecoderException;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.lanternpowered.server.game.registry.type.item.ItemRegistryModule;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.objects.LocalizedText;
import org.lanternpowered.server.network.objects.Parameter;
import org.lanternpowered.server.network.objects.ParameterType;
import org.lanternpowered.server.network.objects.ParameterTypes;
import org.lanternpowered.server.network.objects.RawItemStack;
import org.lanternpowered.server.text.gson.JsonTextSerializer;
import org.lanternpowered.server.text.gson.JsonTextTranslatableSerializer;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class Types {

    public static final int VECTOR_3_I_LENGTH = Long.BYTES;

    /**
     * A vector3i (position) encoded for minecraft protocol.
     */
    public static final Type<Vector3i> VECTOR_3_I = Type.create(Vector3i.class, new ValueSerializer<Vector3i>() {
        @Override
        public void write(ByteBuffer buf, Vector3i object) throws CodecException {
            int x = object.getX();
            int y = object.getY();
            int z = object.getZ();
            buf.writeLong((x & 0x3ffffff) << 38 | (y & 0xfff) << 26 | (z & 0x3ffffff));
        }

        @Override
        public Vector3i read(ByteBuffer buf) throws CodecException {
            long value = buf.readLong();
            int x = (int) (value >> 38);
            int y = (int) (value << 26 >> 52);
            int z = (int) (value << 38 >> 38);
            return new Vector3i(x, y, z);
        }
    });

    public static final int VECTOR_3_F_LENGTH = Float.BYTES * 3;

    /**
     * A vector3i (position) encoded for minecraft protocol.
     */
    public static final Type<Vector3f> VECTOR_3_F = Type.create(Vector3f.class, new ValueSerializer<Vector3f>() {
        @Override
        public void write(ByteBuffer buf, Vector3f object) throws CodecException {
            buf.ensureWritable(VECTOR_3_F_LENGTH);
            buf.writeFloat(object.getX());
            buf.writeFloat(object.getY());
            buf.writeFloat(object.getZ());
        }

        @Override
        public Vector3f read(ByteBuffer buf) throws CodecException {
            float x = buf.readFloat();
            float y = buf.readFloat();
            float z = buf.readFloat();
            return new Vector3f(x, y, z);
        }
    });

    private static final Gson TEXT_GSON = JsonTextSerializer.applyTo(new GsonBuilder(),
            Lantern.getGame().getRegistry().getTranslationManager(), true).create();

    /**
     * The client doesn't like it when the server just sends a
     * primitive json string, so we put it as one entry in an array
     * to avoid errors.
     *
     * @param json the json
     * @return the result json
     */
    private static String fixJson(String json) {
        char start = json.charAt(0);
        if (start == '[' || start == '{') {
            return json;
        } else {
            return '[' + json + ']';
        }
    }

    /**
     * A utf-8 encoded text prefixed by the length in var-int.
     */
    public static final Type<Text> TEXT = Type.create(Text.class, new ValueSerializer<Text>() {
        @Override
        public void write(ByteBuffer buf, Text object) throws CodecException {
            buf.writeString(fixJson(TEXT_GSON.toJson(object)));
        }

        @Override
        public Text read(ByteBuffer buf) throws CodecException {
            return TEXT_GSON.fromJson(buf.readString(), Text.class);
        }
    });

    /**
     * A localized text object.
     */
    public static final Type<LocalizedText> LOCALIZED_TEXT = Type.create(LocalizedText.class, new ValueSerializer<LocalizedText>() {
        @Override
        public void write(ByteBuffer buf, LocalizedText object) throws CodecException {
            JsonTextTranslatableSerializer.setCurrentLocale(object.getLocale());
            buf.writeString(fixJson(TEXT_GSON.toJson(object.getText())));
            JsonTextTranslatableSerializer.removeCurrentLocale();
        }

        @Override
        public LocalizedText read(ByteBuffer buf) throws CodecException {
            try {
                return new LocalizedText(TEXT_GSON.fromJson(buf.readString(), Text.class),
                        JsonTextTranslatableSerializer.getCurrentLocale());
            } catch (JsonSyntaxException e) {
                throw new DecoderException(e);
            }
        }
    });

    /**
     * A item stack.
     */
    public static final Type<ItemStack> ITEM_STACK = Type.create(ItemStack.class, new ValueSerializer<ItemStack>() {
        @Override
        public void write(ByteBuffer buf, ItemStack object) throws CodecException {
            // TODO: Properly serialize the item stack data
            buf.write(Types.RAW_ITEM_STACK, object == null ? null :
                   new RawItemStack(ItemRegistryModule.get().getInternalId(object.getItem()), 0, object.getQuantity(), null));
        }

        @Override
        public ItemStack read(ByteBuffer buf) throws CodecException {
            // TODO: Properly deserialize the item stack data back
            RawItemStack rawItemStack = buf.read(Types.RAW_ITEM_STACK);
            if (rawItemStack == null) {
                return null;
            }
            ItemType itemType = ItemRegistryModule.get().getTypeByInternalId(rawItemStack.getItemType()).orElse(null);
            if (itemType == null) {
                return null;
            }
            return new LanternItemStack(itemType, rawItemStack.getAmount());
        }
    });

    /**
     * A parameter list.
     */
    public static final Type<List<Parameter<?>>> PARAMETERS = Type.create(new TypeToken<List<Parameter<?>>>() {},
            new ValueSerializer<List<Parameter<?>>>() {

                private final static int BYTE = 0;
                private final static int INTEGER = 1;
                private final static int FLOAT = 2;
                private final static int STRING = 3;
                private final static int TEXT = 4;
                private final static int ITEM_STACK = 5;
                private final static int BOOLEAN = 6;
                private final static int VECTOR_F = 7;
                private final static int VECTOR_3_I = 8;
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
                    this.idByParameterType.put(ParameterTypes.VECTOR_I, VECTOR_3_I);
                    this.idByParameterType.put(ParameterTypes.OPTIONAL_VECTOR_I, OPTIONAL_VECTOR_I);
                    this.idByParameterType.put(ParameterTypes.DIRECTION, DIRECTION);
                    this.idByParameterType.put(ParameterTypes.OPTIONAL_UUID, OPTIONAL_UUID);
                    this.idByParameterType.put(ParameterTypes.BLOCK_STATE, BLOCK_STATE);
                }

                @Override
                public void write(ByteBuffer buf, List<Parameter<?>> object) throws CodecException {
                    for (Parameter<?> parameter : object) {
                        final int type = this.idByParameterType.get(parameter.getParameterType());
                        buf.writeByte((byte) type);
                        buf.writeByte((byte) parameter.getIndex());
                        switch (type) {
                            case BYTE:
                                buf.writeByte((Byte) parameter.getObject());
                                break;
                            case INTEGER:
                                buf.writeVarInt((Integer) parameter.getObject());
                                break;
                            case FLOAT:
                                buf.writeFloat((Float) parameter.getObject());
                                break;
                            case STRING:
                                buf.writeString((String) parameter.getObject());
                                break;
                            case TEXT:
                                buf.write(Types.TEXT, (Text) parameter.getObject());
                                break;
                            case ITEM_STACK:
                                buf.write(Types.ITEM_STACK, (ItemStack) parameter.getObject());
                                break;
                            case BOOLEAN:
                                buf.writeBoolean((Boolean) parameter.getObject());
                                break;
                            case VECTOR_F:
                                buf.write(Types.VECTOR_3_F, (Vector3f) parameter.getObject());
                                break;
                            case VECTOR_3_I:
                                buf.write(Types.VECTOR_3_I, (Vector3i) parameter.getObject());
                                break;
                            case OPTIONAL_VECTOR_I:
                                final Vector3i position = ((Optional<Vector3i>) parameter.getObject()).orElse(null);
                                buf.writeBoolean(position != null);
                                if (position != null) {
                                    buf.write(Types.VECTOR_3_I, (Vector3i) parameter.getObject());
                                }
                                break;
                            case DIRECTION:
                                buf.writeVarInt(0); // TODO
                                break;
                            case OPTIONAL_UUID:
                                final UUID uuid = ((Optional<UUID>) parameter.getObject()).orElse(null);
                                buf.writeBoolean(uuid != null);
                                if (uuid != null) {
                                    buf.writeUniqueId(uuid);
                                    buf.write(Types.VECTOR_3_I, (Vector3i) parameter.getObject());
                                }
                                break;
                            case BLOCK_STATE:
                                buf.writeVarInt(BlockRegistryModule.get().getStateInternalId((BlockState) parameter.getObject()));
                                break;
                        }
                    }
                    buf.writeByte((byte) 0xff);
                }

                @Override
                public List<Parameter<?>> read(ByteBuffer buf) throws CodecException {
                    throw new DecoderException();
                }
            });

    /**
     * A raw item stack.
     */
    public static final Type<RawItemStack> RAW_ITEM_STACK = Type.create(RawItemStack.class, new ValueSerializer<RawItemStack>() {
        @Override
        public void write(ByteBuffer buf, RawItemStack object) throws CodecException {
            if (object == null) {
                buf.writeByte((byte) -1);
            } else {
                buf.writeShort((short) object.getItemType());
                buf.writeByte((byte) object.getAmount());
                buf.writeShort((short) object.getData());
                buf.writeDataView(object.getDataView());
            }
        }

        @Override
        public RawItemStack read(ByteBuffer buf) throws CodecException {
            short id = buf.readShort();
            if (id == -1) {
                return null;
            }
            int amount = buf.readByte();
            int data = buf.readShort();
            DataView dataView = buf.readDataView();
            return new RawItemStack(id, data, amount, dataView);
        }
    });

    private Types() {
    }
}
