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
import static org.lanternpowered.server.text.translation.TranslationHelper.tr;

import org.lanternpowered.server.catalog.PluginCatalogType;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.text.translation.Translation;

import java.util.function.BiConsumer;

public class LanternPotionEffectType extends PluginCatalogType.Base.Translatable.Internal implements PotionEffectType {

    private final Translation potionTranslation;
    private final BiConsumer<Entity, PotionEffect> effectConsumer;
    private boolean instant;

    public LanternPotionEffectType(String pluginId, String id, int internalId, String translationKey) {
        this(pluginId, id, id, internalId, translationKey);
    }

    public LanternPotionEffectType(String pluginId, String id, int internalId,
            Translation translation, Translation potionTranslation) {
        this(pluginId, id, id, internalId, translation, potionTranslation);
    }

    public LanternPotionEffectType(String pluginId, String id, String name, int internalId, String translationKey) {
        this(pluginId, id, name, internalId,
                tr("effect.%s", translationKey),
                tr("potion.effect.%s", name));
    }

    public LanternPotionEffectType(String pluginId, String id, String name, int internalId,
            Translation translation, Translation potionTranslation) {
        super(pluginId, id, name, translation, internalId);
        this.potionTranslation = checkNotNull(potionTranslation, "potionTranslation");
        this.effectConsumer = (entity, potionEffect) -> {};
    }

    public LanternPotionEffectType(String pluginId, String id, int internalId, String translationKey, BiConsumer<Entity, PotionEffect> effectConsumer) {
        this(pluginId, id, id, internalId, translationKey, effectConsumer);
    }

    public LanternPotionEffectType(String pluginId, String id, int internalId,
            Translation translation, Translation potionTranslation, BiConsumer<Entity, PotionEffect> effectConsumer) {
        this(pluginId, id, id, internalId, translation, potionTranslation, effectConsumer);
    }

    public LanternPotionEffectType(String pluginId, String id, String name, int internalId, String translationKey,
            BiConsumer<Entity, PotionEffect> effectConsumer) {
        this(pluginId, id, name, internalId,
                tr("effect.%s", translationKey),
                tr("potion.effect.%s", name), effectConsumer);
    }

    public LanternPotionEffectType(String pluginId, String id, String name, int internalId,
            Translation translation, Translation potionTranslation, BiConsumer<Entity, PotionEffect> effectConsumer) {
        super(pluginId, id, name, translation, internalId);
        this.potionTranslation = checkNotNull(potionTranslation, "potionTranslation");
        this.effectConsumer = effectConsumer;
    }

    public LanternPotionEffectType instant() {
        this.instant = true;
        return this;
    }

    @Override
    public boolean isInstant() {
        return this.instant;
    }

    @Override
    public Translation getPotionTranslation() {
        return this.potionTranslation;
    }

    public BiConsumer<Entity, PotionEffect> getEffectConsumer() {
        return this.effectConsumer;
    }
}
