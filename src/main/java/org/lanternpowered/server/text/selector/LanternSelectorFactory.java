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
package org.lanternpowered.server.text.selector;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.registry.type.text.SelectorTypeRegistryModule;
import org.lanternpowered.server.util.UncheckedExceptions;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scoreboard.Score;
import org.spongepowered.api.text.selector.Argument;
import org.spongepowered.api.text.selector.ArgumentHolder;
import org.spongepowered.api.text.selector.ArgumentHolder.Limit;
import org.spongepowered.api.text.selector.ArgumentType;
import org.spongepowered.api.text.selector.ArgumentTypes;
import org.spongepowered.api.text.selector.Selector;
import org.spongepowered.api.text.selector.SelectorFactory;
import org.spongepowered.api.text.selector.SelectorType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.annotation.Nullable;

public class LanternSelectorFactory implements SelectorFactory {

    private static final Pattern intListPattern = Pattern.compile("\\G([-!]?[\\w-]*)(?:$|,)");
    private static final Pattern keyValueListPattern = Pattern.compile("\\G(\\w+)=([-!]?[\\w-]*)(?:$|,)");
    private final static String argumentNamesLookup = "xyzr";

    public static <K, V> Function<K, V> methodAsFunction(final Method m, boolean isStatic) {
        if (isStatic) {
            return input -> {
                try {
                    return (V) m.invoke(null, input);
                } catch (IllegalAccessException e) {
                    Lantern.getLogger().debug(m + " wasn't public", e);
                    return null;
                } catch (IllegalArgumentException e) {
                    Lantern.getLogger().debug(m + " failed with parameter " + input, e);
                    return null;
                } catch (InvocationTargetException e) {
                    throw UncheckedExceptions.thrOw(e.getCause());
                }
            };
        } else {
            return input -> {
                try {
                    return (V) m.invoke(input);
                } catch (IllegalAccessException e) {
                    Lantern.getLogger().debug(m + " wasn't public", e);
                    return null;
                } catch (IllegalArgumentException e) {
                    Lantern.getLogger().debug(m + " failed with parameter " + input, e);
                    return null;
                } catch (InvocationTargetException e) {
                    throw UncheckedExceptions.thrOw(e.getCause());
                }
            };
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Optional<T> recast(Optional<?> source) {
        return (Optional<T>) source;
    }

    private final Map<String, ArgumentHolder.Limit<ArgumentType<Integer>>> scoreToTypeMap = Maps.newLinkedHashMap();
    private final Map<String, ArgumentType<?>> argumentLookupMap = Maps.newLinkedHashMap();
    private final SelectorTypeRegistryModule selectorTypeRegistry;

    public LanternSelectorFactory(SelectorTypeRegistryModule selectorTypeRegistry) {
        this.selectorTypeRegistry = selectorTypeRegistry;
    }

    @Override
    public Selector.Builder createBuilder() {
        return new LanternSelectorBuilder();
    }

    @Override
    public Selector parseRawSelector(String selector) {
        checkArgument(selector.startsWith("@"), "Invalid selector %s", selector);
        // If multi-character types are possible, this handles it
        int argListIndex = selector.indexOf('[');
        if (argListIndex < 0) {
            argListIndex = selector.length();
        } else {
            int end = selector.indexOf(']');
            checkArgument(end > argListIndex && selector.charAt(end - 1) != ',', "Invalid selector %s",
                    selector);
        }
        String typeStr = selector.substring(1, argListIndex);
        Optional<SelectorType> optSelectorType = this.selectorTypeRegistry.getById(typeStr);
        checkArgument(optSelectorType.isPresent(), "No type known as '%s'", typeStr);
        try {
            Map<String, String> rawMap;
            if (argListIndex == selector.length()) {
                rawMap = ImmutableMap.of();
            } else {
                rawMap = this.parseArgumentsMap(selector.substring(argListIndex + 1, selector.length() - 1));
            }
            Map<ArgumentType<?>, Argument<?>> arguments = parseArguments(rawMap);
            return new LanternSelector(optSelectorType.get(), ImmutableMap.copyOf(arguments));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid selector " + selector, e);
        }
    }

    @Override
    public Limit<ArgumentType<Integer>> createScoreArgumentType(String name) {
        if (!this.scoreToTypeMap.containsKey(name)) {
            LanternArgumentType<Integer> min = this.createArgumentType("score_" + name + "_min", Integer.class,
                    Score.class.getName());
            LanternArgumentType<Integer> max = this.createArgumentType("score_" + name, Integer.class,
                    Score.class.getName());
            this.scoreToTypeMap.put(name, new LanternArgumentHolder.LanternLimit<>(min, max));
        }
        return this.scoreToTypeMap.get(name);
    }

    @Override
    public Optional<ArgumentType<?>> getArgumentType(String name) {
        return recast(Optional.ofNullable(this.argumentLookupMap.get(name)));
    }

    @Override
    public Collection<ArgumentType<?>> getArgumentTypes() {
        return this.argumentLookupMap.values();
    }

    @Override
    public LanternArgumentType<String> createArgumentType(String key) {
        return this.createArgumentType(key, String.class);
    }

    @Override
    public <T> LanternArgumentType<T> createArgumentType(String key, Class<T> type) {
        return this.createArgumentType(key, type, type.getName());
    }

    @SuppressWarnings("unchecked")
    public <T> LanternArgumentType<T> createArgumentType(String key, Class<T> type, String converterKey) {
        if (!this.argumentLookupMap.containsKey(key)) {
            this.argumentLookupMap.put(key, new LanternArgumentType<>(key, type, converterKey));
        }
        return (LanternArgumentType<T>) this.argumentLookupMap.get(key);
    }

    public LanternArgumentType.Invertible<String> createInvertibleArgumentType(String key) {
        return this.createInvertibleArgumentType(key, String.class);
    }

    public <T> LanternArgumentType.Invertible<T> createInvertibleArgumentType(String key, Class<T> type) {
        return this.createInvertibleArgumentType(key, type, type.getName());
    }

    @SuppressWarnings("unchecked")
    public <T> LanternArgumentType.Invertible<T> createInvertibleArgumentType(String key, Class<T> type,
            String converterKey) {
        if (!this.argumentLookupMap.containsKey(key)) {
            this.argumentLookupMap.put(key, new LanternArgumentType.Invertible<>(key, type, converterKey));
        }
        return (LanternArgumentType.Invertible<T>) this.argumentLookupMap.get(key);
    }

    @Override
    public <T> Argument<T> createArgument(ArgumentType<T> type, T value) {
        if (type instanceof ArgumentType.Invertible) {
            return this.createArgument((ArgumentType.Invertible<T>) type, value, false);
        }
        return new LanternArgument<>(type, value);
    }

    @Override
    public <T> Argument.Invertible<T> createArgument(ArgumentType.Invertible<T> type, T value, boolean inverted) {
        return new LanternArgument.Invertible<>(type, value, inverted);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, V> Set<Argument<T>> createArguments(
            ArgumentHolder<? extends ArgumentType<T>> type, V value) {
        Set<Argument<T>> set = Sets.newLinkedHashSet();
        if (type instanceof LanternArgumentHolder.LanternVector3) {
            Set<Function<V, T>> extractors = ((LanternArgumentHolder.LanternVector3<V, T>) type).extractFunctions();
            Set<? extends ArgumentType<T>> types = type.getTypes();
            Iterator<Function<V, T>> extIter = extractors.iterator();
            Iterator<? extends ArgumentType<T>> typeIter = types.iterator();
            while (extIter.hasNext() && typeIter.hasNext()) {
                Function<V, T> extractor = extIter.next();
                ArgumentType<T> subtype = typeIter.next();
                set.add(createArgument(subtype, extractor.apply(value)));
            }
        }
        return set;
    }

    @Override
    public Argument<?> parseArgument(String argument) throws IllegalArgumentException {
        String[] argBits = argument.split("=");
        LanternArgumentType<Object> type = this.getArgumentTypeWithChecks(argBits[0]);
        return this.parseArgumentCreateShared(type, argBits[1]);
    }

    public Map<ArgumentType<?>, Argument<?>> parseArguments(Map<String, String> argumentMap) {
        Map<ArgumentType<?>, Argument<?>> generated = Maps.newHashMapWithExpectedSize(argumentMap.size());
        for (Entry<String, String> argument : argumentMap.entrySet()) {
            String argKey = argument.getKey();
            LanternArgumentType<Object> type = this.getArgumentTypeWithChecks(argKey);
            String value = argument.getValue();
            generated.put(type, this.parseArgumentCreateShared(type, value));
        }
        return generated;
    }

    public Map<String, String> parseArgumentsMap(@Nullable String input) {
        final Map<String, String> map = new HashMap<>();
        if (input == null) {
            return map;
        }

        int index = 0;
        int end = -1;

        Matcher matcher = intListPattern.matcher(input);
        while (matcher.find()) {
            if (argumentNamesLookup.length() < index++ && matcher.group(1).length() > 0) {
                map.put(argumentNamesLookup.indexOf(index - 1) + "", matcher.group(2));
            }
            end = matcher.end();
        }

        if (end < input.length()) {
            matcher = keyValueListPattern.matcher(end == -1 ? input : input.substring(end));
            while (matcher.find()) {
                map.put(matcher.group(1), matcher.group(2));
            }
        }

        return map;
    }

    @SuppressWarnings("unchecked")
    private LanternArgumentType<Object> getArgumentTypeWithChecks(String argKey) {
        Optional<ArgumentType<?>> type = ArgumentTypes.valueOf(argKey);
        if (!type.isPresent()) {
            throw new IllegalArgumentException("Invalid argument key " + argKey);
        }
        ArgumentType<Object> unwrappedType = (ArgumentType<Object>) type.get();
        if (!(unwrappedType instanceof LanternArgumentType)) {
            // TODO handle convert generally?
            throw new IllegalStateException("Cannot convert from string: " + unwrappedType);
        }
        return (LanternArgumentType<Object>) unwrappedType;
    }

    @SuppressWarnings("unchecked")
    private Argument<?> parseArgumentCreateShared(LanternArgumentType<Object> type, String value) {
        Argument<?> created;
        if (type instanceof ArgumentType.Invertible && value.charAt(0) == '!') {
            created = this.createArgument((ArgumentType.Invertible<Object>) type, type.convert(value.substring(1)), true);
        } else {
            created = this.createArgument(type, type.convert(value));
        }
        return created;
    }

    @Override
    public List<String> complete(String selector) {
        if (!selector.startsWith("@") || selector.contains("]")) {
            return ImmutableList.of();
        }
        Stream<String> choices;
        if (!selector.contains("[")) {
            // No arguments yet
            choices = Sponge.getRegistry().getAllOf(SelectorType.class).stream().map(type -> "@" + type.getName());
        } else {
            int keyStart = Math.max(selector.indexOf("["), selector.lastIndexOf(",")) + 1;
            int valueStart = selector.lastIndexOf("=") + 1;
            final String prefix = selector.substring(Math.max(keyStart, valueStart));
            if (keyStart > valueStart) {
                // Tab completing key
                choices = ArgumentTypes.values().stream().map(ArgumentType::getKey);
            } else {
                // Tab completing value
                Optional<ArgumentType<?>> type = ArgumentTypes.valueOf(selector.substring(keyStart, valueStart - 1));
                if (!type.isPresent()) {
                    return ImmutableList.of();
                }
                // TODO How to get all the values of an argument type?
                return ImmutableList.of();
            }
            choices = choices.map(input -> prefix + input);
        }
        return choices.filter(choice -> choice.startsWith(selector)).collect(ImmutableList.toImmutableList());
    }

}
