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
package org.lanternpowered.server.command.element;

import static org.spongepowered.api.util.SpongeApiTranslationHelper.t;

import com.google.common.collect.ImmutableList;
import org.spongepowered.api.command.CommandMessageFormatting;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.StartsWithPredicate;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nullable;

public final class ChoicesElement extends CommandElement {

    /**
     * Return an argument that allows selecting from a limited set of values.
     * Unless {@code choicesInUsage} is true, general command usage will only
     * display the provided key
     *
     * @param key The key to store the resulting value under
     * @param choices The choices the users can choose from
     * @param caseSensitive Whether the choices should be case sensitive
     * @param choicesInUsage Whether to display the available choices, or simply
     *        the provided key, as part of usage
     * @return the element to match the input
     */
    public static CommandElement of(Text key, Map<String, Object> choices, boolean caseSensitive,
            boolean choicesInUsage) {
        return new ChoicesElement(key, src -> choices, null, caseSensitive, choicesInUsage);
    }

    /**
     * Return an argument that allows selecting from a limited set of values.
     * Unless {@code choicesInUsage} is true, general command usage will only
     * display the provided key
     *
     * @param key The key to store the resulting value under
     * @param choices The choices the users can choose from
     * @param aliasesChoices This allows there to be aliases attached that
     *        will not be visible when using the tab completation but are still usable
     * @param caseSensitive Whether the choices should be case sensitive
     * @param choicesInUsage Whether to display the available choices, or simply
     *        the provided key, as part of usage
     * @return the element to match the input
     */
    public static CommandElement of(Text key, Map<String, Object> choices,
            @Nullable Map<String, Object> aliasesChoices, boolean caseSensitive, boolean choicesInUsage) {
        return new ChoicesElement(key, src -> choices, aliasesChoices == null ? null :
            src -> aliasesChoices, caseSensitive, choicesInUsage);
    }

    /**
     * Return an argument that allows selecting from a limited set of values.
     * Unless {@code choicesInUsage} is true, general command usage will only
     * display the provided key
     *
     * @param key The key to store the resulting value under
     * @param choicesSupplier The supplier that gets the choices the users can
     *        choose from
     * @param caseSensitive Whether the choices should be case sensitive
     * @param choicesInUsage Whether to display the available choices, or simply
     *        the provided key, as part of usage
     * @return the element to match the input
     */
    public static CommandElement ofSupplier(Text key, Supplier<Map<String, Object>> choicesSupplier,
            boolean caseSensitive, boolean choicesInUsage) {
        return new ChoicesElement(key, src -> choicesSupplier.get(), null, caseSensitive, choicesInUsage);
    }

    /**
     * Return an argument that allows selecting from a limited set of values.
     * Unless {@code choicesInUsage} is true, general command usage will only
     * display the provided key
     *
     * @param key The key to store the resulting value under
     * @param choicesSupplier The supplier that gets the choices the users can
     *        choose from
     * @param aliasesChoicesSupplier This supplier allows there to be aliases attached that
     *        will not be visible when using the tab completation but are still usable
     * @param caseSensitive Whether the choices should be case sensitive
     * @param choicesInUsage Whether to display the available choices, or simply
     *        the provided key, as part of usage
     * @return the element to match the input
     */
    public static CommandElement ofSupplier(Text key, Supplier<Map<String, Object>> choicesSupplier,
            @Nullable Supplier<Map<String, Object>> aliasesChoicesSupplier,
            boolean caseSensitive, boolean choicesInUsage) {
        return new ChoicesElement(key, src -> choicesSupplier.get(), aliasesChoicesSupplier == null ? null :
            src -> aliasesChoicesSupplier.get(), caseSensitive, choicesInUsage);
    }

    /**
     * Return an argument that allows selecting from a limited set of values.
     * Unless {@code choicesInUsage} is true, general command usage will only
     * display the provided key
     *
     * @param key The key to store the resulting value under
     * @param choicesFunction The function that gets the choices a specific
     *        users can choose from
     * @param caseSensitive Whether the choices should be case sensitive
     * @param choicesInUsage Whether to display the available choices, or simply
     *        the provided key, as part of usage
     * @return the element to match the input
     */
    public static CommandElement ofFunction(Text key, Function<CommandSource, Map<String, Object>> choicesFunction,
            boolean caseSensitive, boolean choicesInUsage) {
        return new ChoicesElement(key, choicesFunction, null, caseSensitive, choicesInUsage);
    }

    /**
     * Return an argument that allows selecting from a limited set of values.
     * Unless {@code choicesInUsage} is true, general command usage will only
     * display the provided key
     *
     * @param key The key to store the resulting value under
     * @param choicesFunction The function that gets the choices a specific
     *        users can choose from
     * @param aliasesChoicesFunction This function allows there to be aliases attached that
     *        will not be visible when using the tab completation but are still usable
     * @param caseSensitive Whether the choices should be case sensitive
     * @param choicesInUsage Whether to display the available choices, or simply
     *        the provided key, as part of usage
     * @return the element to match the input
     */
    public static CommandElement ofFunction(Text key, Function<CommandSource, Map<String, Object>> choicesFunction,
            @Nullable Function<CommandSource, Map<String, Object>> aliasesChoicesFunction,
            boolean caseSensitive, boolean choicesInUsage) {
        return new ChoicesElement(key, choicesFunction, aliasesChoicesFunction, caseSensitive, choicesInUsage);
    }

    @Nullable private final Function<CommandSource, Map<String, Object>> aliasesChoicesFunction;
    private final Function<CommandSource, Map<String, Object>> choicesFunction;
    private final boolean choicesInUsage;
    private final boolean caseSensitive;

    private ChoicesElement(Text key, Function<CommandSource, Map<String, Object>> choicesFunction,
            @Nullable Function<CommandSource, Map<String, Object>> aliasesChoicesFunction,
            boolean caseSensitive, boolean choicesInUsage) {
        super(key);
        this.aliasesChoicesFunction = aliasesChoicesFunction;
        this.choicesFunction = choicesFunction;
        this.choicesInUsage = choicesInUsage;
        this.caseSensitive = caseSensitive;
    }

    @Override
    public Object parseValue(CommandSource src, CommandArgs args) throws ArgumentParseException {
        String key = args.next();
        if (!this.caseSensitive) {
            key = key.toLowerCase();
        }
        // TODO: Force the choices to be lowercase?
        Object value = this.choicesFunction.apply(src).get(key);
        if (this.aliasesChoicesFunction != null && value == null) {
            value = this.aliasesChoicesFunction.apply(src).get(key);
        }
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
                .collect(ImmutableList.toImmutableList());
    }

    @Override
    public Text getUsage(CommandSource src) {
        if (this.choicesInUsage) {
            final Text.Builder build = Text.builder();
            build.append(CommandMessageFormatting.LT_TEXT);
            for (Iterator<String> it = this.choicesFunction.apply(src).keySet().iterator(); it.hasNext();) {
                build.append(Text.of(it.next()));
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
