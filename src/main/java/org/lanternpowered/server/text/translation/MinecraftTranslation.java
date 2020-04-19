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
import org.spongepowered.api.text.translation.Translation;

import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.ResourceBundle;

public final class MinecraftTranslation implements Translation {

    private final String id;
    private final ResourceBundle bundle;

    public MinecraftTranslation(String id, ResourceBundle bundle) {
        this.bundle = bundle;
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String get(Locale locale) {
        return this.bundle.getString(this.id);
    }

    @Override
    public String get(Locale locale, Object... args) {
        final String value = this.get(locale);
        try {
            return String.format(value, args);
        } catch (IllegalFormatException e) {
            Lantern.getLogger().error("Illegal format used in the translation: " + this.id, e);
            return value;
        }
    }

}
