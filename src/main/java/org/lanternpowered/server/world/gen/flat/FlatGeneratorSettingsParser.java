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
package org.lanternpowered.server.world.gen.flat;

import com.flowpowered.math.GenericMath;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.lanternpowered.server.game.registry.type.world.biome.BiomeRegistryModule;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.util.Coerce;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

final class FlatGeneratorSettingsParser {

    public static String toString(FlatGeneratorSettings settings) {
        // All the parts
        final List<Object> parts = new ArrayList<>();
        // The current version
        parts.add(3);

        // All the layers
        final List<String> layers = new ArrayList<>();
        settings.getLayers().forEach(layer -> {
            final StringBuilder builder = new StringBuilder();
            final int depth = layer.getDepth();
            // Only append the depth if needed
            if (depth > 1) {
                builder.append(depth).append('*');
            }
            final BlockState block = layer.getBlockState();
            // Append the block id
            builder.append(block.getType().getId());
            final int data = BlockRegistryModule.get().getStateData(block);
            // Only append the data if needed
            if (data > 0) {
                builder.append(':').append(data);
            }
            layers.add(builder.toString());
        });

        // Add the layers part
        parts.add(Joiner.on(',').join(layers));
        // Add the biome id part
        parts.add(BiomeRegistryModule.get().getInternalId(settings.getBiomeType()));

        final List<String> extraDataValues = new ArrayList<>();
        settings.getExtraData().getValues(false).entrySet().forEach(e -> {
            final Object value = e.getValue();
            if (value instanceof DataView) {
                final List<String> values = new ArrayList<>();
                ((DataView) value).getValues(false).entrySet().forEach(e1 -> {
                    final Object value1 = e1.getValue();
                    // Only integer numbers are currently supported
                    if (value instanceof Number) {
                        values.add(e1.getKey().getParts().get(0) + '=' + ((Number) value1).intValue());
                    }
                });
                final StringBuilder builder = new StringBuilder();
                builder.append(e.getKey().getParts().get(0));
                if (values.size() > 0) {
                    builder.append('(');
                    builder.append(Joiner.on(' ').join(values));
                    builder.append(')');
                }
                extraDataValues.add(builder.toString());
            }
        });

        if (!extraDataValues.isEmpty()) {
            parts.add(Joiner.on(',').join(extraDataValues));
        }

        return Joiner.on(';').join(parts);
    }

    @Nullable
    public static FlatGeneratorSettings fromString(@Nullable String value) {
        if (value == null) {
            return null;
        }

        // Split the value into parts
        final List<String> parts = Lists.newArrayList(Splitter.on(';').split(value));

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
        final String layersPart = parts.remove(0);

        // The parsed layers
        final List<FlatLayer> layers = new ArrayList<>();

        // Can be empty if there are no layers
        if (!layersPart.isEmpty()) {
            // The separator that can be used to create a layer
            // of x amount of blocks
            final char depthSeparator = version >= 3 ? '*' : 'x';
            Splitter.on(',').split(layersPart).forEach(s -> {
                // The block type
                BlockType blockType;
                // The data value (optional)
                int blockData = 0;
                // The depth of the layer
                int depth = 1;

                // The depth separated by the depth separator followed by the block state
                final List<String> parts1 = Lists.newArrayList(Splitter.on(depthSeparator).limit(2).split(s));
                if (parts1.size() > 1) {
                    final Optional<Integer> optDepth = Coerce.asInteger(parts1.remove(0));
                    if (optDepth.isPresent()) {
                        depth = GenericMath.clamp(optDepth.get(), 0, 255);
                        if (depth <= 0) {
                            // Skip to the next layer
                            return;
                        }
                    }
                }

                String blockStatePart = parts1.get(0);

                final int index = blockStatePart.lastIndexOf(':');
                if (index > 0) {
                    Optional<Integer> optData = Coerce.asInteger(blockStatePart.substring(index + 1));
                    if (optData.isPresent()) {
                        blockData = GenericMath.clamp(optData.get(), 0, 15);
                        blockStatePart = blockStatePart.substring(0, index);
                    }
                }

                // Try to parse the block id as internal (int) id
                final Optional<Integer> optId = Coerce.asInteger(blockStatePart);
                if (optId.isPresent()) {
                    blockType = BlockRegistryModule.get().getStateByInternalId(optId.get()).orElse(BlockTypes.STONE.getDefaultState()).getType();
                // Not an integer, try the catalog system
                } else {
                    blockType = BlockRegistryModule.get().getById(blockStatePart).orElse(BlockTypes.STONE);
                }

                layers.add(new FlatLayer(BlockRegistryModule.get().getStateByTypeAndData(blockType, (byte) blockData).get(), depth));
            });
        }

        // Try to parse the biome type if present
        BiomeType biomeType = BiomeTypes.PLAINS;

        if (!parts.isEmpty()) {
            final String biomePart = parts.remove(0);

            final Optional<Integer> optBiomeId = Coerce.asInteger(biomePart);
            final Optional<BiomeType> optBiome;
            if (optBiomeId.isPresent()) {
                optBiome = BiomeRegistryModule.get().getByInternalId(optBiomeId.get());
            } else {
                optBiome = BiomeRegistryModule.get().getById(biomePart);
            }
            if (optBiome.isPresent()) {
                biomeType = optBiome.get();
            }
        }

        // Extra data (like structures)
        final DataContainer extraData = DataContainer.createNew();

        if (!parts.isEmpty()) {
            final String extraPart = parts.remove(0);
            if (!extraPart.isEmpty()) {
                Splitter.on(',').split(extraPart).forEach(s -> {
                    String key = s;

                    // Check if there is extra data attached to the key
                    final int valuesIndex = s.indexOf('(');
                    if (valuesIndex != -1) {
                        // Separate the key from the values
                        key = s.substring(0, valuesIndex);

                        int endIndex = s.lastIndexOf(')');
                        if (endIndex == -1) {
                            endIndex = s.length();
                        }

                        // Get the values section from the string
                        s = s.substring(valuesIndex + 1, endIndex);

                        // Create the view to store the values
                        final DataView dataView = extraData.createView(DataQuery.of(key));
                        if (!s.isEmpty()) {
                            Splitter.on(' ').split(s).forEach(v -> {
                                final List<String> parts1 = Splitter.on('=').limit(2).splitToList(v);
                                // Must be greater then 1, otherwise it's invalid
                                if (parts1.size() > 1) {
                                    // Currently, only integer values seem to be supported
                                    dataView.set(DataQuery.of(parts1.get(0)), Coerce.toInteger(parts1.get(1)));
                                }
                            });
                        }
                    } else {
                        extraData.createView(DataQuery.of(key));
                    }
                });
            }
        }

        return new FlatGeneratorSettings(biomeType, layers, extraData);
    }

    private FlatGeneratorSettingsParser() {
    }

}
