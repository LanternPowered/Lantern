package org.lanternpowered.server.text.translation;

import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;

public class TranslationHelper {

    /**
     * Get the translated text for a given string.
     *
     * @param key The translation key
     * @param args Translation parameters
     * @return The translatable text
     */
    public static Text t(String key, Object... args) {
        return Texts.of(LanternGame.get().getRegistry().getTranslationManager().get(key), args);
    }
}
