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
import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Sets;
import org.lanternpowered.server.asset.Asset;
import org.lanternpowered.server.asset.ReloadListener;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.text.translation.ResourceBundleTranslation;
import org.spongepowered.api.text.translation.Translation;

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
import java.util.concurrent.ExecutionException;

import javax.annotation.Nullable;

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
            CacheBuilder.newBuilder().build(new CacheLoader<ResourceKey, Optional<ResourceBundle>>() {

                @Override
                public Optional<ResourceBundle> load(ResourceKey key) throws Exception {
                    Locale locale = key.locale == null ? Locale.ENGLISH : key.locale;
                    Optional<ResourceBundle> optBundle = this.load(key.name, locale);
                    if (!optBundle.isPresent() && locale != Locale.ENGLISH) {
                        optBundle = this.load(key.name, Locale.ENGLISH);
                    }
                    return optBundle;
                }

                private Optional<ResourceBundle> load(String name, Locale locale) throws Exception {
                    if (bundles.containsKey(locale)) {
                        for (ResourceBundle resourceBundle : bundles.get(locale)) {
                            if (resourceBundle.containsKey(name)) {
                                return Optional.of(resourceBundle);
                            }
                        }
                    }
                    return Optional.empty();
                }

            });

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
        try {
            final InputStream inputStream = asset.getUrl().openStream();
            try {
                final ResourceBundle bundle = new PropertyResourceBundle(inputStream);
                this.bundles.computeIfAbsent(locale, locale0 -> Sets.newConcurrentHashSet()).add(bundle);
                if (refresh) {
                    final Set<ResourceKey> refreshKeys = Sets.newHashSet();
                    for (ResourceKey key : this.resourceBundlesCache.asMap().keySet()) {
                        Locale locale1 = key.locale == null ? Locale.ENGLISH : key.locale;
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
        } catch (IOException e) {
            Lantern.getLogger().warn("Unable to open the asset stream for: " + asset.getId(), e);
        }
    }

    @Override
    public Translation get(final String key) {
        return new ResourceBundleTranslation(checkNotNullOrEmpty(key, "key"), locale -> {
            try {
                return this.resourceBundlesCache.get(new ResourceKey(key, locale)).orElse(null);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Optional<Translation> getIfPresent(String key) {
        checkNotNullOrEmpty(key, "key");
        try {
            if (this.resourceBundlesCache.get(new ResourceKey(key, null)).isPresent()) {
                return Optional.of(this.get(key));
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
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
