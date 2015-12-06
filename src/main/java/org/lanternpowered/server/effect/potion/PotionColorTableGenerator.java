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
    private static final int MAX_COLOR_DEPTH = 4;

    // The amount of actions that should run parallel
    private static final int PARALLEL_ACTIONS_COUNT = 10;

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
     *      but takes longer.</td>
     *   </tr>
     * </table>
     * 
     * @param args the launch parameters
     */
    public static void main(String[] args) {
        String path = "potionColorTable.dat";
        int maxColorDepth = MAX_COLOR_DEPTH;
        for (int i = 0; i < args.length; i++) {
            if (i + 1 != args.length) {
                if (args[i].equalsIgnoreCase("--path")) {
                    path = args[++i];
                } else if (args[i].equalsIgnoreCase("--maxColorDepth")) {
                    maxColorDepth = Integer.parseInt(args[++i]);
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
        int r = (rgb & 0xff) / 4;
        int g = ((rgb >> 8) & 0xff) / 4;
        int b = ((rgb >> 16) & 0xff) / 4;
        knownColorsById.put(id, new int[] { rgb, r, g, b });
    }

    private final TIntObjectMap<int[]> colorTable = new TIntObjectHashMap<>();

    private PotionColorTableGenerator(String path, int maxColorDepth) throws IOException {
        final File file = new File(path);
        System.out.println("Generating the potion color table, this may take a while...");
        // Process all color combinations
        for (int key : knownColorsById.keys()) {
            int[] t = knownColorsById.get(key);
            this.colorTable.put(key, new int[] { t[0] });
            if (maxColorDepth > 0) {
                this.process(Lists.newArrayList(key), t, maxColorDepth - 1);
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
        for (MissingColorsPartAction part : action.actions) {
            part.dos.flush();
            dos.write(part.baos.toByteArray());
            part.dos.close();
            this.colorTable.putAll(part.colorTableChanges);
        }
        dos.flush();
        dos.close();
        System.out.println("Finished generating the potion color table.");
    }

    private class MissingColorsAction extends RecursiveAction {

        private static final long serialVersionUID = -7556112770347635348L;
        private final MissingColorsPartAction[] actions;

        private MissingColorsAction() {
            this.actions = new MissingColorsPartAction[PARALLEL_ACTIONS_COUNT];
            final int step = 64 / PARALLEL_ACTIONS_COUNT;
            int start = 0;
            for (int i = 0; i < PARALLEL_ACTIONS_COUNT; i++) {
                int end;
                if ((i + 1) == PARALLEL_ACTIONS_COUNT) {
                    end = 64;
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

        // The changes that should be added to the color table once finished
        private final TIntObjectMap<int[]> colorTableChanges = new TIntObjectHashMap<>();

        private final int redStart;
        private final int redEnd;

        public MissingColorsPartAction(int redStart, int redEnd) {
            this.redStart = redStart;
            this.redEnd = redEnd;
        }

        @Override
        protected void compute() {
            for (int r = this.redStart; r < this.redEnd; r++) {
                for (int g = 0; g < 64; g++) {
                    for (int b = 0; b < 64; b++) {
                        int rgb = b << 16 | g << 8 | r;
                        int[] types;
                        if (!colorTable.containsKey(rgb)) {
                            int closestDistance = Integer.MAX_VALUE;
                            int closest = 0;
                            for (int color : colorTable.keys()) {
                                int r0 = color & 0xff;
                                int g0 = (color >> 8) & 0xff;
                                int b0 = (color >> 16) & 0xff;
                                int distance = (int) Math.sqrt(Math.pow(r - r0, 2) -
                                        Math.pow(g - g0, 2) - Math.pow(b - b0, 2));
                                if (closestDistance > distance) {
                                    closestDistance = distance;
                                    closest = color;
                                }
                            }
                            colorTableChanges.put(rgb, types = colorTable.get(closest));
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

    private void process(List<Integer> entries, int[] t, int depth) {
        for (int key : knownColorsById.keys()) {
            int[] t0 = knownColorsById.get(key);
            int r0 = (t0[1] + t[1]) / 2;
            int g0 = (t0[2] + t[2]) / 2;
            int b0 = (t0[3] + t[3]) / 2;
            List<Integer> entries0 = Lists.newArrayList(entries);
            entries0.add(key);
            int rgb0 = b0 << 16 | g0 << 8 | r0;
            int[] t1 = new int[] { rgb0, r0, g0, b0 };
            if (!this.colorTable.containsKey(t[0]) || entries0.size() < this.colorTable.get(t[0]).length) {
                this.colorTable.put(rgb0, entries0.stream().mapToInt(i -> i).toArray());
            }
            if (depth > 0) {
                this.process(entries0, t1, depth - 1);
            }
        }
    }
}
