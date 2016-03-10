/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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
