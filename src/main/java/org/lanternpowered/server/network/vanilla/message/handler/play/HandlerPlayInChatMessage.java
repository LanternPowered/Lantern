/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
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

import com.google.common.collect.ImmutableMap;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import kotlin.text.MatchResult;
import org.apache.commons.lang3.StringUtils;
import org.lanternpowered.api.cause.CauseStack;
import org.lanternpowered.server.command.LanternCommandCause;
import org.lanternpowered.server.config.GlobalConfig;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.NetworkSession;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInChatMessage;
import org.lanternpowered.server.permission.Permissions;
import org.lanternpowered.server.text.TextConstants;
import org.lanternpowered.server.text.action.LanternClickActionCallbacks;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.message.MessageEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.chat.ChatTypes;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HandlerPlayInChatMessage implements Handler<MessagePlayInChatMessage> {

    public static final String URL_ARGUMENT = "url";
    private final static AttributeKey<ChatData> CHAT_DATA = AttributeKey.valueOf("chat-data");

    private static class ChatData {

        private int chatThrottle;
        private long lastChatTime = -1L;
    }

    @Override
    public void handle(NetworkContext context, MessagePlayInChatMessage message) {
        final NetworkSession session = context.getSession();
        final LanternPlayer player = session.getPlayer();
        player.resetIdleTimeoutCounter();
        player.resetOpenedSignPosition();
        final String message0 = message.getMessage();

        // Check for a valid click action callback
        final MatchResult result = LanternClickActionCallbacks.INSTANCE.getCommandPattern().matchEntire(message0);
        if (result != null) {
            final UUID uniqueId = UUID.fromString(result.getGroupValues().get(1));
            final Consumer<CommandCause> callback = LanternClickActionCallbacks.INSTANCE.getCallbackForUUID(uniqueId);
            if (callback != null) {
                final CauseStack causeStack = CauseStack.current();
                try (CauseStack.Frame frame = causeStack.pushCauseFrame()) {
                    frame.addContext(EventContextKeys.PLAYER, player);
                    final CommandCause commandCause = new LanternCommandCause(frame.getCurrentCause());
                    callback.accept(commandCause);
                }
            } else {
                player.sendMessage(t("The callback you provided was not valid. Keep in mind that callbacks will expire "
                        + "after 10 minutes, so you might want to consider clicking faster next time!"));
            }
            return;
        }

        String message1 = StringUtils.normalizeSpace(message0);
        if (!isAllowedString(message0)) {
            session.disconnect(t("multiplayer.disconnect.illegal_characters"));
            return;
        }
        if (message1.startsWith("/")) {
            Lantern.getSyncScheduler().submit(() -> Sponge.getCommandManager().process(player, message1.substring(1)));
        } else {
            final Text nameText = player.get(Keys.DISPLAY_NAME).get();
            final Text rawMessageText = Text.of(message0);
            final GlobalConfig.Chat.Urls urls = Lantern.getGame().getGlobalConfig().getChat().getUrls();
            final Text messageText;
            if (urls.isEnabled() && player.hasPermission(Permissions.Chat.FORMAT_URLS)) {
                messageText = newTextWithLinks(message0, urls.getTemplate(), false);
            } else {
                messageText = rawMessageText;
            }
            final MessageChannel channel = player.getMessageChannel();

            final CauseStack causeStack = CauseStack.current();
            try (CauseStack.Frame frame = causeStack.pushCauseFrame()) {
                frame.addContext(EventContextKeys.PLAYER, player);
                final MessageChannelEvent.Chat event = SpongeEventFactory.createMessageChannelEventChat(causeStack.getCurrentCause(),
                        channel, Optional.of(channel), new MessageEvent.MessageFormatter(nameText, messageText), rawMessageText, false);
                if (!Sponge.getEventManager().post(event) && !event.isMessageCancelled()) {
                    event.getChannel().ifPresent(c -> c.send(player, event.getMessage(), ChatTypes.CHAT));
                }
            }
        }
        final Attribute<ChatData> attr = context.getChannel().attr(CHAT_DATA);
        ChatData chatData = attr.get();
        if (chatData == null) {
            chatData = new ChatData();
            final ChatData chatData1 = attr.setIfAbsent(chatData);
            if (chatData1 != null) {
                chatData = chatData1;
            }
        }
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (chatData) {
            final long currentTime = LanternGame.currentTimeTicks();
            if (chatData.lastChatTime != -1L) {
                chatData.chatThrottle = (int) Math.max(0, chatData.chatThrottle - (currentTime - chatData.lastChatTime));
            }
            chatData.lastChatTime = currentTime;
            chatData.chatThrottle += 20;
            if (chatData.chatThrottle > Lantern.getGame().getGlobalConfig().getChatSpamThreshold()) {
                session.disconnect(t("disconnect.spam"));
            }
        }
    }

    private static final Pattern URL_PATTERN = Pattern.compile(
            "((?:[a-z0-9]{2,}://)?(?:(?:[0-9]{1,3}\\.){3}[0-9]{1,3}|(?:[-\\w_]+\\.[a-z]{2,}?))(?::[0-9]{1,5})?.*?(?=[!\"\u00A7 \n]|$))",
            Pattern.CASE_INSENSITIVE);

    private static Text newTextWithLinks(String message, TextTemplate template, boolean allowMissingHeader) {
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

                builder.append(template.apply(ImmutableMap.of(URL_ARGUMENT, url))
                        .onClick(TextActions.openUrl(urlInstance))
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
