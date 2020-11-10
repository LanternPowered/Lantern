package org.lanternpowered.server.text

import org.lanternpowered.api.key.lanternKey
import org.lanternpowered.api.text.translation.GlobalTranslator
import org.lanternpowered.api.text.translation.Translator

object Translators {

    /**
     * The global translator which will translate everything
     * except for vanilla minecraft translations.
     */
    val Global: GlobalTranslator = GlobalTranslator.get()

    /**
     * The translator that will attempt to translate everything,
     * including vanilla minecraft and other translations. Minecraft translations
     * can be overwritten through the default translator.
     */
    val GlobalAndMinecraft: Translator = ComposedTranslator(lanternKey("all"),
            TranslationRegistries.Lantern, TranslationRegistries.Minecraft)

    /**
     * Initializes the translators.
     */
    fun init() {
        Global.addSource(TranslationRegistries.Lantern)
    }
}
