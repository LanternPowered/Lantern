package org.lanternpowered.server.configuration;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.TextMessageException;

import com.google.common.reflect.TypeToken;

public final class TextTypeSerializer implements TypeSerializer<Text> {

    @Override
    public Text deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        final String value0 = value.getString();
        try {
            // Try to deserialize as xml
            return Texts.xml().from(value0);
        } catch (TextMessageException e0) {
            try {
                // Try to deserialize as json
                return Texts.json().from(value0);
            } catch (TextMessageException e1) {
                // No format is possible, use plain
                return Texts.of(value0);
            }
        }
    }

    @Override
    public void serialize(TypeToken<?> type, Text obj, ConfigurationNode value) throws ObjectMappingException {
        value.setValue(Texts.xml().to(obj));
    }
}
