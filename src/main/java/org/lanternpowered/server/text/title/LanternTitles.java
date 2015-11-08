/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTitle;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.title.Title;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;

public final class LanternTitles {

    private final static LoadingCache<Title, List<Message>> messagesCache = 
            CacheBuilder.newBuilder().weakKeys().build(new CacheLoader<Title, List<Message>>() {

                @Override
                public List<Message> load(Title key) throws Exception {
                    ImmutableList.Builder<Message> builder = ImmutableList.builder();
                    if (key.isClear()) {
                        builder.add(new MessagePlayOutTitle.Clear());
                    }
                    if (key.isReset()) {
                        builder.add(new MessagePlayOutTitle.Reset());
                    }
                    Optional<Integer> fadeIn = key.getFadeIn();
                    Optional<Integer> stay = key.getStay();
                    Optional<Integer> fadeOut = key.getFadeOut();
                    if (fadeIn.isPresent() || stay.isPresent() || fadeOut.isPresent()) {
                        builder.add(new MessagePlayOutTitle.SetTimes(fadeIn.orElse(20), stay.orElse(60), fadeOut.orElse(20)));
                    }
                    Optional<Text> title = key.getTitle();
                    if (title.isPresent()) {
                        builder.add(new MessagePlayOutTitle.SetTitle(title.get()));
                    }
                    title = key.getSubtitle();
                    if (title.isPresent()) {
                        builder.add(new MessagePlayOutTitle.SetSubtitle(title.get()));
                    }
                    return builder.build();
                }
            });

    public static List<Message> getMessages(Title title) {
        try {
            return messagesCache.get(title);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private LanternTitles() {
    }
}
