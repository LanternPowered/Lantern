package org.lanternpowered.server.text;

import java.util.Locale;
import java.util.concurrent.ExecutionException;

import org.lanternpowered.server.text.gson.JsonTextRepresentation;
import org.lanternpowered.server.text.translation.TranslationManager;
import org.lanternpowered.server.text.xml.XmlTextRepresentation;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextFactory;
import org.spongepowered.api.text.TextRepresentation;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public final class LanternTextFactory implements TextFactory {

    private final LoadingCache<Character, LegacyTextRepresentation> legacyCache = CacheBuilder.newBuilder()
            .maximumSize(53)
            .build(new CacheLoader<Character, LegacyTextRepresentation>() {
                @Override
                public LegacyTextRepresentation load(Character key) throws Exception {
                    return new LegacyTextRepresentation(key);
                }
            });

    private final JsonTextRepresentation jsonTextRepresentation;
    private final PlainTextRepresentation plainTextRepresentation;
    private final XmlTextRepresentation xmlTextRepresentation;

    public LanternTextFactory(TranslationManager translationManager) {
        this.jsonTextRepresentation = new JsonTextRepresentation(translationManager);
        this.plainTextRepresentation = new PlainTextRepresentation();
        this.xmlTextRepresentation = new XmlTextRepresentation();
    }

    @Override
    public String toPlain(Text text) {
        return this.plainTextRepresentation.to(text);
    }

    @Override
    public String toPlain(Text text, Locale locale) {
        return this.plainTextRepresentation.to(text, locale);
    }

    @Override
    public TextRepresentation json() {
        return this.jsonTextRepresentation;
    }

    @Override
    public TextRepresentation xml() {
        return this.xmlTextRepresentation;
    }

    @Override
    public char getLegacyChar() {
        return LegacyTextRepresentation.DEFAULT_CHAR;
    }

    @Override
    public TextRepresentation legacy(char legacyChar) {
        if (legacyChar == LegacyTextRepresentation.DEFAULT_CHAR) {
            return LegacyTextRepresentation.DEFAULT_REPRESENTATION;
        }
        try {
            return this.legacyCache.get(legacyChar);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String stripLegacyCodes(String text, char code) {
        return LegacyTextRepresentation.strip(text, code);
    }

    @Override
    public String replaceLegacyCodes(String text, char from, char to) {
        return LegacyTextRepresentation.replace(text, from, to);
    }
}
