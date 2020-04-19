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
package org.lanternpowered.server.block;

import org.lanternpowered.server.block.provider.BlockObjectProvider;
import org.spongepowered.api.block.trait.EnumTrait;
import org.spongepowered.api.text.translation.Translatable;
import org.spongepowered.api.text.translation.Translation;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

@FunctionalInterface
public interface TranslationProvider extends BlockObjectProvider<Translation> {

    static TranslationProvider of(Translation translation) {
        return (blockState, location, face) -> translation;
    }

    static <E extends Enum<E>> TranslationProvider of(EnumTrait<E> enumTrait, Function<E, Translation> lookupFunction) {
        final Map<E, Translation> translationLookupMap = new EnumMap<>(enumTrait.getValueClass());
        for (E enumValue : enumTrait.getPossibleValues()) {
            translationLookupMap.put(enumValue, lookupFunction.apply(enumValue));
        }
        return (blockState, location, face) -> translationLookupMap.get(blockState.getTraitValue(enumTrait)
                .orElseThrow(() -> new IllegalStateException("Unable to find the enum trait " + enumTrait.getKey() +
                        " for the block type " + blockState.getKey())));
    }

    static <E extends Enum<E> & Translatable> TranslationProvider of(EnumTrait<E> enumTrait) {
        return (blockState, location, face) -> blockState.getTraitValue(enumTrait)
                .orElseThrow(() -> new IllegalStateException("Unable to find the enum trait " + enumTrait.getKey() +
                        " for the block type " + blockState.getKey()))
                .getTranslation();
    }
}
