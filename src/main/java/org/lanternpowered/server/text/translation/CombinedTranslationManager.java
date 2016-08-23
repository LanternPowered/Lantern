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
package org.lanternpowered.server.text.translation;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.asset.Asset;
import org.lanternpowered.server.asset.ReloadListener;
import org.spongepowered.api.text.translation.FixedTranslation;
import org.spongepowered.api.text.translation.Translation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.annotation.Nullable;

public final class CombinedTranslationManager implements TranslationManager, ReloadListener {

    // The primary translation manager that will be used
    private final List<TranslationManager> translationManagers = new ArrayList<>();

    // The delegate translation manager
    @Nullable private volatile TranslationManager delegateTranslationManager;

    /**
     * Adds a translation manager.
     * 
     * @param manager the manager
     */
    public void addManager(TranslationManager manager) {
        this.translationManagers.add(manager);
    }

    /**
     * Adds a translation manager at the specified index.
     * 
     * @param index the index
     * @param manager the manager
     */
    public void addManagerAt(int index, TranslationManager manager) {
        index = index < 0 ? 0 : index > this.translationManagers.size() ? this.translationManagers.size() : index;
        this.translationManagers.add(index, manager);
    }

    /**
     * Sets the translation manager that the {@link #addResourceBundle} method
     * should be delegated to, otherwise will the first one be used.
     * 
     * <p>The manager must first be added through {@link #addManager} before
     * you can use this method.</p>
     * 
     * @param manager the manager
     */
    public void setDelegateManager(TranslationManager manager) {
        checkNotNull(manager, "manager");
        checkArgument(this.translationManagers.contains(manager), "manager must be added before using this method");
        this.delegateTranslationManager = manager;
    }

    private TranslationManager getDelegateManager() {
        if (this.delegateTranslationManager != null) {
            return this.delegateTranslationManager;
        } else if (!this.translationManagers.isEmpty()) {
            return this.translationManagers.get(0);
        }
        return null;
    }

    @Override
    public void addResourceBundle(Asset asset, Locale locale) {
        final TranslationManager manager = this.getDelegateManager();
        if (manager != null) {
            manager.addResourceBundle(asset, locale);
        }
    }

    @Override
    public Translation get(String key) {
        return this.getIfPresent(key).orElseGet(() -> {
            TranslationManager manager = this.getDelegateManager();
            if (manager != null) {
                return manager.get(key);
            }
            return new FixedTranslation(key);
        });
    }

    @Override
    public Optional<Translation> getIfPresent(String key) {
        Optional<Translation> optTranslation;
        for (TranslationManager manager : this.translationManagers) {
            optTranslation = manager.getIfPresent(key);
            if (optTranslation.isPresent()) {
                return optTranslation;
            }
        }
        return Optional.empty();
    }

    @Override
    public void onReload() {
        this.translationManagers.stream()
                .filter(manager -> manager instanceof ReloadListener)
                .forEach(manager -> ((ReloadListener) manager).onReload());
    }
}
