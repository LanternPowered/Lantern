package org.lanternpowered.server.command;

import java.util.List;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.ArgumentParseException;
import org.spongepowered.api.util.command.args.CommandArgs;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.args.CommandElement;

import com.google.common.collect.Lists;

/**
 * This argument will take the rest of the arguments buffer and
 * turn into a message. An example can be the kick command that
 * can kick a player with a specific reason.
 */
public final class ArgumentRemainingText extends CommandElement {

    private ArgumentRemainingText(Text key) {
        super(key);
    }

    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        Text text = Texts.of(args.getRaw().substring(args.getRawPosition()));
        // Move the position to the end
        while (args.hasNext()) {
            args.next();
        }
        return text;
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        return Lists.newArrayList();
    }

    public static final ArgumentRemainingText of(Text key) {
        return new ArgumentRemainingText(key);
    }
}
