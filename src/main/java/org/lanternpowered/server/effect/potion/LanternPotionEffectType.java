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
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.text.translation.Translation;

public class LanternPotionEffectType extends PluginCatalogType.Base.Translatable.Internal implements PotionEffectType {

    private final Translation potionTranslation;

    public LanternPotionEffectType(String pluginId, String id, int internalId, String translationKey) {
        this(pluginId, id, id, internalId, translationKey);
    }

    public LanternPotionEffectType(String pluginId, String id, int internalId,
            Translation translation, Translation potionTranslation) {
        this(pluginId, id, id, internalId, translation, potionTranslation);
    }

    public LanternPotionEffectType(String pluginId, String id, String name, int internalId, String translationKey) {
        this(pluginId, id, name, internalId,
                Lantern.getRegistry().getTranslationManager().get("effect." + translationKey),
                Lantern.getRegistry().getTranslationManager().get("potion.effect." + name));
    }

    public LanternPotionEffectType(String pluginId, String id, String name, int internalId,
            Translation translation, Translation potionTranslation) {
        super(pluginId, id, name, translation, internalId);
        this.potionTranslation = checkNotNull(potionTranslation, "potionTranslation");
    }

    @Override
    public boolean isInstant() {
        return false; // TODO
    }

    @Override
    public Translation getPotionTranslation() {
        return this.potionTranslation;
    }
}
