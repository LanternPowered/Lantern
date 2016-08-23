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

import org.lanternpowered.server.asset.Asset;
import org.lanternpowered.server.asset.ReloadListener;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.translation.FixedTranslation;
import org.spongepowered.api.text.translation.Translation;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

public final class MinecraftTranslationManager implements TranslationManager, ReloadListener {

    private final Map<String, Translation> translations = new ConcurrentHashMap<>();
    @SuppressWarnings("NullableProblems") private ResourceBundle resourceBundle;

    public MinecraftTranslationManager() {
        this.onReload();
    }

    @Override
    public void addResourceBundle(Asset asset, Locale locale) {
    }

    @Override
    public Translation get(String key) {
        return this.getIfPresent(key).orElseGet(() -> this.translations.computeIfAbsent(key, FixedTranslation::new));
    }

    @Override
    public Optional<Translation> getIfPresent(String key) {
        if (this.resourceBundle.containsKey(key)) {
            return Optional.of(this.translations.computeIfAbsent(key, key0 -> new MinecraftTranslation(key, this.resourceBundle)));
        }
        return Optional.empty();
    }

    @Override
    public void onReload() {
        final Asset asset = Lantern.getAssetRepository().get("minecraft", "lang/en_US.properties").orElseThrow(
                () -> new IllegalStateException("The minecraft language file is missing!"));
        try {
            this.resourceBundle = new PropertyResourceBundle(asset.getUrl().openStream());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create the minecraft language resource bundle!", e);
        }
    }
}
