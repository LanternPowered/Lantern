package org.lanternpowered.server.text.selector;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import org.lanternpowered.server.catalog.CatalogTypeRegistry;
import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.scoreboard.Score;
import org.spongepowered.api.text.selector.Argument;
import org.spongepowered.api.text.selector.ArgumentHolder;
import org.spongepowered.api.text.selector.ArgumentHolder.Limit;
import org.spongepowered.api.text.selector.ArgumentType;
import org.spongepowered.api.text.selector.ArgumentTypes;
import org.spongepowered.api.text.selector.Selector;
import org.spongepowered.api.text.selector.SelectorBuilder;
import org.spongepowered.api.text.selector.SelectorFactory;
import org.spongepowered.api.text.selector.SelectorType;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NonnullByDefault
public class LanternSelectorFactory implements SelectorFactory {

    private static final Pattern intListPattern = Pattern.compile("\\G([-!]?[\\w-]*)(?:$|,)");
    private static final Pattern keyValueListPattern = Pattern.compile("\\G(\\w+)=([-!]?[\\w-]*)(?:$|,)");
    private final static String argumentNamesLookup = "xyzr";

    public static <K, V> Function<K, V> methodAsFunction(final Method m, boolean isStatic) {
        if (isStatic) {
            return new Function<K, V>() {

                @SuppressWarnings("unchecked")
                @Override
                public V apply(K input) {
                    try {
                        return (V) m.invoke(null, input);
                    } catch (IllegalAccessException e) {
                        LanternGame.log().debug(m + " wasn't public", e);
                        return null;
                    } catch (IllegalArgumentException e) {
                        LanternGame.log().debug(m + " failed with paramter " + input, e);
                        return null;
                    } catch (InvocationTargetException e) {
                        throw Throwables.propagate(e.getCause());
                    }
                }

            };
        } else {
            return new Function<K, V>() {

                @SuppressWarnings("unchecked")
                @Override
                public V apply(K input) {
                    try {
                        return (V) m.invoke(input);
                    } catch (IllegalAccessException e) {
                        LanternGame.log().debug(m + " wasn't public", e);
                        return null;
                    } catch (IllegalArgumentException e) {
                        LanternGame.log().debug(m + " failed with paramter " + input, e);
                        return null;
                    } catch (InvocationTargetException e) {
                        throw Throwables.propagate(e.getCause());
                    }
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
    private final CatalogTypeRegistry<SelectorType> selectorTypeRegistry;

    public LanternSelectorFactory(CatalogTypeRegistry<SelectorType> selectorTypeRegistry) {
        this.selectorTypeRegistry = selectorTypeRegistry;
    }

    @Override
    public SelectorBuilder createBuilder(SelectorType type) {
        return new LanternSelectorBuilder(type);
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
        checkArgument(this.selectorTypeRegistry.has(typeStr), "No type known as '%s'", typeStr);
        SelectorType type = this.selectorTypeRegistry.get(typeStr).get();
        try {
            Map<String, String> rawMap;
            if (argListIndex == selector.length()) {
                rawMap = ImmutableMap.of();
            } else {
                rawMap = this.parseArgumentsMap(selector.substring(argListIndex + 1, selector.length() - 1));
            }
            Map<ArgumentType<?>, Argument<?>> arguments = parseArguments(rawMap);
            return new LanternSelector(type, ImmutableMap.copyOf(arguments));
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
            this.scoreToTypeMap.put(name, new LanternArgumentHolder.LanternLimit<ArgumentType<Integer>>(min, max));
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
            this.argumentLookupMap.put(key, new LanternArgumentType<T>(key, type, converterKey));
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
            this.argumentLookupMap.put(key, new LanternArgumentType.Invertible<T>(key, type, converterKey));
        }
        return (LanternArgumentType.Invertible<T>) this.argumentLookupMap.get(key);
    }

    @Override
    public <T> Argument<T> createArgument(ArgumentType<T> type, T value) {
        if (type instanceof ArgumentType.Invertible) {
            return this.createArgument((ArgumentType.Invertible<T>) type, value, false);
        }
        return new LanternArgument<T>(type, value);
    }

    @Override
    public <T> Argument.Invertible<T> createArgument(ArgumentType.Invertible<T> type, T value, boolean inverted) {
        return new LanternArgument.Invertible<T>(type, value, inverted);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, V> Set<Argument<T>> createArguments(
            ArgumentHolder<? extends ArgumentType<T>> type, V value) {
        Set<Argument<T>> set = Sets.newLinkedHashSet();
        if (type instanceof LanternArgumentHolder.LanternVector3) {
            Set<Function<V, T>> extractors = ((LanternArgumentHolder.LanternVector3<V, T>) (Object) type).extractFunctions();
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

    public Map<String, String> parseArgumentsMap(String input) {
        Map<String, String> map = Maps.newHashMap();
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
}
