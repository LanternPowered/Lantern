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
package org.lanternpowered.server.text.title;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTitle;
import org.spongepowered.api.text.title.Title;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class LanternTitles {

    private final static LoadingCache<Title, CacheValue> messagesCache =
            Caffeine.newBuilder().weakKeys().build(LanternTitles::createValue);

    private static CacheValue createValue(Title title) {
        final ImmutableList.Builder<Message> builder = ImmutableList.builder();
        if (title.isClear()) {
            builder.add(new MessagePlayOutTitle.Clear());
        }
        if (title.isReset()) {
            builder.add(new MessagePlayOutTitle.Reset());
        }
        final Optional<Integer> fadeIn = title.getFadeIn();
        final Optional<Integer> stay = title.getStay();
        final Optional<Integer> fadeOut = title.getFadeOut();
        if (fadeIn.isPresent() || stay.isPresent() || fadeOut.isPresent()) {
            builder.add(new MessagePlayOutTitle.SetTimes(fadeIn.orElse(20), stay.orElse(60), fadeOut.orElse(20)));
        }
        if (title.getTitle().isPresent() || title.getSubtitle().isPresent() || title.getActionBar().isPresent()) {
            return new LocaleCacheValue(builder.build(), title);
        } else {
            return new CacheValue(builder.build());
        }
    }

    private static class CacheValue {

        final List<Message> messages;

        CacheValue(List<Message> messages) {
            this.messages = messages;
        }

        public List<Message> getMessages(Locale locale) {
            return this.messages;
        }
    }

    private static class LocaleCacheValue extends CacheValue {

        private final WeakReference<Title> title;
        private final Map<Locale, List<Message>> cache = new ConcurrentHashMap<>();

        LocaleCacheValue(List<Message> baseMessages, Title title) {
            super(baseMessages);
            this.title = new WeakReference<>(title);
        }

        @Override
        public List<Message> getMessages(Locale locale) {
            return this.cache.computeIfAbsent(locale, locale0 -> {
                Title title = this.title.get();
                if (title == null) {
                    return Collections.emptyList();
                }
                final ImmutableList.Builder<Message> builder = ImmutableList.builder();
                builder.addAll(this.messages);
                title.getTitle().ifPresent(text ->
                        builder.add(new MessagePlayOutTitle.SetTitle(text)));
                title.getSubtitle().ifPresent(text ->
                        builder.add(new MessagePlayOutTitle.SetSubtitle(text)));
                title.getActionBar().ifPresent(text ->
                        builder.add(new MessagePlayOutTitle.SetActionbarTitle(text)));
                return builder.build();
            });
        }
    }

    public static List<Message> getMessages(Title title, Locale locale) {
        return messagesCache.get(title).getMessages(locale);
    }

    public static List<Message> getMessages(Title title) {
        return getMessages(title, Locale.ENGLISH);
    }

    private LanternTitles() {
    }

}
