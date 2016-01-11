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
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.lanternpowered.server.game.registry.Registries;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.util.Coerce;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

public final class FlatGeneratorSettingsParser {

    public static String toString(FlatGeneratorSettings settings) {
        // All the parts
        List<Object> parts = Lists.newArrayList();
        // The current version
        parts.add(3);

        // All the layers
        List<String> layers = Lists.newArrayList();
        settings.getLayers().forEach(layer -> {
            StringBuilder builder = new StringBuilder();
            int depth = layer.getDepth();
            // Only append the depth if needed
            if (depth > 1) {
                builder.append(depth).append('*');
            }
            BlockState block = layer.getBlockState();
            // Append the block id
            builder.append(block.getType().getId());
            int data = Registries.getBlockRegistry().getStateData(block);
            // Only append the data if needed
            if (data > 0) {
                builder.append(':').append(data);
            }
            layers.add(builder.toString());
        });

        // Add the layers part
        parts.add(Joiner.on(',').join(layers));
        // Add the biome id part
        parts.add(Registries.getBiomeRegistry().getInternalId(settings.getBiomeType()));

        // TODO: Add structures

        return Joiner.on(';').join(parts);
    }

    @Nullable
    public static FlatGeneratorSettings fromString(@Nullable String value) {
        if (value == null) {
            return null;
        }

        // Split the value into parts
        List<String> parts = Splitter.on(';').splitToList(value);

        // Try to extract the version from the parts
        int version = 0;
        if (parts.size() > 1) {
            version = Coerce.toInteger(parts.remove(0));
        }

        // Smaller then 0 is unknown? and 3 is the latest format version
        if (version < 0 || version > 3) {
            return null;
        }

        // The layers are stored in the first part
        String layersPart = parts.remove(0);

        // The parsed layers
        List<FlatLayer> layers = Lists.newArrayList();

        // Can be empty if there are no layers
        if (!layersPart.isEmpty()) {
            // The seperator that can be used to create a layer
            // of x amount of blocks
            final String depthSeperator = version >= 3 ? "\\*" : "x";
            Splitter.on(',').split(layersPart).forEach(s -> {
                // The block type
                BlockType blockType;
                // The data value (optional)
                int blockData = 0;
                // The depth of the layer
                int depth = 1;

                // The depth seperated by the depth seperator followed by the block state
                List<String> parts1 = Splitter.on(depthSeperator).limit(2).splitToList(value);
                if (parts1.size() > 1) {
                    Optional<Integer> optDepth = Coerce.asInteger(parts1.remove(0));
                    if (optDepth.isPresent()) {
                        depth = GenericMath.clamp(optDepth.get(), 0, 255);
                    }
                }

                String blockStatePart = parts1.get(0);

                int index = blockStatePart.lastIndexOf(':');
                if (index > 0) {
                    Optional<Integer> optData = Coerce.asInteger(blockStatePart.substring(index + 1));
                    if (optData.isPresent()) {
                        blockData = GenericMath.clamp(optData.get(), 0, 15);
                        blockStatePart = blockStatePart.substring(0, index);
                    }
                }

                // Try to parse the block id as internal (int) id
                Optional<Integer> optId = Coerce.asInteger(blockStatePart);
                if (optId.isPresent()) {
                    blockType = Registries.getBlockRegistry().getStateByInternalId(optId.get()).orElse(BlockTypes.STONE.getDefaultState()).getType();
                // Not an integer, try the catalog system
                } else {
                    blockType = Registries.getBlockRegistry().getById(blockStatePart).orElse(BlockTypes.STONE);
                }

                layers.add(new FlatLayer(Registries.getBlockRegistry().getStateByTypeAndData(blockType, (byte) blockData).get(), depth));
            });
        }

        // Try to parse the biome type if present
        BiomeType biomeType = BiomeTypes.PLAINS;

        if (!parts.isEmpty()) {
            String biomePart = parts.remove(0);

            Optional<Integer> optBiomeId = Coerce.asInteger(biomePart);
            Optional<BiomeType> optBiome;
            if (optBiomeId.isPresent()) {
                optBiome = Registries.getBiomeRegistry().getByInternalId(optBiomeId.get());
            } else {
                optBiome = Registries.getBiomeRegistry().getById(biomePart);
            }
            if (optBiome.isPresent()) {
                biomeType = optBiome.get();
            }
        }

        // TODO: Parse structures

        return new FlatGeneratorSettings(biomeType, layers);
    }

    private FlatGeneratorSettingsParser() {
    }

}
