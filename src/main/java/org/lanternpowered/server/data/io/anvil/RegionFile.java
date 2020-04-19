/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
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
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.BitSet;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class RegionFile {

    private static final Marker REGION_FILE_MARKER = MarkerFactory.getMarker("REGION_FILE");

    private static final int VERSION_GZIP = 1;
    private static final int VERSION_DEFLATE = 2;

    private static final int SECTOR_BYTES = 4096;
    private static final int SECTOR_INTS = SECTOR_BYTES / 4;

    private static final int CHUNK_HEADER_SIZE = 5;
    private static final byte[] EMPTY_SECTOR = new byte[SECTOR_BYTES];

    private final RandomAccessFile file;
    private final int[] offsets;
    private final BitSet usedSectors;
    private final int regionX;
    private final int regionZ;

    private int sectorCount;

    RegionFile(Path path, int regionX, int regionZ) throws IOException {
        this.regionX = regionX;
        this.regionZ = regionZ;
        this.offsets = new int[SECTOR_INTS];

        long lastModified = 0;
        if (Files.isRegularFile(path)) {
            lastModified = Files.getLastModifiedTime(path).toMillis();
        }

        this.file = new RandomAccessFile(path.toFile(), "rw");

        long length = this.file.length();
        // seek to the end to prepare size checking
        this.file.seek(length);

        // if the file size is under 8KB, grow it (4K chunk offset table, 4K timestamp table)
        if (length < 2 * SECTOR_BYTES) {
            if (lastModified != 0) {
                // Only give a warning if the region file existed beforehand
                Lantern.getLogger().warn(REGION_FILE_MARKER, "Region \"{}\" under 8K: {} increasing by {}",
                        path, length, 2 * SECTOR_BYTES - length);
            }

            if (length == 0) {
                this.file.write(EMPTY_SECTOR);
                this.file.write(EMPTY_SECTOR);
            } else {
                for (; length < 2 * SECTOR_BYTES; length++) {
                    this.file.write(0);
                }
            }
        }

        length = this.file.length();
        // if the file size is not a multiple of 4KB, grow it
        if ((length & 0xfff) != 0) {
            Lantern.getLogger().warn(REGION_FILE_MARKER, "Region \"{}\" not aligned: {} increasing by {}",
                    path, length, SECTOR_BYTES - (length & 0xfff));

            length = length & 0xfff;
            for (; length < SECTOR_BYTES; length++) {
                this.file.write(0);
            }
        }

        // set up the available sector map
        final int nSectors = (int) (this.file.length() / SECTOR_BYTES);
        this.sectorCount = nSectors;
        this.usedSectors = new BitSet(nSectors);
        this.usedSectors.set(0, 2);

        // set the following sectors
        // 0 - chunk offset table
        // 1 - for the last modified

        // read offsets from offset table
        this.file.seek(0);
        for (int i = 0; i < SECTOR_INTS; ++i) {
            final int offset = this.file.readInt();
            this.offsets[i] = offset;

            final int startSector = (offset >> 8);
            final int numSectors = (offset & 0xff);

            if (offset != 0 && startSector >= 0 && startSector + numSectors <= nSectors) {
                this.usedSectors.set(startSector, startSector + numSectors);
            } else if (offset != 0) {
                Lantern.getLogger().warn(REGION_FILE_MARKER, "Region \"{}\": offsets[{}] = {} -> {},{} does not fit",
                        path, i, offset, startSector, numSectors);
            }
        }
        // read timestamps from timestamp table -- the timestamps aren't used, so there is no need to read them
        // for (int i = 0; i < SECTOR_INTS; ++i) {
        //     this.file.readInt();
        // }
    }

    /**
     * Gets whether there chunk data exists for the chunk at the
     * coordinates (relative to the region coordinates).
     *
     * @param x the x coordinate
     * @param z the z coordinate
     * @return whether the chunk data exists
     */
    public synchronized boolean hasChunk(int x, int z) {
        checkBounds(x, z);

        try {
            final int offset = getOffset(x, z);
            if (offset == 0) {
                // Does not exist
                return false;
            }

            final int sectorNumber = offset >> 8;
            final int numSectors = offset & 0xff;
            if (sectorNumber + numSectors > this.sectorCount) {
                logWarning();
                return false;
            }

            this.file.seek(sectorNumber * SECTOR_BYTES);
            final int length = this.file.readInt();
            if (length > SECTOR_BYTES * numSectors) {
                logWarning();
                return false;
            }

            final byte version = this.file.readByte();
            if (version == VERSION_GZIP || version == VERSION_DEFLATE) {
                return true;
            }
        } catch (IOException ignored) {
        }

        logWarning();
        return false;
    }

    /*
     * gets an (uncompressed) stream representing the chunk data returns null if
     * the chunk is not found or an error occurs
     */
    @Nullable
    public synchronized DataInputStream getChunkDataInputStream(int x, int z) {
        checkBounds(x, z);

        try {
            final int offset = getOffset(x, z);
            if (offset == 0) {
                // Does not exist
                return null;
            }

            final int sectorNumber = offset >> 8;
            final int numSectors = offset & 0xff;
            if (sectorNumber + numSectors > this.sectorCount) {
                logWarning();
                return null;
            }

            this.file.seek(sectorNumber * SECTOR_BYTES);
            final int length = this.file.readInt();
            if (length > SECTOR_BYTES * numSectors) {
                logWarning();
                return null;
            }

            final byte version = this.file.readByte();
            if (version == VERSION_GZIP) {
                final byte[] data = new byte[length - 1];
                this.file.read(data);
                return new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(data))));
            } else if (version == VERSION_DEFLATE) {
                final byte[] data = new byte[length - 1];
                this.file.read(data);
                return new DataInputStream(new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(data))));
            }
        } catch (IOException ignored) {
        }
        logWarning();
        return null;
    }

    private void logWarning() {
        // Use the debug level, there is no need to spam the console with
        // corrupt file errors that cannot be fixed
        // But might be useful for debugging purposes
        Lantern.getLogger().debug(REGION_FILE_MARKER, "An error occurred loading the region file ({};{}), is the file corrupt?",
                this.regionX, this.regionZ);
    }

    public DataOutputStream getChunkDataOutputStream(int x, int z) {
        checkBounds(x, z);
        return new DataOutputStream(new BufferedOutputStream(new DeflaterOutputStream(
                new ChunkBuffer(x, z), new Deflater(Deflater.BEST_SPEED))));
    }

    /*
     * lets chunk writing be multithreaded by not locking the whole file as a
     * chunk is serializing -- only writes when serialization is over
     */
    private class ChunkBuffer extends ByteArrayOutputStream {

        private final int x;
        private final int z;

        ChunkBuffer(int x, int z) {
            super(8096); // initialize to 8KB
            this.x = x;
            this.z = z;
        }

        @Override
        public void close() throws IOException {
            try {
                RegionFile.this.write(this.x, this.z, this.buf, this.count);
            } finally {
                super.close();
            }
        }
    }

    // write a chunk at (x,z) with length bytes of data to disk
    protected synchronized void write(int x, int z, byte[] data, int length) throws IOException {
        final int offset = getOffset(x, z);
        int sectorNumber = offset >> 8;
        final int sectorsAllocated = offset & 0xff;
        final int sectorsNeeded = (length + CHUNK_HEADER_SIZE) / SECTOR_BYTES + 1;

        // maximum chunk size is 1MB
        if (sectorsNeeded >= 256) {
            return;
        }

        if (sectorNumber != 0 && sectorsAllocated == sectorsNeeded) {
            // we can simply overwrite the old sectors
            write(sectorNumber, data, length);
        } else {
            // we need to allocate new sectors

            // mark the sectors previously used for this chunk as free
            this.usedSectors.clear(sectorNumber, sectorNumber + sectorsAllocated);

            // scan for a free space large enough to store this chunk
            int runStart = this.usedSectors.nextClearBit(2);
            int runLength = 0;
            if (runStart != -1 && runStart < this.sectorCount) {
                for (int i = runStart; i < this.sectorCount; ++i) {
                    if (runLength != 0) {
                        if (!this.usedSectors.get(i)) {
                            runLength++;
                        } else {
                            runLength = 0;
                        }
                    } else if (!this.usedSectors.get(i)) {
                        runStart = i;
                        runLength = 1;
                    }
                    if (runLength >= sectorsNeeded) {
                        break;
                    }
                }
            } else {
                runStart = this.sectorCount;
            }

            sectorNumber = runStart;
            if (runLength < sectorsNeeded) {
                final int added = sectorsNeeded - runLength;
                this.sectorCount += added;
                this.file.seek(this.file.length());
                for (int i = 0; i < added; i++) {
                    this.file.write(EMPTY_SECTOR);
                }
            }

            // Clear the sectors to set them in use
            this.usedSectors.set(sectorNumber, sectorNumber + sectorsNeeded);

            // Write chunk data and offset
            write(sectorNumber, data, length);
            setOffset(x, z, (sectorNumber << 8) | sectorsNeeded);
        }
        setTimestamp(x, z, (int) (System.currentTimeMillis() / 1000L));
    }

    // write a chunk data to the region file at specified sector number
    private void write(int sectorNumber, byte[] data, int length) throws IOException {
        this.file.seek(sectorNumber * SECTOR_BYTES);
        this.file.writeInt(length + 1); // chunk length
        this.file.writeByte(VERSION_DEFLATE); // chunk version number
        this.file.write(data, 0, length); // chunk data
    }

    // is this an invalid chunk coordinate?
    private void checkBounds(int x, int z) {
        if (x < 0 || x >= 32 || z < 0 || z >= 32) {
            throw new IllegalArgumentException("Chunk out of bounds: (" + x + ", " + z + ")");
        }
    }

    private int getOffset(int x, int z) {
        return this.offsets[x + z * 32];
    }

    private void setOffset(int x, int z, int offset) throws IOException {
        this.offsets[x + z * 32] = offset;
        this.file.seek((x + z * 32) * 4);
        this.file.writeInt(offset);
    }

    private void setTimestamp(int x, int z, int value) throws IOException {
        this.file.seek(SECTOR_BYTES + (x + z * 32) * 4);
        this.file.writeInt(value);
    }

    public void close() throws IOException {
        this.file.getChannel().force(true);
        this.file.close();
    }
}
