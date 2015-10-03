package org.lanternpowered.server.network.vanilla.message.handler.play;

import org.apache.commons.lang3.StringUtils;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.session.Session;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInChatMessage;
import org.lanternpowered.server.text.LegacyTextRepresentation;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.command.MessageSinkEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.sink.MessageSink;
import org.spongepowered.api.text.sink.MessageSinks;
import org.spongepowered.api.text.translation.Translation;

public final class HandlerPlayInChatMessage implements Handler<MessagePlayInChatMessage> {

    @Override
    public void handle(Session session, MessagePlayInChatMessage message) {
        Player player = session.getPlayer();
        String message0 = message.getMessage();
        String message1 = StringUtils.normalizeSpace(message0);
        if (!isAllowedString(message0)) {
            session.disconnect("Illegal characters in chat!");
            return;
        }
        if (message0.startsWith("/")) {
            LanternGame.get().getCommandDispatcher().process(player, message0);
        } else {
            Translation translation = LanternGame.get().getRegistry().getTranslationManager().get("chat.type.text");
            Object displayName = player.getName(); // TODO: player.getDisplayNameData().displayName().get();
            Text.Translatable text = Texts.builder(translation, displayName, message1).build();
            MessageSink sink = MessageSinks.toAll();
            MessageSinkEvent.Chat event = SpongeEventFactory.createMessageSinkEventChat(LanternGame.get(),
                    Cause.of(player), text, text, sink, sink);
            if (!LanternGame.get().getEventManager().post(event)) {
                event.getSink().sendMessage(event.getMessage());
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
        return character != LegacyTextRepresentation.DEFAULT_CHAR &&
                character >= ' ' && character != '\u007F';
    }
}
