package org.lanternpowered.server.network.vanilla.message.processor.play;

import io.netty.handler.codec.CodecException;

import java.util.Collection;
import java.util.List;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.processor.Processor;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTabListEntries;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTabListEntries.Entry;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * This processor will separate the entries with different types and put them
 * into a new message, this is required because the vanilla codec can only use
 * one entry type for one message.
 */
public final class ProcessorPlayOutTabListEntries implements Processor<MessagePlayOutTabListEntries> {

    @Override
    public void process(CodecContext context, MessagePlayOutTabListEntries message, List<Message> output) throws CodecException {
        Multimap<Class<?>, Entry> entriesByType = HashMultimap.create();
        for (Entry entry : message.getEntries()) {
            entriesByType.put(entry.getClass(), entry);
        }
        if (entriesByType.isEmpty()) {
            return;
        }
        if (entriesByType.keySet().size() == 1) {
            output.add(message);
        } else {
            for (java.util.Map.Entry<Class<?>, Collection<Entry>> en : entriesByType.asMap().entrySet()) {
                output.add(new MessagePlayOutTabListEntries(en.getValue()));
            }
        }
    }
}
