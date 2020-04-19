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
