/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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
package org.lanternpowered.server.effect.potion;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.spongepowered.api.util.Color;

public final class PotionColorTable {

    // The version of the cache file (to update when new mc potion types are added)
    static final int VERSION = 1;

    /**
     * Loads the potion color table from the input stream.
     * 
     * @param is the input stream
     * @return the color table
     * @throws IOException
     */
    public static PotionColorTable load(InputStream is) throws IOException {
        final DataInputStream dis = new DataInputStream(new GZIPInputStream(is));
        final int[][] colorTable = new int[64 * 64 * 64][];
        @SuppressWarnings("unused")
        int version = dis.readInt();
        for (int r = 0; r < 64; r++) {
            for (int g = 0; g < 64; g++) {
                for (int b = 0; b < 64; b++) {
                    int rgb = r << 12 | g << 6 | b;
                    int[] ids = new int[dis.readByte()];
                    for (int i = 0; i < ids.length; i++) {
                        ids[i] = dis.readByte();
                    }
                    colorTable[rgb] = ids;
                }
            }
        }
        dis.close();
        return new PotionColorTable(colorTable);
    }

    private final int[][] colorTable;

    private PotionColorTable(int[][] colorTable) {
        this.colorTable = colorTable;
    }

    /**
     * Gets all the potion ids that are required for the color, the
     * array may contain duplicates.
     * 
     * @param color the color
     * @return the potion ids
     */
    public int[] getPotionIds(Color color) {
        return this.getPotionIds(color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * Gets all the potion ids that are required for the color (rgb), the
     * array may contain duplicates.
     * 
     * @param red the red color component
     * @param green the green color component
     * @param blue the blue color component
     * @return the potion ids
     */
    public int[] getPotionIds(int red, int green, int blue) {
        if (red > 255 || green > 255 || blue > 255 || red < 0 || green < 0 || blue < 0) {
            throw new IndexOutOfBoundsException();
        }
        return this.colorTable[(red / 4) << 12 | (green / 4) << 6 | (blue / 4)].clone();
    }

    /**
     * Gets all the potion ids that are required for the color (rgb), the
     * array may contain duplicates.
     * 
     * @param rgb the color
     * @return the potion ids
     */
    public int[] getPotionIds(int rgb) {
        return this.getPotionIds((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff);
    }
}
