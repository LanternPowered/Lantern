package org.lanternpowered.server.text.title;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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
            CacheBuilder.newBuilder().weakKeys().expireAfterAccess(5, TimeUnit.MINUTES).build(new CacheLoader<Title, List<Message>>() {

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

    public static List<Message> getCachedMessages(Title title) {
        try {
            return messagesCache.get(title);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private LanternTitles() {
    }
}
