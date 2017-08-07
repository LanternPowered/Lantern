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
package org.lanternpowered.server.text.translation;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import org.lanternpowered.server.text.LanternTextSerializer;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.text.translation.Translation;

import java.util.Locale;
import java.util.Objects;

/**
 * A {@link Translation} that contains a {@link Text} object internally.
 * The {@link #toText(Translation)} methods can be used to convert
 * {@link Translation} into a {@link Text} object.
 * <p>
 * The main usage for these {@link Translation}s are within the
 * {@link Inventory}s, these use always a {@link Translation} as name.
 */
public final class TextTranslation implements Translation {

    /**
     * Constructs a new {@link TextTranslation} for the provided
     * {@link Text} object.
     *
     * @param text The text
     * @return The text translation
     */
    public static TextTranslation of(Text text) {
        return new TextTranslation(checkNotNull(text, "text"));
    }

    public static Text toText(Translation translation) {
        return translation instanceof TextTranslation ? ((TextTranslation) translation).text : Text.of(translation);
    }

    public static Text toText(Translation translation, Object... args) {
        return translation instanceof TextTranslation ? ((TextTranslation) translation).text : Text.of(translation, args);
    }

    private final Text text;

    private TextTranslation(Text text) {
        this.text = text;
    }

    @Override
    public String getId() {
        return this.text.toPlain();
    }

    @Override
    public String get(Locale locale) {
        return ((LanternTextSerializer) TextSerializers.PLAIN).serialize(this.text, locale);
    }

    @Override
    public String get(Locale locale, Object... args) {
        return get(locale);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("text", this.text)
                .toString();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TextTranslation && ((TextTranslation) obj).text.equals(this.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass().getName(), this.text);
    }
}
