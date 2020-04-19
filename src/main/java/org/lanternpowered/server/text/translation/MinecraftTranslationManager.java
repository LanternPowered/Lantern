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

import org.lanternpowered.api.asset.Asset;
import org.lanternpowered.server.asset.ReloadListener;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.util.JsonResourceBundle;
import org.spongepowered.api.text.translation.FixedTranslation;
import org.spongepowered.api.text.translation.Translation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("NullableProblems")
public final class MinecraftTranslationManager implements TranslationManager, ReloadListener {

    private final Map<String, Translation> translations = new ConcurrentHashMap<>();
    private ResourceBundle resourceBundle;

    public MinecraftTranslationManager() {
        onReload();
    }

    @Override
    public void addResourceBundle(Asset asset, Locale locale) {
    }

    @Override
    public Translation get(String key) {
        return getIfPresent(key).orElseGet(() -> this.translations.computeIfAbsent(key, FixedTranslation::new));
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
        final Asset asset = Lantern.getAssetRepository().get("minecraft", "lang/en_us.json").orElseThrow(
                () -> new IllegalStateException("The minecraft language file is missing!"));
        try (InputStream is = asset.getUrl().openStream()) {
            this.resourceBundle = JsonResourceBundle.loadFrom(is);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create the minecraft language resource bundle!", e);
        }
    }
}
