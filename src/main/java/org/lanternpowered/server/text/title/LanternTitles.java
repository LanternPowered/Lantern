/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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
package org.lanternpowered.server.text.title;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.object.LocalizedText;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTitle;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.title.Title;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import org.spongepowered.api.util.annotation.NonnullByDefault;

@NonnullByDefault
public final class LanternTitles {

    private final static LoadingCache<Title, CacheValue> messagesCache = 
            CacheBuilder.newBuilder().weakKeys().build(new CacheLoader<Title, CacheValue>() {
                @Override
                public CacheValue load(Title key) throws Exception {
                    return createValue(key);
                }
            });

    private static CacheValue createValue(Title title) {
        final ImmutableList.Builder<Message> builder = ImmutableList.builder();
        if (title.isClear()) {
            builder.add(new MessagePlayOutTitle.Clear());
        }
        if (title.isReset()) {
            builder.add(new MessagePlayOutTitle.Reset());
        }
        Optional<Integer> fadeIn = title.getFadeIn();
        Optional<Integer> stay = title.getStay();
        Optional<Integer> fadeOut = title.getFadeOut();
        if (fadeIn.isPresent() || stay.isPresent() || fadeOut.isPresent()) {
            builder.add(new MessagePlayOutTitle.SetTimes(fadeIn.orElse(20), stay.orElse(60), fadeOut.orElse(20)));
        }
        if (title.getTitle().isPresent() || title.getSubtitle().isPresent()) {
            return new LocaleCacheValue(builder.build(), title);
        } else {
            return new CacheValue(builder.build());
        }
    }

    private static class CacheValue {

        final List<Message> messages;

        public CacheValue(List<Message> messages) {
            this.messages = messages;
        }

        public List<Message> getMessages(Locale locale) {
            return this.messages;
        }
    }

    private static class LocaleCacheValue extends CacheValue {

        private final WeakReference<Title> title;
        private final Map<Locale, List<Message>> cache = Maps.newConcurrentMap();

        public LocaleCacheValue(List<Message> baseMessages, Title title) {
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
                final ImmutableList.Builder<Message> builder = ImmutableList.<Message>builder();
                builder.addAll(this.messages);
                Optional<Text> text = title.getTitle();
                if (text.isPresent()) {
                    builder.add(new MessagePlayOutTitle.SetTitle(new LocalizedText(text.get(), locale)));
                }
                text = title.getSubtitle();
                if (text.isPresent()) {
                    builder.add(new MessagePlayOutTitle.SetSubtitle(new LocalizedText(text.get(), locale)));
                }
                return builder.build();
            });
        }
    }

    public static List<Message> getMessages(Title title, Locale locale) {
        try {
            return messagesCache.get(title).getMessages(locale);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Message> getMessages(Title title) {
        return getMessages(title, Locale.ENGLISH);
    }

    private LanternTitles() {
    }
}
