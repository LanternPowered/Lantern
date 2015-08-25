package org.lanternpowered.server.text.translation;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.text.translation.ResourceBundleTranslation;
import org.spongepowered.api.text.translation.Translation;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.Sets;

public class LanternTranslationManager implements TranslationManager {

    private static class ResourceKey {

        private final String name;
        private final Locale locale;

        public ResourceKey(String name, Locale locale) {
            this.name = name;
            this.locale = locale;
        }

        @Override
        public int hashCode() {
            return 31 * this.name.hashCode() + this.locale.hashCode();
        }
    }

    private final LoadingCache<ResourceKey, Optional<ResourceBundle>> resourceBundlesCache = 
            CacheBuilder.newBuilder().build(new CacheLoader<ResourceKey, Optional<ResourceBundle>>() {

                @Override
                public Optional<ResourceBundle> load(ResourceKey key) throws Exception {
                    for (ResourceBundle resourceBundle : resourceBundles) {
                        if (resourceBundle.getLocale().equals(key.locale) && resourceBundle.getString(key.name) != null) {
                            return Optional.of(resourceBundle);
                        }
                    }
                    return Optional.absent();
                }

            });

    private final LoadingCache<String, Translation> translationCache = 
            CacheBuilder.newBuilder().softValues().expireAfterWrite(5, TimeUnit.MINUTES)
            .removalListener(new RemovalListener<String, Translation>() {

                @Override
                public void onRemoval(RemovalNotification<String, Translation> notification) {
                    for (ResourceKey key : resourceBundlesCache.asMap().keySet()) {
                        if (key.name.equals(notification.getKey())) {
                            resourceBundlesCache.invalidate(key);
                            break;
                        }
                    }
                }

            }).build(new CacheLoader<String, Translation>() {

                @Override
                public Translation load(final String key) throws Exception {
                    return new ResourceBundleTranslation(key, new Function<Locale, ResourceBundle>() {

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

            });

    private final Set<ResourceBundle> resourceBundles = Sets.newConcurrentHashSet();

    @Override
    public void addResourceBundle(ResourceBundle resourceBundle) {
        if (!this.resourceBundles.add(checkNotNull(resourceBundle, "resourceBundle"))) {
            return;
        }
        Locale locale = resourceBundle.getLocale();
        Set<ResourceKey> refresh = Sets.newHashSet();
        for (ResourceKey key : this.resourceBundlesCache.asMap().keySet()) {
            if (key.locale.equals(locale) && resourceBundle.containsKey(key.name)) {
                refresh.add(key);
            }
        }
        if (!refresh.isEmpty()) {
            this.resourceBundlesCache.invalidateAll(refresh);
        }
    }

    @Override
    public Translation get(String key) {
        try {
            return translationCache.get(checkNotNullOrEmpty(key, "key"));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Translation> getIfPresent(String key) {
        checkNotNullOrEmpty(key, "key");
        try {
            if (this.resourceBundlesCache.get(new ResourceKey(key, Locale.ENGLISH)).isPresent()) {
                return Optional.of(this.get(key));
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        return Optional.absent();
    }

}
