package org.lanternpowered.server.text.translation;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nullable;

import org.spongepowered.api.text.translation.ResourceBundleTranslation;
import org.spongepowered.api.text.translation.Translation;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class LanternTranslationManager implements TranslationManager {

    private static class ResourceKey {

        private final String name;
        private final Locale locale;

        public ResourceKey(String name, @Nullable Locale locale) {
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
                    if (bundles.containsKey(locale)) {
                        for (ResourceBundle resourceBundle : bundles.get(locale)) {
                            if (resourceBundle.containsKey(key.name)) {
                                return Optional.of(resourceBundle);
                            }
                        }
                    }
                    return Optional.absent();
                }

            });

    private final ConcurrentMap<Locale, Set<ResourceBundle>> bundles = Maps.newConcurrentMap();

    @Override
    public void addResourceBundle(String resourceBundle, Locale locale) {
        // We cannot allow the resource bundle instance to be directly
        // added to the translation manager, because for some strange
        // reasons the "getLocale" always a empty object returns (no name)
        // and doesn't match the one in the constructor
        ResourceBundle bundle = ResourceBundle.getBundle(checkNotNull(resourceBundle, "resourceBundle"));
        Set<ResourceBundle> bundles;

        if (this.bundles.containsKey(locale)) {
            bundles = this.bundles.get(locale);
        } else {
            this.bundles.put(locale, bundles = Sets.newConcurrentHashSet());
        }

        bundles.add(bundle);

        Set<ResourceKey> refresh = Sets.newHashSet();
        for (ResourceKey key : this.resourceBundlesCache.asMap().keySet()) {
            if (key.locale.equals(locale) && bundle.containsKey(key.name)) {
                refresh.add(key);
            }
        }
        if (!refresh.isEmpty()) {
            this.resourceBundlesCache.invalidateAll(refresh);
        }
    }

    @Override
    public Translation get(final String key) {
        return new ResourceBundleTranslation(checkNotNullOrEmpty(key, "key"), new Function<Locale, ResourceBundle>() {
            @Nullable
            @Override
            public ResourceBundle apply(Locale input) {
                try {
                    return resourceBundlesCache.get(new ResourceKey(key, input)).orNull();
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
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
        return Optional.absent();
    }
}
