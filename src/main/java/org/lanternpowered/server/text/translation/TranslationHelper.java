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

import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TranslatableText;
import org.spongepowered.api.text.translation.Translation;

public final class TranslationHelper {

    /**
     * Gets the translated {@link Text} for a given
     * string and arguments.
     *
     * @param key The translation key
     * @param args Translation parameters
     * @return The translatable text
     */
    public static Text t(String key, Object... args) {
        return Text.of(tr(key), args);
    }

    /**
     * Gets the translated {@link Text.Builder} for a given
     * string and arguments.
     *
     * @param key The translation key
     * @param args Translation parameters
     * @return The translatable text builder
     */
    public static Text.Builder tb(String key, Object... args) {
        return Text.builder(tr(key), args);
    }

    public static Translation tr(String key) {
        return Lantern.getGame().getRegistry().getTranslationManager().get(key);
    }

    public static Translation tr(String key, Object... args) {
        return tr(String.format(key, args));
    }

    /**
     * Checks whether there are any non minecraft {@link Translation}s
     * (translations not available on the client) present in the given
     * {@link Text} object.
     *
     * @param text The text
     * @return Whether any non minecraft translation is found
     */
    public static boolean containsNonMinecraftTranslation(Text text) {
        if (text instanceof TranslatableText) {
            final Translation translation = ((TranslatableText) text).getTranslation();
            if (!(translation instanceof MinecraftTranslation)) {
                return true;
            }
        }
        return containsNonMinecraftTranslation(text.getChildren());
    }

    /**
     * Checks whether there are any non minecraft {@link Translation}s
     * (translations not available on the client) present in the given
     * {@link Text} objects.
     *
     * @param iterable The iterable of text objects
     * @return Whether any non minecraft translation is found
     */
    public static boolean containsNonMinecraftTranslation(Iterable<Text> iterable) {
        for (Text child : iterable) {
            if (containsNonMinecraftTranslation(child)) {
                return true;
            }
        }
        return false;
    }

    private TranslationHelper() {
    }

}
