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
package org.lanternpowered.server.text.translation

import org.lanternpowered.server.text.translation.TranslationHelper.tr
import org.spongepowered.api.text.translation.Translatable
import org.spongepowered.api.text.translation.Translation

/**
 * Represents a translated/translatable object.
 */
class Translated(private val translation: Translation) : Translatable {

    /**
     * Constructs a [Translated] object from the given translation key.
     */
    constructor(translationKey: () -> String) : this(translationKey())

    /**
     * Constructs a [Translated] object from the given translation key.
     */
    constructor(translationKey: String) : this(tr(translationKey))

    override fun getTranslation(): Translation = this.translation
}
