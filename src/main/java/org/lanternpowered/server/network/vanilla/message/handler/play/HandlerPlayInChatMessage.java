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
package org.lanternpowered.server.network.vanilla.message.handler.play;

import static org.lanternpowered.server.text.translation.TranslationHelper.t;

import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.session.Session;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInChatMessage;
import org.lanternpowered.server.text.TextConstants;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.message.MessageEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HandlerPlayInChatMessage implements Handler<MessagePlayInChatMessage> {

    private final static AttributeKey<Long> LAST_CHAT_TIME = AttributeKey.valueOf("last-chat-time");

    @Override
    public void handle(NetworkContext context, MessagePlayInChatMessage message) {
        Session session = context.getSession();
        LanternPlayer player = session.getPlayer();
        player.resetIdleTimeoutCounter();
        String message0 = message.getMessage();
        String message1 = StringUtils.normalizeSpace(message0);
        if (!isAllowedString(message0)) {
            session.disconnect(t("disconnect.invalidChatCharacters"));
            return;
        }
        if (message1.startsWith("/")) {
            Lantern.getSyncExecutorService().submit(() -> Sponge.getCommandManager().process(player, message1.substring(1)));
        } else {
            Text nameText = Text.of(player.getName()); // TODO: player.getDisplayNameData().displayName().get();
            Text rawMessageText = Text.of(message0);
            Text messageText = newTextWithLinks(message0, true);
            MessageChannel channel = player.getMessageChannel();
            MessageChannelEvent.Chat event = SpongeEventFactory.createMessageChannelEventChat(Cause.of(NamedCause.source(player)),
                    channel, Optional.of(channel), new MessageEvent.MessageFormatter(nameText, messageText), rawMessageText, false);
            if (!Sponge.getEventManager().post(event) && !event.isMessageCancelled()) {
                event.getChannel().ifPresent(c -> c.send(player, event.getMessage(), ChatTypes.CHAT));
            }
        }
        Attribute<Long> attr = context.getChannel().attr(LAST_CHAT_TIME);
        long currentTime = System.currentTimeMillis();
        Long lastTime = attr.getAndSet(currentTime);
        if (lastTime != null && currentTime - lastTime < Lantern.getGame().getGlobalConfig().getChatSpamThreshold()) {
            session.disconnect(t("disconnect.spam"));
        }
    }

    private static final Pattern URL_PATTERN = Pattern.compile(
            "((?:[a-z0-9]{2,}://)?(?:(?:[0-9]{1,3}\\.){3}[0-9]{1,3}|(?:[-\\w_\\.]+\\.[a-z]{2,}?))(?::[0-9]{1,5})?.*?(?=[!\"\u00A7 \n]|$))", 2);

    private static Text newTextWithLinks(String message, boolean allowMissingHeader) {
        Text.Builder builder = null;
        StringBuilder lastMessage = null;

        Matcher matcher = URL_PATTERN.matcher(message);
        int lastEnd = 0;
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            String part = message.substring(lastEnd, start);
            if (part.length() > 0) {
                if (lastMessage != null) {
                    lastMessage.append(part);
                } else {
                    lastMessage = new StringBuilder(part);
                }
            }

            lastEnd = end;
            String url = message.substring(start, end);
            URL urlInstance = null;

            try {
                URI uri = new URI(url);
                if (uri.getScheme() == null) {
                    if (!allowMissingHeader) {
                        uri = null;
                    } else {
                        uri = new URI("http://" + url);
                    }
                }
                if (uri != null) {
                    urlInstance = uri.toURL();
                }
            } catch (URISyntaxException | MalformedURLException ignored) {
            }

            if (urlInstance == null) {
                if (lastMessage != null) {
                    lastMessage.append(url);
                } else {
                    lastMessage = new StringBuilder(url);
                }
            } else {
                if (builder == null) {
                    builder = Text.builder();
                }
                if (lastMessage != null) {
                    builder.append(Text.of(lastMessage.toString()));
                    lastMessage = null;
                }
                builder.append(Text.builder(url)
                        .onClick(TextActions.openUrl(urlInstance))
                        .color(TextColors.BLUE)
                        .style(TextStyles.UNDERLINE)
                        .build());
            }
        }

        if (builder == null) {
            return Text.of(message);
        } else {
            if (lastMessage != null) {
                builder.append(Text.of(lastMessage.toString()));
            }
            String end = message.substring(lastEnd);
            if (end.length() > 0) {
                builder.append(Text.of(message.substring(lastEnd)));
            }
            return builder.build();
        }
    }

    private static boolean isAllowedString(String string) {
        for (char character : string.toCharArray()) {
            if (!isAllowedCharacter(character)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isAllowedCharacter(char character) {
        return character != TextConstants.LEGACY_CHAR && character >= ' ' && character != '\u007F';
    }
}
