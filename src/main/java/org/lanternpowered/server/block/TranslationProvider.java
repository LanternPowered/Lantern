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
