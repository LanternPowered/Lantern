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
package org.lanternpowered.server.text.translation;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.api.asset.Asset;
import org.lanternpowered.server.asset.ReloadListener;
import org.spongepowered.api.text.translation.FixedTranslation;
import org.spongepowered.api.text.translation.Translation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class CombinedTranslationManager implements TranslationManager, ReloadListener {

    // The primary translation manager that will be used
    private final List<TranslationManager> translationManagers = new ArrayList<>();

    // The delegate translation manager
    @Nullable private TranslationManager delegateTranslationManager;

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

    @Nullable
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
        final TranslationManager manager = getDelegateManager();
        if (manager != null) {
            manager.addResourceBundle(asset, locale);
        }
    }

    @Override
    public Translation get(String key) {
        return this.getIfPresent(key).orElseGet(() -> {
            final TranslationManager manager = getDelegateManager();
            if (manager != null) {
                return manager.get(key);
            }
            return new FixedTranslation(key);
        });
    }

    @Override
    public Optional<Translation> getIfPresent(String key) {
        for (TranslationManager manager : this.translationManagers) {
            final Optional<Translation> optTranslation = manager.getIfPresent(key);
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
