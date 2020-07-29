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
package org.lanternpowered.server.network.vanilla.packet.handler.play;

import com.google.common.collect.Lists;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.packet.handler.Handler;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientTabCompletePacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.TabCompletePacket;
import org.spongepowered.api.text.Text;

public final class HandlerPlayInTabComplete implements Handler<ClientTabCompletePacket> {

    @Override
    public void handle(NetworkContext context, ClientTabCompletePacket packet) {
        final String text = packet.getInput();
        final LanternPlayer player = context.getSession().getPlayer();
        player.sendMessage(Text.of("Received tab completion (" + packet.getId() + "): " + text));
        player.getConnection().send(new TabCompletePacket(Lists.newArrayList(
                new TabCompletePacket.Match("Avalue", null),
                new TabCompletePacket.Match("Btest", null),
                new TabCompletePacket.Match("Cwhy", null)), packet.getId(), 0, 20));

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
