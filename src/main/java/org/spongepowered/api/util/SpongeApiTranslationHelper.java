package org.spongepowered.api.util;

import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;

/**
 * This class overrides the class provided by the sponge api,
 * doing this to allow us to inject our own translations.
 */
public class SpongeApiTranslationHelper {

    private SpongeApiTranslationHelper() {
    }

    public static Text t(String key, Object... args) {
        return Texts.of(LanternGame.get().getRegistry().getTranslationManager().get(key), args);
    }
}
