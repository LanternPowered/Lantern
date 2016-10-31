/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
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
package org.lanternpowered.server.effect.potion;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.catalog.PluginCatalogType;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.text.translation.TranslationManager;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.text.translation.Translation;

import java.util.ArrayList;
import java.util.List;

public class LanternPotionType extends PluginCatalogType.Base.Translatable.Internal implements PotionType {

    private final Translation lingeringTranslation;
    private final Translation splashTranslation;
    private final Translation tippedArrowTranslation;

    private final List<PotionEffect> potionEffects = new ArrayList<>();

    public LanternPotionType(String pluginId, String name, String translationPattern, int internalId) {
        super(pluginId, name, String.format(translationPattern, "potion"), internalId);
        final TranslationManager translationManager = Lantern.getRegistry().getTranslationManager();
        this.lingeringTranslation = translationManager.get(String.format(translationPattern, "lingering_potion"));
        this.splashTranslation = translationManager.get(String.format(translationPattern, "splash_potion"));
        this.tippedArrowTranslation = translationManager.get(String.format(translationPattern, "tipped_arrow"));
    }

    @Override
    public List<PotionEffect> getEffects() {
        return this.potionEffects;
    }

    public LanternPotionType add(PotionEffect potionEffect) {
        checkNotNull(potionEffect, "potionEffect");
        this.potionEffects.add(potionEffect);
        return this;
    }

    @Override
    public Translation getLingeringTranslation() {
        return this.lingeringTranslation;
    }

    @Override
    public Translation getSplashTranslation() {
        return this.splashTranslation;
    }

    @Override
    public Translation getTippedArrowTranslation() {
        return this.tippedArrowTranslation;
    }
}
