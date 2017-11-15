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

import com.google.common.collect.Lists;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInTabComplete;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTabComplete;
import org.spongepowered.api.text.Text;

public final class HandlerPlayInTabComplete implements Handler<MessagePlayInTabComplete> {

    @Override
    public void handle(NetworkContext context, MessagePlayInTabComplete message) {
        final String text = message.getInput();
        final LanternPlayer player = context.getSession().getPlayer();
        player.sendMessage(Text.of("Received tab completion (" + message.getId() + "): " + text));
        player.getConnection().send(new MessagePlayOutTabComplete(Lists.newArrayList(
                new MessagePlayOutTabComplete.Match("Avalue", null),
                new MessagePlayOutTabComplete.Match("Btest", null),
                new MessagePlayOutTabComplete.Match("Cwhy", null)), message.getId(), 0, 20));

        /*
        // The content with normalized spaces, the spaces are trimmed
        // from the ends and there are never two spaces directly after each other
        final String textNormalized = StringUtils.normalizeSpace(text);

        final boolean hasPrefix = textNormalized.startsWith("/");
        if (hasPrefix) {
            String command = textNormalized;

            // Don't include the '/'
            if (hasPrefix) {
                command = command.substring(1);
            }

            // Keep the last space, it must be there!
            if (text.endsWith(" ")) {
                command = command + " ";
            }

            // Get the suggestions
            List<String> suggestions = ((LanternCommandManager) Sponge.getCommandManager())
                    .getCustomSuggestions(player, command, null, false);

            // If the suggestions are for the command and there was a prefix, then append the prefix
            if (command.split(" ").length == 1 && !command.endsWith(" ")) {
                suggestions = suggestions.stream()
                        .map(suggestion -> '/' + suggestion)
                        .collect(ImmutableList.toImmutableList());
            }

            context.getSession().send(new MessagePlayOutTabComplete(suggestions));
        } else {
            // Vanilla mc will complete user names if
            // no command is being completed
            final int index = text.lastIndexOf(' ');
            final String part;
            if (index == -1) {
                part = text;
            } else {
                part = text.substring(index + 1);
            }
            if (part.isEmpty()) {
                return;
            }
            final String part1 = part.toLowerCase();
            final List<String> suggestions = Sponge.getServer().getOnlinePlayers().stream()
                    .map(CommandSource::getName)
                    .filter(n -> n.toLowerCase().startsWith(part1))
                    .collect(Collectors.toList());
            final Cause cause = Cause.of(EventContext.empty(), context.getSession().getPlayer());
            final TabCompleteEvent.Chat event = SpongeEventFactory.createTabCompleteEventChat(
                    cause, ImmutableList.copyOf(suggestions), suggestions, text, Optional.empty(), false);
            if (!Sponge.getEventManager().post(event)) {
                context.getSession().send(new MessagePlayOutTabComplete(suggestions));
            }
        }*/
    }
}
