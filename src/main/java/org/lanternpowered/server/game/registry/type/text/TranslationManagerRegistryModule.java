/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.game.registry.type.text;

import org.lanternpowered.server.game.registry.EarlyRegistration;
import org.lanternpowered.server.text.translation.CombinedTranslationManager;
import org.lanternpowered.server.text.translation.LanternTranslationManager;
import org.lanternpowered.server.text.translation.MinecraftTranslationManager;
import org.lanternpowered.server.text.translation.TranslationManager;
import org.spongepowered.api.registry.RegistryModule;
import org.spongepowered.api.registry.util.RegistrationDependency;

import java.util.Locale;
import java.util.ResourceBundle;

@RegistrationDependency(LocaleRegistryModule.class)
public final class TranslationManagerRegistryModule implements RegistryModule {

    private CombinedTranslationManager translationManager;

    @EarlyRegistration
    @Override
    public void registerDefaults() {
        this.translationManager = new CombinedTranslationManager();
        final MinecraftTranslationManager minecraftTranslationManager = new MinecraftTranslationManager(
                ResourceBundle.getBundle("assets/minecraft/lang/en_US"));
        final LanternTranslationManager lanternTranslationManager = new LanternTranslationManager();
        lanternTranslationManager.addResourceBundle("assets/lantern/lang/en_US", Locale.ENGLISH);
        this.translationManager.addManager(minecraftTranslationManager);
        this.translationManager.addManager(lanternTranslationManager);
        this.translationManager.setDelegateManager(lanternTranslationManager);
    }

    public void addTranslationManager(TranslationManager translationManager) {
        this.translationManager.addManager(translationManager);
    }

    public TranslationManager getTranslationManager() {
        return this.translationManager;
    }

}
