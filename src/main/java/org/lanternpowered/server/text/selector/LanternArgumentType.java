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
package org.lanternpowered.server.text.selector;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Maps;
import com.google.common.primitives.Primitives;
import org.lanternpowered.lmbda.LambdaFactory;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.registry.type.entity.EntityTypeRegistryModule;
import org.lanternpowered.server.registry.type.data.GameModeRegistry;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.text.selector.ArgumentType;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Function;

public class LanternArgumentType<T> extends LanternArgumentHolder<ArgumentType<T>> implements ArgumentType<T> {

    private static final Map<String, Function<String, ?>> converters = Maps.newHashMap();

    static {
        converters.put(String.class.getName(), Function.identity());
        converters.put(EntityType.class.getName(), (Function<String, EntityType>) input ->
                EntityTypeRegistryModule.INSTANCE.get(ResourceKey.resolve(input.toLowerCase())).orElse(null));
        converters.put(GameMode.class.getName(), input -> {
            switch (input) {
                case "s": return GameModes.SURVIVAL;
                case "c": return GameModes.CREATIVE;
                case "a": return GameModes.ADVENTURE;
                case "sp": return GameModes.SPECTATOR;
            }
            try {
                final int i = Integer.parseInt(input);
                return GameModeRegistry.get().getOptional(i).orElseGet(GameModes.NOT_SET);
            } catch (NumberFormatException e) {
                return GameModeRegistry.get().getOptional(ResourceKey.resolve(input)).orElseGet(GameModes.NOT_SET);
            }
        });
    }

    @SuppressWarnings("unchecked")
    static <T> Function<String, T> getConverter(Class<T> type, String converterKey) {
        if (!converters.containsKey(converterKey)) {
            try {
                final Method valueOf = Primitives.wrap(type).getMethod("valueOf", String.class);
                converters.put(converterKey, LambdaFactory.createFunction(MethodHandles.publicLookup().unreflect(valueOf)));
            } catch (NoSuchMethodException ignored) {
                if (CatalogType.class.isAssignableFrom(type)) {
                    final Class<? extends CatalogType> type2 = type.asSubclass(CatalogType.class);
                    converters.put(converterKey, (Function<String, T>) input -> {
                        // assume it exists for now
                        return (T) Lantern.getGame().getRegistry().getType(type2, ResourceKey.resolve(input)).get();
                    });
                } else {
                    throw new IllegalStateException("Can't convert " + type);
                }
            } catch (Exception e) {
                Lantern.getLogger().warn("Unable to create converter for: " + type, e);
            }
        }
        return (Function<String, T>) converters.get(converterKey);
    }

    private final String key;
    private final Function<String, T> converter;

    public LanternArgumentType(String key, Class<T> type) {
        this(key, type, type.getName());
    }

    public LanternArgumentType(String key, Class<T> type, String converterKey) {
        this(key, getConverter(type, converterKey));
    }

    public LanternArgumentType(String key, Function<String, T> converter) {
        this.key = checkNotNull(key);
        this.converter = checkNotNull(converter);
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public String toString() {
        return getKey();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ArgumentType && ((ArgumentType<?>) obj).getKey().equals(getKey());
    }

    @Override
    public int hashCode() {
        return getKey().hashCode();
    }

    protected T convert(String s) {
        return this.converter.apply(s);
    }

    public static class Invertible<T> extends LanternArgumentType<T> implements ArgumentType.Invertible<T> {

        public Invertible(String key, Class<T> type) {
            super(key, type);
        }

        public Invertible(String key, Class<T> type, String converterKey) {
            super(key, getConverter(type, converterKey));
        }

        public Invertible(String key, Function<String, T> converter) {
            super(key, converter);
        }

    }
}
