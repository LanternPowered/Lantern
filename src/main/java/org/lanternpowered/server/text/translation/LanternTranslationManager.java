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
import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import org.lanternpowered.api.asset.Asset;
import org.lanternpowered.server.asset.ReloadListener;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.util.JsonResourceBundle;
import org.spongepowered.api.text.translation.ResourceBundleTranslation;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.text.translation.locale.Locales;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class LanternTranslationManager implements TranslationManager, ReloadListener {

    private static class ResourceKey {

        private final String name;
        @Nullable private final Locale locale;

        ResourceKey(String name, @Nullable Locale locale) {
            this.name = name;
            this.locale = locale;
        }

        @Override
        public int hashCode() {
            return 31 * this.name.hashCode() + (this.locale == null ? 0 : this.locale.hashCode());
        }
    }

    private final LoadingCache<ResourceKey, Optional<ResourceBundle>> resourceBundlesCache =
            Caffeine.newBuilder().build(key -> {
                final Locale locale = key.locale == null ? Locales.DEFAULT : key.locale;
                Optional<ResourceBundle> optBundle = this.load(key.name, locale);
                if (!optBundle.isPresent() && locale != Locales.DEFAULT) {
                    optBundle = this.load(key.name, Locales.DEFAULT);
                }
                return optBundle;
            });

    private Optional<ResourceBundle> load(String name, Locale locale) throws Exception {
        if (this.bundles.containsKey(locale)) {
            for (ResourceBundle resourceBundle : this.bundles.get(locale)) {
                if (resourceBundle.containsKey(name)) {
                    return Optional.of(resourceBundle);
                }
            }
        }
        return Optional.empty();
    }

    private final ConcurrentMap<Locale, Set<ResourceBundle>> bundles = new ConcurrentHashMap<>();
    private final Map<Asset, Locale> entries = new HashMap<>();

    @Override
    public void addResourceBundle(Asset asset, Locale locale) {
        checkNotNull(asset, "asset");
        checkNotNull(locale, "locale");
        synchronized (this.entries) {
            checkArgument(!this.entries.containsKey(asset), "The asset %s is already added to this translation manager.", asset.getId());
            this.entries.put(asset, locale);
            this.loadAssetBundle(asset, locale, true);
        }
    }

    private void loadAssetBundle(Asset asset, Locale locale, boolean refresh) {
        try (InputStream inputStream = asset.getUrl().openStream()) {
            ResourceBundle bundle;
            try {
                // Try to parse the json first
                bundle = JsonResourceBundle.loadFrom(inputStream);
            } catch (JsonSyntaxException e) {
                // It failed, open a new stream and try again as properties
                inputStream.close();
                try (InputStream inputStream1 = asset.getUrl().openStream()) {
                    bundle = new PropertyResourceBundle(inputStream1);
                }
            }

            this.bundles.computeIfAbsent(locale, locale0 -> Sets.newConcurrentHashSet()).add(bundle);
            if (refresh) {
                final Set<ResourceKey> refreshKeys = Sets.newHashSet();
                for (ResourceKey key : this.resourceBundlesCache.asMap().keySet()) {
                    final Locale locale1 = key.locale == null ? Locales.DEFAULT : key.locale;
                    if (locale1.equals(locale) && bundle.containsKey(key.name)) {
                        refreshKeys.add(key);
                    }
                }
                if (!refreshKeys.isEmpty()) {
                    this.resourceBundlesCache.invalidateAll(refreshKeys);
                }
            }
        } catch (IOException e) {
            Lantern.getLogger().warn("Unable to create the resource bundle for: " + asset.getId(), e);
        }
    }

    @Override
    public Translation get(final String key) {
        return new ResourceBundleTranslation(checkNotNullOrEmpty(key, "key"),
                locale -> this.resourceBundlesCache.get(new ResourceKey(key, locale)).orElse(null));
    }

    @Override
    public Optional<Translation> getIfPresent(String key) {
        checkNotNullOrEmpty(key, "key");
        if (this.resourceBundlesCache.get(new ResourceKey(key, null)).isPresent()) {
            return Optional.of(this.get(key));
        }
        return Optional.empty();
    }

    @Override
    public void onReload() {
        synchronized (this.entries) {
            this.bundles.clear();
            this.resourceBundlesCache.invalidateAll();
            for (Map.Entry<Asset, Locale> entry : this.entries.entrySet()) {
                this.loadAssetBundle(entry.getKey(), entry.getValue(), false);
            }
        }
    }
}
