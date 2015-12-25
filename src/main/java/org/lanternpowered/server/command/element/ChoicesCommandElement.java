/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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
package org.lanternpowered.server.command.element;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.spongepowered.api.command.CommandMessageFormatting;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.GuavaCollectors;
import org.spongepowered.api.util.StartsWithPredicate;
import static org.spongepowered.api.util.SpongeApiTranslationHelper.t;

public final class ChoicesCommandElement extends CommandElement {

    /**
     * Return an argument that allows selecting from a limited set of values.
     * Unless {@code choicesInUsage} is true, general command usage will only
     * display the provided key
     *
     * @param key The key to store the resulting value under
     * @param choices The choices the users can choose from
     * @param choicesInUsage Whether to display the available choices, or simply
     *        the provided key, as part of usage
     * @return the element to match the input
     */
    public static CommandElement of(Text key, Map<String, Object> choices, boolean choicesInUsage) {
        return new ChoicesCommandElement(key, src -> choices, choicesInUsage);
    }

    /**
     * Return an argument that allows selecting from a limited set of values.
     * Unless {@code choicesInUsage} is true, general command usage will only
     * display the provided key
     *
     * @param key The key to store the resulting value under
     * @param choicesSupplier The supplier that gets the choices the users can
     *        choose from
     * @param choicesInUsage Whether to display the available choices, or simply
     *        the provided key, as part of usage
     * @return the element to match the input
     */
    public static CommandElement ofSupplier(Text key, Supplier<Map<String, Object>> choicesSupplier,
            boolean choicesInUsage) {
        return new ChoicesCommandElement(key, src -> choicesSupplier.get(), choicesInUsage);
    }

    /**
     * Return an argument that allows selecting from a limited set of values.
     * Unless {@code choicesInUsage} is true, general command usage will only
     * display the provided key
     *
     * @param key The key to store the resulting value under
     * @param choicesFunction The function that gets the choices a specific
     *        users can choose from
     * @param choicesInUsage Whether to display the available choices, or simply
     *        the provided key, as part of usage
     * @return the element to match the input
     */
    public static CommandElement ofFunction(Text key, Function<CommandSource, Map<String, Object>> choicesFunction,
            boolean choicesInUsage) {
        return new ChoicesCommandElement(key, choicesFunction, choicesInUsage);
    }

    private final Function<CommandSource, Map<String, Object>> choicesFunction;
    private final boolean choicesInUsage;

    private ChoicesCommandElement(Text key, Function<CommandSource, Map<String, Object>> choicesFunction,
            boolean choicesInUsage) {
        super(key);
        this.choicesFunction = choicesFunction;
        this.choicesInUsage = choicesInUsage;
    }

    @Override
    public Object parseValue(CommandSource src, CommandArgs args) throws ArgumentParseException {
        Object value = this.choicesFunction.apply(src).get(args.next());
        if (value == null) {
            throw args.createError(t("Argument was not a valid choice. Valid choices: %s",
                    this.choicesFunction.apply(src).keySet().toString()));
        }
        return value;
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        final String prefix = args.nextIfPresent().orElse("");
        return this.choicesFunction.apply(src).keySet().stream().filter(new StartsWithPredicate(prefix))
                .collect(GuavaCollectors.toImmutableList());
    }

    @Override
    public Text getUsage(CommandSource src) {
        if (this.choicesInUsage) {
            final TextBuilder build = Texts.builder();
            build.append(CommandMessageFormatting.LT_TEXT);
            for (Iterator<String> it = this.choicesFunction.apply(src).keySet().iterator(); it.hasNext();) {
                build.append(Texts.of(it.next()));
                if (it.hasNext()) {
                    build.append(CommandMessageFormatting.PIPE_TEXT);
                }
            }
            build.append(CommandMessageFormatting.GT_TEXT);
            return build.build();
        } else {
            return super.getUsage(src);
        }
    }
}
