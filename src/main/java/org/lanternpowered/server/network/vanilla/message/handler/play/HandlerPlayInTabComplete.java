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

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInTabComplete;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTabComplete;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.command.TabCompleteEvent;
import org.spongepowered.api.util.GuavaCollectors;

import java.util.List;
import java.util.stream.Collectors;

public final class HandlerPlayInTabComplete implements Handler<MessagePlayInTabComplete> {

    @Override
    public void handle(NetworkContext context, MessagePlayInTabComplete message) {
        String text = message.getText().trim();
        boolean prefix = text.startsWith("/");
        if (prefix || message.getAssumeCommand()) {
            if (prefix) {
                text = text.substring(1);
            }
            List<String> suggestions = Sponge.getCommandManager().getSuggestions(context.getSession().getPlayer(), text);
            if (!prefix) {
                suggestions = suggestions.stream()
                        .map(s -> s.charAt(0) == '/' ? s.substring(1) : s)
                        .collect(GuavaCollectors.toImmutableList());
            // Make the command name start with '/'
            } else if (text.split(" ").length == 1) {
                suggestions = suggestions.stream()
                        .map(s -> s.charAt(0) == '/' ? s : '/' + s)
                        .collect(GuavaCollectors.toImmutableList());
            }
            context.getSession().send(new MessagePlayOutTabComplete(suggestions));
        } else {
            // Vanilla mc will complete user names if
            // no command is being completed
            int index = text.lastIndexOf(' ');
            String part;
            if (index == -1) {
                part = text;
            } else {
                part = text.substring(index + 1);
            }
            if (part.isEmpty()) {
                return;
            }
            final String part1 = part.toLowerCase();
            List<String> suggestions = Sponge.getServer().getOnlinePlayers().stream()
                    .map(CommandSource::getName)
                    .filter(n -> n.toLowerCase().startsWith(part1))
                    .collect(Collectors.toList());
            TabCompleteEvent.Chat event = SpongeEventFactory.createTabCompleteEventChat(Cause.source(context.getSession().getPlayer()).build(),
                    ImmutableList.copyOf(suggestions), suggestions, text);
            if (!Sponge.getEventManager().post(event)) {
                context.getSession().send(new MessagePlayOutTabComplete(suggestions));
            }
        }
    }
}
