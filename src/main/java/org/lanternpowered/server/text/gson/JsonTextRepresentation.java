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
package org.lanternpowered.server.text.gson;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Locale;

import org.lanternpowered.server.text.translation.TranslationManager;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextRepresentation;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.TextMessageException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public final class JsonTextRepresentation implements TextRepresentation {

    private final Gson gson;

    public JsonTextRepresentation(TranslationManager translationManager) {
        this.gson = JsonTextSerializer.applyTo(new GsonBuilder(), translationManager, false).create();
    }

    /**
     * Gets the gson instance.
     * 
     * @return the gson
     */
    public Gson getGson() {
        return this.gson;
    }

    @Override
    public String to(Text text) {
        return this.gson.toJson(checkNotNull(text, "text"));
    }

    @Override
    public String to(Text text, Locale locale) {
        checkNotNull(locale, "locale"); // Not used, but the locale shouldn't be null anyway
        return this.gson.toJson(checkNotNull(text, "text"));
    }

    @Override
    public Text from(String input) throws TextMessageException {
        try {
            return this.gson.fromJson(checkNotNull(input, "input"), Text.class);
        } catch (JsonSyntaxException e) {
            throw new TextMessageException(e);
        }
    }

    @Override
    public Text fromUnchecked(String input) {
        try {
            return this.gson.fromJson(checkNotNull(input, "input"), Text.class);
        } catch (JsonSyntaxException e) {
            return Texts.of(input);
        }
    }
}
