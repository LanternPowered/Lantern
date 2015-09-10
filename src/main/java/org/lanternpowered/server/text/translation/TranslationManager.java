package org.lanternpowered.server.text.translation;

import java.util.Locale;
import java.util.ResourceBundle;

import org.spongepowered.api.text.translation.Translation;

import com.google.common.base.Optional;

public interface TranslationManager {

    /**
     * Adds a resource bundle to the translation manager.
     * 
     * @param resourceBundle the resource bundle
     */
    void addResourceBundle(String resourceBundle, Locale locale);

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
