/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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
package org.lanternpowered.server.world.gen.flat;

import com.flowpowered.math.GenericMath;
import com.google.common.collect.Lists;
import org.lanternpowered.server.game.registry.Registries;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.util.Coerce;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;

import java.util.List;

import javax.annotation.Nullable;

public final class FlatGeneratorSettingsParser {

    public static String toString(FlatGeneratorSettings settings) {
        StringBuilder builder = new StringBuilder();
        // The current version
        builder.append(3).append(";");

        List<FlatLayer> layers = settings.getLayers();
        for (int i = 0; i < layers.size(); i++) {
            if (i > 0) {
                builder.append(",");
            }
            final FlatLayer layer = layers.get(i);
            final int depth = layer.getDepth();
            if (depth > 1) {
                builder.append(depth).append('*');
            }
            final BlockState block = layer.getBlockState();
            builder.append(block.getType().getId());
            final int data = Registries.getBlockRegistry().getStateData(block);
            if (data > 0) {
                builder.append(':').append(data);
            }
        }

        builder.append(';').append(Registries.getBiomeRegistry().getInternalId(settings.getBiomeType())).append(';');
        // TODO: Add structures
        return builder.toString();
    }

    @Nullable
    public static FlatGeneratorSettings fromString(@Nullable String value) {
        if (value == null) {
            return null;
        }
        String[] parts = value.split(";");
        int index = 0;
        int version = parts.length == 1 ? 0 : Coerce.asInteger(parts[index++]).orElse(0);
        if (version < 0 || version > 3) {
            return null;
        }
        String layersPart = parts[index++];
        List<FlatLayer> layers = Lists.newArrayList();
        if (!layersPart.isEmpty()) {
            String[] layerParts = layersPart.split(",");
            for (String layerPart : layerParts) {
                String[] parts1 = version >= 3 ? layerPart.split("\\*", 2) : layerPart.split("x", 2);
                BlockType blockType = null;
                int blockData = 0;
                int index1 = 0;
                int depth = parts1.length > 1 ? Coerce.asInteger(parts1[index1++]).orElse(1) : 1;
                if (version < 3) {
                    parts1 = parts1[index1].split(":", 2);
                    if (parts1.length > 1) {
                        blockData = Coerce.toInteger(parts[1]);
                    }
                    blockType = Registries.getBlockRegistry().getStateByInternalId(Coerce.toInteger(parts[1]))
                            .orElse(BlockTypes.AIR.getDefaultState()).getType();
                } else {
                    parts1 = parts1[index1].split(":", 3);
                    String name = parts1.length > 1 ? parts1[0] + ':' + parts1[1] : parts1[0];
                    blockType = Registries.getBlockRegistry().getById(name).orElse(BlockTypes.AIR);
                    if (blockType == null) {
                        blockType = Registries.getBlockRegistry().getById(parts1[0]).orElse(null);
                        if (parts1.length > 1) {
                            blockData = Coerce.toInteger(parts1[1]);
                        }
                    } else if (parts1.length > 2) {
                        blockData = Coerce.toInteger(parts1[2]);
                    }
                }
                if (blockType == null) {
                    return null;
                }
                layers.add(new FlatLayer(Registries.getBlockRegistry().getStateByTypeAndData(
                        blockType, (byte) GenericMath.clamp(blockData, 0x0, 0xff)).orElse(BlockTypes.AIR.getDefaultState()), depth));
            }
        }
        BiomeType biomeType = BiomeTypes.PLAINS;
        if (version > 0 && parts.length > index) {
            Integer biomeId = Coerce.asInteger(parts[index]).orElse(null);
            if (biomeId != null) {
                BiomeType biomeType0 = Registries.getBiomeRegistry().getByInternalId(biomeId).orElse(BiomeTypes.OCEAN);
                if (biomeType0 != null) {
                    biomeType = biomeType0;
                }
            }
        }
        // TODO: Add structures
        return new FlatGeneratorSettings(biomeType, layers);
    }

    private FlatGeneratorSettingsParser() {
    }

}
