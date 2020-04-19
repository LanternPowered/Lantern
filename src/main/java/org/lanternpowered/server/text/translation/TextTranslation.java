/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
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
