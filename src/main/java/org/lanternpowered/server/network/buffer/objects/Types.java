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
package org.lanternpowered.server.network.buffer.objects;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.DecoderException;
import org.lanternpowered.server.data.io.store.ObjectStore;
import org.lanternpowered.server.data.io.store.ObjectStoreRegistry;
import org.lanternpowered.server.data.io.store.item.ItemStackStore;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.registry.type.item.ItemRegistryModule;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.objects.LocalizedText;
import org.lanternpowered.server.network.objects.RawItemStack;
import org.lanternpowered.server.text.gson.JsonTextSerializer;
import org.lanternpowered.server.text.gson.JsonTextTranslatableSerializer;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;

public final class Types {

    public static final int VECTOR_3_I_LENGTH = Long.BYTES;

    /**
     * A vector3i (position) encoded for minecraft protocol.
     */
    public static final Type<Vector3i> VECTOR_3_I = Type.create(Vector3i.class, new ValueSerializer<Vector3i>() {
        @Override
        public void write(ByteBuffer buf, Vector3i object) throws CodecException {
            long x = object.getX();
            long y = object.getY();
            long z = object.getZ();
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

    public static final Gson TEXT_GSON = JsonTextSerializer.applyTo(new GsonBuilder(),
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

        private final ObjectStore<LanternItemStack> store = ObjectStoreRegistry.get().get(LanternItemStack.class).get();

        @Override
        public void write(ByteBuffer buf, @Nullable ItemStack object) throws CodecException {
            if (object == null) {
                buf.write(Types.RAW_ITEM_STACK, null);
            } else {
                final DataView dataView = new MemoryDataContainer(DataView.SafetyMode.NO_DATA_CLONED);
                this.store.serialize((LanternItemStack) object, dataView);
                buf.write(Types.RAW_ITEM_STACK, new RawItemStack(ItemRegistryModule.get().getInternalId(object.getItem()),
                        dataView.getShort(ItemStackStore.DATA).orElse((short) 0), object.getQuantity(),
                        dataView.getView(ItemStackStore.TAG).orElse(null)));
            }
        }

        @Override
        public ItemStack read(ByteBuffer buf) throws CodecException {
            // TODO: Properly deserialize the item stack data back
            RawItemStack rawItemStack = buf.read(Types.RAW_ITEM_STACK);
            //noinspection ConstantConditions
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
     * A raw item stack.
     */
    public static final Type<RawItemStack> RAW_ITEM_STACK = Type.create(RawItemStack.class, new ValueSerializer<RawItemStack>() {
        @Override
        public void write(ByteBuffer buf, RawItemStack object) throws CodecException {
            if (object == null) {
                buf.writeShort((short) -1);
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
