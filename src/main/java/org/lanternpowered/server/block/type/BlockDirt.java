/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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
package org.lanternpowered.server.block.type;

import org.lanternpowered.server.block.LanternBlockType;
import org.lanternpowered.server.block.trait.LanternEnumTrait;
import org.lanternpowered.server.data.type.LanternDirtType;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.translation.Translation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.annotation.Nullable;

public final class BlockDirt extends LanternBlockType {

    @SuppressWarnings("unchecked")
    public static final BlockTrait<LanternDirtType> TYPE = LanternEnumTrait.of("variant", (Key) Keys.DIRT_TYPE, LanternDirtType.class);

    private final Map<LanternDirtType, Translation> translations = new HashMap<>();

    public BlockDirt(String pluginId, String identifier, @Nullable Function<BlockType, ItemType> itemTypeBuilder) {
        super(pluginId, identifier, itemTypeBuilder, TYPE);

        for (LanternDirtType dirtType : LanternDirtType.values()) {
            this.translations.put(dirtType, Lantern.getRegistry().getTranslationManager().get(
                    "tile." + this.getId().replace("minecraft:", "").replace(":", ".") + "." + dirtType.getId() + ".name"));
        }
    }

    @Override
    public Translation getTranslation(BlockState blockState) {
        final Optional<LanternDirtType> dirtType = blockState.getTraitValue(TYPE);
        if (dirtType.isPresent()) {
            return this.translations.get(dirtType.get());
        }
        return super.getTranslation(blockState);
    }
}
