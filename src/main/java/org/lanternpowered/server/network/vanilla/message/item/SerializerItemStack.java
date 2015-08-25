package org.lanternpowered.server.network.vanilla.message.item;

import java.util.List;
import java.util.Locale;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.codec.object.serializer.ObjectSerializer;
import org.lanternpowered.server.network.message.codec.object.serializer.ObjectSerializerContext;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayInClientSettings;
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
import org.spongepowered.api.text.Texts;

import com.google.common.collect.Lists;

public class SerializerItemStack implements ObjectSerializer<ItemStack> {

    @SuppressWarnings("deprecation")
    @Override
    public void write(ObjectSerializerContext context, ByteBuf buf, ItemStack object) throws CodecException {
        if (object == null) {
            buf.writeShort(-1);
            return;
        }

        Locale locale = getLocale(context);

        DataContainer tag = new MemoryDataContainer();
        int id = 1; // TODO: Lookup the internal id
        int amount = 1;
        int data = 0;

        EnchantmentData enchantments = object.get(EnchantmentData.class).orNull();
        if (enchantments != null) {
            tag.set(DataQuery.of("ench"), Lists.newArrayList());
        }

        DurabilityData durability = object.get(DurabilityData.class).orNull();
        if (durability != null) {
            data = durability.durability().get();
            if (!durability.unbreakable().get()) {
                tag.set(DataQuery.of("Unbreakable"), true);
            }
        }

        DataView display = null;
        DisplayNameData name = object.get(DisplayNameData.class).orNull();
        if (name != null && name.customNameVisible().get()) {
            display = tag.createView(DataQuery.of("display"));
            display.set(DataQuery.of("Name"), Texts.legacy().to(name.displayName().get(), locale));
        }
        LoreData lore = object.get(LoreData.class).orNull();
        if (lore != null) {
            List<String> lines = Lists.newArrayList();
            for (Text line : lore.lore()) {
                lines.add(Texts.legacy().to(line, locale));
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

        Locale locale = getLocale(context);

        return null;
    }

    private static Locale getLocale(ObjectSerializerContext context) {
        if (context instanceof CodecContext) {
            Locale locale0 = ((CodecContext) context).channel().attr(ProcessorPlayInClientSettings.LOCALE).get();
            if (locale0 != null) {
                return locale0;
            }
        }
        return Locale.ENGLISH;
    }

}
