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
/*
 * 2011 January 5
 *
 * The author disclaims copyright to this source code.  In place of
 * a legal notice, here is a blessing:
 *
 *    May you do good and not evil.
 *    May you find forgiveness for yourself and forgive others.
 *    May you share freely, never taking more than you give.
 */
/*
 * 2011 February 16
 *
 * This source code is based on the work of Scaevolus (see notice above).
 * It has been slightly modified by Mojang AB to limit the maximum cache
 * size (relevant to extremely big worlds on Linux systems with limited
 * number of file handles). The region files are postfixed with ".mcr"
 * (Minecraft region file) instead of ".data" to differentiate from the
 * original McRegion files.
 */
/*
 * Copyright (c) 2011-2014 Glowstone - Tad Hardesty
 * Copyright (c) 2010-2011 Lightstone - Graham Edgecombe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.data.io.anvil;

import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import javax.annotation.Nullable;


@NonnullByDefault
public class RegionFile {

    private static final int VERSION_GZIP = 1;
    private static final int VERSION_DEFLATE = 2;

    private static final int SECTOR_BYTES = 4096;
    private static final int SECTOR_INTS = SECTOR_BYTES / 4;

    private static final int CHUNK_HEADER_SIZE = 5;
    private static final byte[] emptySector = new byte[SECTOR_BYTES];

    private RandomAccessFile file;
    private final int[] offsets;
    private final int[] chunkTimestamps;
    private ArrayList<Boolean> sectorFree;
    private int sizeDelta;
    private long lastModified = 0;

    public RegionFile(Path path) throws IOException {
        offsets = new int[SECTOR_INTS];
        chunkTimestamps = new int[SECTOR_INTS];
        sizeDelta = 0;

        if (Files.isRegularFile(path)) {
            lastModified = Files.getLastModifiedTime(path).toMillis();
        }

        file = new RandomAccessFile(path.toFile(), "rw");
        // seek to the end to prepare size checking
        file.seek(file.length());

        // if the file size is under 8KB, grow it (4K chunk offset table, 4K timestamp table)
        if (file.length() < 2 * SECTOR_BYTES) {
            sizeDelta += 2 * SECTOR_BYTES - file.length();
            if (lastModified != 0) {
                // only give a warning if the region file existed beforehand
                Lantern.getLogger().warn("Region \"" + path + "\" under 8K: " + file.length() + " increasing by " + (2 * SECTOR_BYTES - file.length()));
            }

            for (long i = file.length(); i < 2 * SECTOR_BYTES; ++i) {
                file.write(0);
            }
        }

        // if the file size is not a multiple of 4KB, grow it
        if ((file.length() & 0xfff) != 0) {
            sizeDelta += SECTOR_BYTES - (file.length() & 0xfff);
            Lantern.getLogger().warn("Region \"" + path + "\" not aligned: " + file.length() + " increasing by " + (SECTOR_BYTES - (file.length() & 0xfff)));

            for (long i = file.length() & 0xfff; i < SECTOR_BYTES; ++i) {
                file.write(0);
            }
        }

        // set up the available sector map
        int nSectors = (int) (file.length() / SECTOR_BYTES);
        sectorFree = new ArrayList<>(nSectors);
        for (int i = 0; i < nSectors; ++i) {
            sectorFree.add(true);
        }

        sectorFree.set(0, false); // chunk offset table
        sectorFree.set(1, false); // for the last modified info

        // read offsets from offset table
        file.seek(0);
        for (int i = 0; i < SECTOR_INTS; ++i) {
            int offset = file.readInt();
            offsets[i] = offset;

            int startSector = (offset >> 8);
            int numSectors = (offset & 0xff);

            if (offset != 0 && startSector >= 0 && startSector + numSectors <= sectorFree.size()) {
                for (int sectorNum = 0; sectorNum < numSectors; ++sectorNum) {
                    sectorFree.set(startSector + sectorNum, false);
                }
            } else if (offset != 0) {
                Lantern.getLogger().warn("Region \"" + path + "\": offsets[" + i + "] = " + offset + " -> " + startSector + "," + numSectors + " does not fit");
            }
        }
        // read timestamps from timestamp table
        for (int i = 0; i < SECTOR_INTS; ++i) {
            chunkTimestamps[i] = file.readInt();
        }
    }

    /* the modification date of the region file when it was first opened */
    public long getLastModified() {
        return lastModified;
    }

    /* gets how much the region file has grown since it was last checked */
    public int getSizeDelta() {
        int ret = sizeDelta;
        sizeDelta = 0;
        return ret;
    }

    /*
     * gets an (uncompressed) stream representing the chunk data returns null if
     * the chunk is not found or an error occurs
     */
    @Nullable
    public DataInputStream getChunkDataInputStream(int x, int z) throws IOException {
        checkBounds(x, z);

        int offset = getOffset(x, z);
        if (offset == 0) {
            // does not exist
            return null;
        }

        int sectorNumber = offset >> 8;
        int numSectors = offset & 0xFF;
        if (sectorNumber + numSectors > sectorFree.size()) {
            throw new IOException("Invalid sector: " + sectorNumber + "+" + numSectors + " > " + sectorFree.size());
        }

        file.seek(sectorNumber * SECTOR_BYTES);
        int length = file.readInt();
        if (length > SECTOR_BYTES * numSectors) {
            throw new IOException("Invalid length: " + length + " > " + (SECTOR_BYTES * numSectors));
        }

        byte version = file.readByte();
        if (version == VERSION_GZIP) {
            byte[] data = new byte[length - 1];
            file.read(data);
            return new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(data)));
        } else if (version == VERSION_DEFLATE) {
            byte[] data = new byte[length - 1];
            file.read(data);
            return new DataInputStream(new InflaterInputStream(new ByteArrayInputStream(data)));
        }

        Lantern.getLogger().info("Unknown version ({}) in region file, possibly corrupt?", version);
        return null;
    }

    public DataOutputStream getChunkDataOutputStream(int x, int z) {
        checkBounds(x, z);
        return new DataOutputStream(new BufferedOutputStream(new DeflaterOutputStream(new ChunkBuffer(x, z),
                new Deflater(Deflater.BEST_SPEED))));
    }

    /*
     * lets chunk writing be multithreaded by not locking the whole file as a
     * chunk is serializing -- only writes when serialization is over
     */
    class ChunkBuffer extends ByteArrayOutputStream {
        private final int x, z;

        public ChunkBuffer(int x, int z) {
            super(8096); // initialize to 8KB
            this.x = x;
            this.z = z;
        }

        @Override
        public void close() throws IOException {
            try {
                RegionFile.this.write(x, z, buf, count);
            } finally {
                super.close();
            }
        }
    }

    /* write a chunk at (x,z) with length bytes of data to disk */
    protected void write(int x, int z, byte[] data, int length) throws IOException {
        int offset = getOffset(x, z);
        int sectorNumber = offset >> 8;
        int sectorsAllocated = offset & 0xFF;
        int sectorsNeeded = (length + CHUNK_HEADER_SIZE) / SECTOR_BYTES + 1;

        // maximum chunk size is 1MB
        if (sectorsNeeded >= 256) {
            return;
        }

        if (sectorNumber != 0 && sectorsAllocated == sectorsNeeded) {
            /* we can simply overwrite the old sectors */
            write(sectorNumber, data, length);
        } else {
            /* we need to allocate new sectors */

            /* mark the sectors previously used for this chunk as free */
            for (int i = 0; i < sectorsAllocated; ++i) {
                sectorFree.set(sectorNumber + i, true);
            }

            /* scan for a free space large enough to store this chunk */
            int runStart = sectorFree.indexOf(true);
            int runLength = 0;
            if (runStart != -1) {
                for (int i = runStart; i < sectorFree.size(); ++i) {
                    if (runLength != 0) {
                        if (sectorFree.get(i)) runLength++;
                        else runLength = 0;
                    } else if (sectorFree.get(i)) {
                        runStart = i;
                        runLength = 1;
                    }
                    if (runLength >= sectorsNeeded) {
                        break;
                    }
                }
            }

            if (runLength >= sectorsNeeded) {
                /* we found a free space large enough */
                sectorNumber = runStart;
                setOffset(x, z, (sectorNumber << 8) | sectorsNeeded);
                for (int i = 0; i < sectorsNeeded; ++i) {
                    sectorFree.set(sectorNumber + i, false);
                }
                write(sectorNumber, data, length);
            } else {
                /*
                 * no free space large enough found -- we need to grow the
                 * file
                 */
                file.seek(file.length());
                sectorNumber = sectorFree.size();
                for (int i = 0; i < sectorsNeeded; ++i) {
                    file.write(emptySector);
                    sectorFree.add(false);
                }
                sizeDelta += SECTOR_BYTES * sectorsNeeded;

                write(sectorNumber, data, length);
                setOffset(x, z, (sectorNumber << 8) | sectorsNeeded);
            }
        }
        setTimestamp(x, z, (int) (System.currentTimeMillis() / 1000L));
        //file.getChannel().force(true);
    }

    /* write a chunk data to the region file at specified sector number */
    private void write(int sectorNumber, byte[] data, int length) throws IOException {
        file.seek(sectorNumber * SECTOR_BYTES);
        file.writeInt(length + 1); // chunk length
        file.writeByte(VERSION_DEFLATE); // chunk version number
        file.write(data, 0, length); // chunk data
    }

    /* is this an invalid chunk coordinate? */
    private void checkBounds(int x, int z) {
        if (x < 0 || x >= 32 || z < 0 || z >= 32) {
            throw new IllegalArgumentException("Chunk out of bounds: (" + x + ", " + z + ")");
        }
    }

    private int getOffset(int x, int z) {
        return this.offsets[x + z * 32];
    }

    /**
     * Gets whether there chunk data exists for the chunk at the
     * coordinates (relative to the region coordinates).
     * 
     * @param x the x coordinate
     * @param z the z coordinate
     * @return whether the chunk data exists
     */
    public boolean hasChunk(int x, int z) {
        return this.getOffset(x, z) != 0;
    }

    private void setOffset(int x, int z, int offset) throws IOException {
        offsets[x + z * 32] = offset;
        file.seek((x + z * 32) * 4);
        file.writeInt(offset);
    }

    private void setTimestamp(int x, int z, int value) throws IOException {
        chunkTimestamps[x + z * 32] = value;
        file.seek(SECTOR_BYTES + (x + z * 32) * 4);
        file.writeInt(value);
    }

    public void close() throws IOException {
        file.getChannel().force(true);
        file.close();
    }
}
