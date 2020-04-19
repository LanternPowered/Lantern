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
import org.spongepowered.api.text.translation.Translation;

import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public interface TranslationManager {

    /**
     * Adds a resource bundle to the translation manager.
     *
     * @param asset The resource bundle asset to add
     * @param locale The locale of the resource bundle
     */
    void addResourceBundle(Asset asset, Locale locale);

    /**
     * Gets a {@link Translation} for the specified key.
     * 
     * @param key the key
     * @return the translation
     */
    Translation get(String key);

    /**
     * Gets a {@link Translation} if the key is present in one
     * of the {@link ResourceBundle}s.
     * 
     * @param key the key
     * @return the translation
     */
    Optional<Translation> getIfPresent(String key);

}
