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
package org.lanternpowered.server.text;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.lanternpowered.server.text.gson.JsonTextRepresentation;
import org.lanternpowered.server.text.translation.TranslationManager;
import org.lanternpowered.server.text.xml.XmlTextRepresentation;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextFactory;
import org.spongepowered.api.text.TextRepresentation;

import java.util.Locale;
import java.util.concurrent.ExecutionException;

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
