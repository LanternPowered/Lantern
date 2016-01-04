/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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
package org.lanternpowered.server.network.vanilla.message.item;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.network.message.codec.object.serializer.ObjectSerializer;
import org.lanternpowered.server.network.message.codec.object.serializer.ObjectSerializerContext;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecUtils;
import org.lanternpowered.server.text.LanternTextSerializer;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.manipulator.mutable.DisplayNameData;
import org.spongepowered.api.data.manipulator.mutable.item.DurabilityData;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.data.manipulator.mutable.item.LoreData;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.List;
import java.util.Locale;

public class SerializerItemStack implements ObjectSerializer<ItemStack> {

    @SuppressWarnings("deprecation")
    @Override
    public void write(ObjectSerializerContext context, ByteBuf buf, ItemStack object) throws CodecException {
        if (object == null) {
            buf.writeShort(-1);
            return;
        }

        Locale locale = CodecUtils.getLocale(context);

        DataContainer tag = new MemoryDataContainer();
        int id = 1; // TODO: Lookup the internal id
        int amount = 1;
        int data = 0;

        EnchantmentData enchantments = object.get(EnchantmentData.class).orElse(null);
        if (enchantments != null) {
            tag.set(DataQuery.of("ench"), Lists.newArrayList());
        }

        DurabilityData durability = object.get(DurabilityData.class).orElse(null);
        if (durability != null) {
            data = durability.durability().get();
            if (!durability.unbreakable().get()) {
                tag.set(DataQuery.of("Unbreakable"), true);
            }
        }

        DataView display = null;
        DisplayNameData name = object.get(DisplayNameData.class).orElse(null);
        if (name != null && name.customNameVisible().get()) {
            display = tag.createView(DataQuery.of("display"));
            display.set(DataQuery.of("Name"), ((LanternTextSerializer) TextSerializers.LEGACY_FORMATTING_CODE).serialize(
                    name.displayName().get(), locale));
        }
        LoreData lore = object.get(LoreData.class).orElse(null);
        if (lore != null) {
            List<String> lines = Lists.newArrayList();
            for (Text line : lore.lore()) {
                lines.add(((LanternTextSerializer) TextSerializers.LEGACY_FORMATTING_CODE).serialize(line, locale));
            }
            if (display == null) {
                display = tag.createView(DataQuery.of("display"));
            }
            display.set(DataQuery.of("Lore"), lines);
        }

        tag.set(DataQuery.of("HideFlags"), (byte) 63);

        buf.writeShort(id);
        buf.writeByte(amount);
        buf.writeShort(data);
        context.write(buf, DataView.class, tag);
    }

    @Override
    public ItemStack read(ObjectSerializerContext context, ByteBuf buf) throws CodecException {
        short id = buf.readShort();
        if (id == -1) {
            return null;
        }
        int amount = buf.readByte();
        int data = buf.readShort();
        DataView tag = context.read(buf, DataView.class);
        Locale locale = CodecUtils.getLocale(context);

        return null;
    }
}
