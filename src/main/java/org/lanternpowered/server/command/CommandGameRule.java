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
package org.lanternpowered.server.command;

import static org.lanternpowered.server.text.translation.TranslationHelper.t;

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.world.LanternWorldPropertiesOld;
import org.lanternpowered.server.world.rules.RuleDataTypes;
import org.lanternpowered.server.world.rules.RuleType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.StartsWithPredicate;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings({ "rawtypes", "unchecked" })
public final class CommandGameRule extends CommandProvider {

    public CommandGameRule() {
        super(2, "gamerule", "rule");
    }

    @Override
    public void completeSpec(PluginContainer pluginContainer, CommandSpec.Builder specBuilder) {
        final Collection<String> defaultRules = Sponge.getRegistry().getDefaultGameRules();
        final ThreadLocal<RuleType<?>> currentRule = new ThreadLocal<>();

        specBuilder
                .arguments(
                        GenericArguments.flags()
                                .valueFlag(GenericArguments.world(CommandHelper.WORLD_KEY), "-world", "w")
                                .buildWith(GenericArguments.none()),
                        new CommandElement(Text.of("rule")) {
                            @Nullable
                            @Override
                            protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
                                RuleType<?> ruleType = RuleType.getOrCreate(args.next(), RuleDataTypes.STRING, "");
                                currentRule.set(ruleType);
                                return ruleType;
                            }

                            @Override
                            public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
                                final String prefix = args.nextIfPresent().orElse("");
                                return defaultRules.stream().filter(new StartsWithPredicate(prefix)).collect(ImmutableList.toImmutableList());
                            }
                        },
                        new CommandElement(Text.of("value")) {
                            private final List<String> booleanRuleSuggestions = ImmutableList.of("true", "false");

                            @Nullable
                            @Override
                            protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
                                RuleType<?> ruleType = currentRule.get();
                                currentRule.remove();
                                try {
                                    return ruleType.getDataType().parse(args.next());
                                } catch (IllegalArgumentException e) {
                                    throw args.createError(t(e.getMessage()));
                                }
                            }

                            @Override
                            public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
                                RuleType<?> ruleType = context.<RuleType<?>>getOne("rule").get();
                                if (ruleType.getDataType() == RuleDataTypes.BOOLEAN) {
                                    // Just return the suggestions, there is no need to
                                    // match the first part of the string
                                    return this.booleanRuleSuggestions;
                                }
                                return Collections.emptyList();
                            }
                        }
                )
                .executor((src, args) -> {
                    WorldProperties world = CommandHelper.getWorldProperties(src, args);
                    Object value = args.getOne("value").get();
                    RuleType ruleType = args.<RuleType>getOne("rule").get();
                    ((LanternWorldPropertiesOld) world).getRules()
                            .getOrCreateRule(ruleType)
                            .setValue(value);
                    src.sendMessage(t("commands.gamerule.success", ruleType.getName(), ruleType.getDataType().serialize(value)));
                    return CommandResult.success();
                });
    }
}
