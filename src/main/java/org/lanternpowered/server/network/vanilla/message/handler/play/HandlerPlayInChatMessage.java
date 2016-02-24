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

import org.apache.commons.lang3.StringUtils;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.session.Session;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInChatMessage;
import org.lanternpowered.server.text.TextConstants;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.translation.Translation;

import java.util.Optional;

public final class HandlerPlayInChatMessage implements Handler<MessagePlayInChatMessage> {

    @Override
    public void handle(NetworkContext context, MessagePlayInChatMessage message) {
        Session session = context.getSession();
        Player player = session.getPlayer();
        String message0 = message.getMessage();
        String message1 = StringUtils.normalizeSpace(message0);
        if (!isAllowedString(message0)) {
            session.disconnect("Illegal characters in chat!");
            return;
        }
        if (message1.startsWith("/")) {
            LanternGame.get().getCommandManager().process(player, message1.substring(1));
        } else {
            Translation translation = LanternGame.get().getRegistry().getTranslationManager().get("chat.type.text");
            Object displayName = player.getName(); // TODO: player.getDisplayNameData().displayName().get();
            Text rawMessage = Text.of(message0);
            Optional<Text> text = Optional.of(Text.builder(translation, displayName, rawMessage).build());
            MessageChannel channel = player.getMessageChannel();
            MessageChannelEvent.Chat event = SpongeEventFactory.createMessageChannelEventChat(Cause.of(player),
                    channel, Optional.of(channel), text, text, rawMessage);
            if (!LanternGame.get().getEventManager().post(event)) {
                Optional<MessageChannel> optChannel = event.getChannel();
                if (optChannel.isPresent() && (text = event.getMessage()).isPresent()) {
                    optChannel.get().send(player, text.get());
                }
            }
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
