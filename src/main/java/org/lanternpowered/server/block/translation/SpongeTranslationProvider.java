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
package org.lanternpowered.server.block.translation;

import org.lanternpowered.server.block.TranslationProvider;
import org.lanternpowered.server.block.state.BlockStateProperties;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.text.translation.TranslationManager;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;

import org.checkerframework.checker.nullness.qual.Nullable;

public class SpongeTranslationProvider implements TranslationProvider {

    private final Translation dryTranslation;
    private final Translation wetTranslation;

    public SpongeTranslationProvider() {
        final TranslationManager manager = Lantern.getRegistry().getTranslationManager();
        this.dryTranslation = manager.get("tile.sponge.dry.name");
        this.wetTranslation = manager.get("tile.sponge.wet.name");
    }

    @Override
    public Translation get(BlockState blockState, @Nullable Location location, @Nullable Direction face) {
        return blockState.getStateProperty(BlockStateProperties.IS_WET).get() ? this.wetTranslation : this.dryTranslation;
    }
}
