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
package org.lanternpowered.server.text;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.catalog.PluginCatalogType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.SafeTextSerializer;
import org.spongepowered.api.text.serializer.TextParseException;
import org.spongepowered.api.text.translation.locale.Locales;

import java.util.Locale;

public final class PlainTextSerializer extends PluginCatalogType.Base implements SafeTextSerializer, LanternTextSerializer {

    public PlainTextSerializer(String pluginId, String name) {
        super(pluginId, name);
    }

    @Override
    public String serialize(Text text) {
        return serialize(text, Locales.DEFAULT);
    }

    @Override
    public String serialize(Text text, Locale locale) {
        checkNotNull(text, "text");
        checkNotNull(locale, "locale");
        return LegacyTexts.toLegacy(locale, text, (char) 0);
    }

    @Override
    public Text deserialize(String input) throws TextParseException {
        return Text.of(input);
    }

}
