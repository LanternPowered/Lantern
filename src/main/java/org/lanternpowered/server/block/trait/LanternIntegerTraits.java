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
package org.lanternpowered.server.block.trait;

import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.data.type.LanternNotePitch;
import org.lanternpowered.server.game.registry.type.data.NotePitchRegistryModule;
import org.lanternpowered.server.state.LanternIntStateProperty;
import org.spongepowered.api.block.trait.IntegerTrait;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.NotePitch;

public final class LanternIntegerTraits {

    public static final IntegerTrait SAPLING_GROWTH_STAGE = LanternIntStateProperty.Companion.minecraft("stage", Keys.GROWTH_STAGE, 0, 1);

    public static final IntegerTrait POWER = LanternIntStateProperty.Companion.minecraftRange("power", Keys.POWER, 0, 15);

    public static final IntegerTrait MOISTURE = LanternIntStateProperty.Companion.minecraftRange("moisture", Keys.MOISTURE, 0, 7);

    public static final IntegerTrait ROTATION = LanternIntStateProperty.Companion.minecraftRange("rotation", LanternKeys.FINE_ROTATION, 0, 15);

    public static final IntegerTrait NOTE = LanternIntStateProperty.Companion.minecraftRangeTransformed("note", Keys.NOTE_PITCH,
            new KeyTraitValueTransformer<Integer, NotePitch>() {
                @Override
                public NotePitch toKeyValue(Integer traitValue) {
                    return NotePitchRegistryModule.get().getByInternalId(traitValue).get();
                }
                @Override
                public Integer toTraitValue(NotePitch keyValue) {
                    return ((LanternNotePitch) keyValue).getInternalId();
                }
            }, 0, 24);

    public static final IntegerTrait DECAY_DISTANCE = LanternIntStateProperty.Companion.minecraftRange("moisture", Keys.MOISTURE, 1, 7);

    private LanternIntegerTraits() {
    }
}
