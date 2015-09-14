package org.lanternpowered.server.world.gen.flat;

import java.util.List;

import org.lanternpowered.server.block.LanternBlocks;
import org.lanternpowered.server.world.biome.LanternBiomes;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.util.Coerce;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;

import com.flowpowered.math.GenericMath;
import com.google.common.collect.Lists;

public class FlatGeneratorSettingsParser {

    public static String toString(FlatGeneratorSettings settings) {
        StringBuilder builder = new StringBuilder();
        // The current version
        builder.append(3).append(";");

        List<FlatLayer> layers = settings.getLayers();
        for (int i = 0; i < layers.size(); i++) {
            if (i > 0) {
                builder.append(",");
            }
            FlatLayer layer = layers.get(i);
            int depth = layer.getDepth();
            if (depth > 1) {
                builder.append(depth).append('*');
            }
            BlockState block = layer.getBlockState();
            builder.append(block.getType().getId());
            int data = LanternBlocks.reg().getStateData(block);
            if (data > 0) {
                builder.append(':').append(data);
            }
        }

        builder.append(';').append(LanternBiomes.getId(settings.getBiomeType())).append(';');
        // TODO: Add structures
        return builder.toString();
    }

    public static FlatGeneratorSettings fromString(String value) {
        if (value == null) {
            return null;
        }
        String[] parts = value.split(";");
        int index = 0;
        int version = parts.length == 1 ? 0 : Coerce.asInteger(parts[index++]).or(0);
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
                int depth = parts1.length > 1 ? Coerce.asInteger(parts1[index1++]).or(1) : 1;
                if (version < 3) {
                    parts1 = parts1[index1].split(":", 2);
                    if (parts1.length > 1) {
                        blockData = Coerce.toInteger(parts[1]);
                    }
                    blockType = LanternBlocks.reg().getTypeByInternalId(Coerce.toInteger(parts[1]));
                } else {
                    parts1 = parts1[index1].split(":", 3);
                    String name = parts1.length > 1 ? parts1[0] + ':' + parts1[1] : parts1[0];
                    blockType = LanternBlocks.reg().get(name).orNull();
                    if (blockType == null) {
                        blockType = LanternBlocks.reg().get(parts1[0]).orNull();
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
                layers.add(new FlatLayer(LanternBlocks.reg().getStateByTypeAndData(blockType,
                        (byte) GenericMath.clamp(blockData, 0x0, 0xf)), depth));
            }
        }
        BiomeType biomeType = BiomeTypes.PLAINS;
        if (version > 0 && parts.length > index) {
            Integer biomeId = Coerce.asInteger(parts[index]).orNull();
            if (biomeId != null) {
                BiomeType biomeType0 = LanternBiomes.getById(biomeId);
                if (biomeType0 != null) {
                    biomeType = biomeType0;
                }
            }
        }
        // TODO: Add structures
        return new FlatGeneratorSettings(biomeType, layers);
    }
}
