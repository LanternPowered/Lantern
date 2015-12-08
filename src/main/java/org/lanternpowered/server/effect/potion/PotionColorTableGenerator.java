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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.zip.GZIPOutputStream;

import com.google.common.collect.Lists;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public final class PotionColorTableGenerator {

    // The maximum amount of colors that may be combined
    private static final int MAX_COLOR_DEPTH = 7;

    // The amount of actions that should run parallel
    private static final int PARALLEL_ACTIONS_COUNT = 10;

    // The devision that we applied to limit the amount of colors
    private static final int DIVISION = 4;

    // The devision that we applied to limit the amount of colors
    private static final int MAX_COMPOUND_VALUE = 256 / DIVISION;

    // The compound mask
    private static final int COMPOUND_MASK = MAX_COMPOUND_VALUE - 1;

    // The amount of bits in each compound
    private static final int COMPOUND_BITS = Integer.bitCount(COMPOUND_MASK);

    // The double amount of bits in each compound
    private static final int DOUBLE_COMPOUND_BITS = 2 * COMPOUND_BITS;

    /**
     * Generates a potion color table file, this process can take a long time
     * because it will try to search for all the potion color combinations to
     * match a rgb color, missing ones will be rounded to the closest ones.
     * 
     * <p>Launch options:</p>
     * <table>
     *   <tr>
     *      <td><strong>--path</td><td>The path of the output file.</td>
     *   </tr>
     *   <tr>
     *      <td><strong>--maxColorDepth</td><td>The maximum amount of potion colors
     *      that may be combined into one. A higher value gives a better result
     *      but takes longer to process.</td>
     *   </tr>
     * </table>
     * 
     * @param args the launch parameters
     */
    public static void main(String[] args) {
        String path = "potionColorTable.dat";
        int maxColorDepth = MAX_COLOR_DEPTH;
        for (int i = 0; i < args.length; i++) {
            if (i + 2 != args.length) {
                if (args[i].equalsIgnoreCase("--path")) {
                    path = args[++i];
                } else if (args[i].equalsIgnoreCase("--maxColorDepth")) {
                    maxColorDepth = Integer.parseInt(args[++i]);
                    if (maxColorDepth > 255) {
                        throw new IllegalArgumentException("Max color depth may cannot be greater then 255! ("
                                + maxColorDepth + ")");
                    }
                    if (maxColorDepth < 0) {
                        throw new IllegalArgumentException("Max color depth may cannot be smaller then 1! ("
                                + maxColorDepth + ")");
                    }
                }
            }
        }
        try {
            new PotionColorTableGenerator(path, maxColorDepth);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final static TIntObjectMap<int[]> knownColorsById = new TIntObjectHashMap<>();

    static {
        put(1, 8171462);
        put(2, 5926017);
        put(3, 14270531);
        put(4, 4866583);
        put(5, 9643043);
        put(6, 16262179);
        put(7, 4393481);
        put(8, 2293580);
        put(9, 5578058);
        put(10, 13458603);
        put(11, 10044730);
        put(12, 14981690);
        put(13, 3035801);
        put(14, 8356754);
        put(15, 2039587);
        put(16, 2039713);
        put(17, 5797459);
        put(18, 4738376);
        put(19, 5149489);
        put(20, 3484199);
        put(21, 16284963);
        put(22, 2445989);
        // Ignore index 23, duplicate color
    }

    private static void put(int id, int rgb) {
        int r = ((rgb >> 16) & 0xff) / 4;
        int g = ((rgb >> 8) & 0xff) / 4;
        int b = (rgb & 0xff) / 4;
        int rgb0 = r << DOUBLE_COMPOUND_BITS | g << COMPOUND_BITS | b;
        knownColorsById.put(id, new int[] { rgb0, r, g, b });
    }

    private final TIntObjectMap<int[]> colorTable = new TIntObjectHashMap<>();
    private int mixedColors;

    private PotionColorTableGenerator(String path, int maxColorDepth) throws IOException {
        final File file = new File(path);
        System.out.println("Generating the potion color table, this may take a while...");
        // Process all color combinations
        for (int key : knownColorsById.keys()) {
            int[] t = knownColorsById.get(key);
            this.colorTable.put(t[0], new int[] { key });
            if (maxColorDepth > 1) {
                this.process(Lists.newArrayList(key), t, maxColorDepth - 2);
            }
        }
        // The pool we will use to make the following process go faster
        final ForkJoinPool pool = new ForkJoinPool();
        // Start writing the cache file
        final DataOutputStream dos = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(file)));
        dos.writeInt(PotionColorTable.VERSION);
        // Check for missing colors and search for the closest
        final MissingColorsAction action = new MissingColorsAction();
        pool.invoke(action);
        for (int i = 0; i < action.actions.length; i++) {
            MissingColorsPartAction part = action.actions[i];
            part.dos.flush();
            dos.write(part.baos.toByteArray());
            part.dos.close();
        }
        dos.flush();
        dos.close();
        System.out.println("Finished generating the potion color table.");
        System.out.println("Mixed " + this.mixedColors + " different colors.");
    }

    private class MissingColorsAction extends RecursiveAction {

        private static final long serialVersionUID = -7556112770347635348L;
        private final MissingColorsPartAction[] actions;

        public MissingColorsAction() {
            this.actions = new MissingColorsPartAction[PARALLEL_ACTIONS_COUNT];
            final int step = (int) Math.ceil((double) MAX_COMPOUND_VALUE / (double) PARALLEL_ACTIONS_COUNT);
            int start = 0;
            for (int i = 0; i < PARALLEL_ACTIONS_COUNT; i++) {
                int end;
                if ((i + 1) == PARALLEL_ACTIONS_COUNT) {
                    end = MAX_COMPOUND_VALUE;
                } else {
                    end = start + step;
                }
                this.actions[i] = new MissingColorsPartAction(start, end);
                start += step;
            }
        }

        @Override
        protected void compute() {
            invokeAll(this.actions);
        }
    }

    private class MissingColorsPartAction extends RecursiveAction {

        private static final long serialVersionUID = -7284819313377189480L;

        // The bytes stream that will be appended to the data stream once finished
        private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        private final DataOutputStream dos = new DataOutputStream(this.baos);

        private final int redStart;
        private final int redEnd;

        public MissingColorsPartAction(int redStart, int redEnd) {
            this.redStart = redStart;
            this.redEnd = redEnd;
        }

        @Override
        protected void compute() {
            for (int r = this.redStart; r < this.redEnd; r++) {
                for (int g = 0; g < MAX_COMPOUND_VALUE; g++) {
                    for (int b = 0; b < MAX_COMPOUND_VALUE; b++) {
                        int rgb = r << DOUBLE_COMPOUND_BITS | g << COMPOUND_BITS | b;
                        int[] types;
                        if (!colorTable.containsKey(rgb)) {
                            float closestDistance = Float.MAX_VALUE;
                            int closest = 0;
                            for (int color : colorTable.keys()) {
                                float distance = colorDistance(r * 4, g * 4, b * 4,
                                        ((color >> DOUBLE_COMPOUND_BITS) & COMPOUND_MASK) * 4,
                                        ((color >> COMPOUND_BITS) & COMPOUND_MASK) * 4,
                                        (color & COMPOUND_MASK) * 4);
                                if (distance < closestDistance) {
                                    closestDistance = distance;
                                    closest = color;
                                }
                            }
                            types = colorTable.get(closest);
                        } else {
                            types = colorTable.get(rgb);
                        }
                        try {
                            this.dos.writeByte(types.length);
                            for (int type : types) {
                                this.dos.writeByte(type);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }
    
    private float colorDistance(int r0, int g0, int b0, int r1, int g1, int b1) {
        int rm = (r0 + r1) >> 1;
        r0 -= r1;
        g0 -= g1;
        b0 -= b1;
        return (((512 + rm) * r0 * r0) >> 8) + 4 * g0 * g0 + (((767 - rm) * b0 * b0) >> 8);
    }

    private void process(List<Integer> entries, int[] t, int depth) {
        for (int key : knownColorsById.keys()) {
            int[] t0 = knownColorsById.get(key);
            int r0 = Math.min(MAX_COMPOUND_VALUE, Math.round((t0[1] + t[1]) / 2f));
            int g0 = Math.min(MAX_COMPOUND_VALUE, Math.round((t0[2] + t[2]) / 2f));
            int b0 = Math.min(MAX_COMPOUND_VALUE, Math.round((t0[3] + t[3]) / 2f));
            List<Integer> entries0 = Lists.newArrayList(entries);
            entries0.add(key);
            int rgb0 = r0 << DOUBLE_COMPOUND_BITS | g0 << COMPOUND_BITS | b0;
            int[] t1 = new int[] { rgb0, r0, g0, b0 };
            if (!this.colorTable.containsKey(rgb0) || entries0.size() < this.colorTable.get(rgb0).length) {
                this.colorTable.put(rgb0, entries0.stream().mapToInt(i -> i).toArray());
                this.mixedColors++;
            }
            if (depth > 0) {
                this.process(entries0, t1, depth - 1);
            }
        }
    }
}
