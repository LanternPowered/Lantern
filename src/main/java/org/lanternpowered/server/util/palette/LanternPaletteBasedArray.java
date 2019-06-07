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
package org.lanternpowered.server.util.palette;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.util.palette.Palette.INVALID_ID;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.lanternpowered.server.util.BitHelper;
import org.lanternpowered.server.util.collect.array.VariableValueArray;
import org.spongepowered.api.util.Tuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

public class LanternPaletteBasedArray<T> implements PaletteBasedArray<T> {

    private static final int ARRAY_BACKED_BITS = 4;
    private static final int MAX_MAP_BACKED_BITS = 8;

    protected final GlobalInternalPalette<T> globalPalette;

    /**
     * The palette of this palette based array.
     */
    protected final Palette<T> palette = new LanternPalette();

    /**
     * The array that holds all the block state ids for
     * each block that is present in the current chunk.
     */
    protected VariableValueArray objects;

    /**
     * The internal palette.
     */
    protected InternalPalette<T> internalPalette;

    /**
     * The amount of bits used for each value.
     */
    protected int bits;

    /**
     * The amount of palette objects that are assigned.
     */
    protected int assignedStates = 0;

    protected class LanternPalette implements Palette<T> {

        @Override
        public Optional<T> get(int id) {
            return Optional.ofNullable(internalPalette.get(id));
        }

        @Override
        public PaletteType getType() {
            return internalPalette.getType();
        }

        @Override
        public int getId(T object) {
            return internalPalette.get(object);
        }

        @Override
        public int getOrAssign(T object) {
            return getOrAssignObject(object);
        }

        @Override
        public Collection<T> getEntries() {
            return Collections.unmodifiableCollection(internalPalette.getEntries());
        }

        @Override
        public int size() {
            return assignedStates;
        }

        @SafeVarargs
        @Override
        public final int[] getOrAssign(T... objects) {
            final int[] ids = new int[objects.length];
            int toAssign = 0;
            for (int i = 0; i < objects.length; i++) {
                final int id = internalPalette.get(objects[i]);
                if (id == INVALID_ID) {
                    toAssign++;
                } else {
                    ids[i] = id;
                }
            }
            if (toAssign != 0) {
                expandForAssign(toAssign);
                for (int i = 0; i < objects.length; i++) {
                    ids[i] = getOrAssign(objects[i]);
                }
            }
            return ids;
        }
    }

    protected LanternPaletteBasedArray(LanternPaletteBasedArray<T> other) {
        this.objects = other.objects.copy();
        this.internalPalette = other.internalPalette.copy();
        this.assignedStates = other.assignedStates;
        this.globalPalette = other.globalPalette;
        this.bits = other.bits;
    }

    public LanternPaletteBasedArray(Palette<T> globalPalette, int capacity) {
        this.globalPalette = new GlobalInternalPalette<>(globalPalette);
        expand(ARRAY_BACKED_BITS, capacity);
    }

    /**
     * Creates a {@link LanternPaletteBasedArray} from
     * a integer array of global ids.
     *
     * @param objectIds The object ids
     */
    public LanternPaletteBasedArray(Palette<T> globalPalette, int[] objectIds) {
        this.globalPalette = new GlobalInternalPalette<>(globalPalette);

        final MapBackedInternalPalette<T> palette = new MapBackedInternalPalette<>(16);
        init(palette);
        int id = palette.assignedStates();

        for (int objectId : objectIds) {
            final T object = globalPalette.get(objectId).get();
            if (palette.get(object) == INVALID_ID) {
                palette.assign(id++, object);
            }
        }
        this.bits = BitHelper.requiredBits(id - 1);

        if (this.bits <= MAX_MAP_BACKED_BITS) {
            if (this.bits <= ARRAY_BACKED_BITS) {
                this.bits = ARRAY_BACKED_BITS;
                this.internalPalette = new ArrayBackedInternalPalette<>(this.bits);
                id = 0;
                for (T object : palette.getEntries()) {
                    this.internalPalette.assign(id++, object);
                }
            } else {
                this.internalPalette = palette;
            }
            this.assignedStates = palette.assignedStates();
        } else {
            this.internalPalette = this.globalPalette;
            this.assignedStates = this.globalPalette.assignedStates();
        }

        this.objects = new VariableValueArray(this.bits, objectIds.length);

        // Just copy over the ids
        if (this.internalPalette == this.globalPalette) {
            for (int i = 0; i < objectIds.length; i++) {
                this.objects.set(i, objectIds[i]);
            }
        } else {
            // Set remapped based on the palette
            for (int i = 0; i < objectIds.length; i++) {
                final T object = globalPalette.get(objectIds[i]).get();
                this.objects.set(i, this.internalPalette.get(object));
            }
        }
    }

    public LanternPaletteBasedArray(Palette<T> globalPalette, Collection<T> palette, int capacity, long[] rawBackingData) {
        this.globalPalette = new GlobalInternalPalette<>(globalPalette);

        final List<T> paletteList = palette instanceof List ? (List<T>) palette : new ArrayList<>(palette);

        // The bits that are assigned per value
        final int bits = BitHelper.requiredBits(paletteList.size() - 1);

        // The loaded variable value array
        final VariableValueArray valueArray = new VariableValueArray(bits, capacity, rawBackingData);

        // Load the palette
        final InternalPalette<T> internalPalette;

        this.bits = bits;
        if (this.bits <= ARRAY_BACKED_BITS) {
            this.bits = ARRAY_BACKED_BITS; // Minimum ARRAY_BACKED_BITS bits per value
            internalPalette = new ArrayBackedInternalPalette<>(this.bits);
        } else {
            internalPalette = new MapBackedInternalPalette<>(paletteList.size());
        }
        for (int i = 0; i < paletteList.size(); i++) {
            internalPalette.assign(i, paletteList.get(i));
        }

        if (this.bits <= MAX_MAP_BACKED_BITS) {
            this.internalPalette = internalPalette;
            this.assignedStates = this.internalPalette.getEntries().size();

            if (this.bits != bits) {
                this.objects = new VariableValueArray(this.bits, valueArray.getCapacity());
                for (int i = 0; i < valueArray.getCapacity(); i++) {
                    this.objects.set(i, valueArray.get(i));
                }
            } else {
                this.objects = valueArray;
            }
        } else {
            this.internalPalette = this.globalPalette;
            this.assignedStates = this.globalPalette.assignedStates();

            this.objects = new VariableValueArray(this.bits, valueArray.getCapacity());
            for (int i = 0; i < valueArray.getCapacity(); i++) {
                this.objects.set(i, this.internalPalette.get(internalPalette.get(valueArray.get(i))));
            }
        }
    }

    protected void init(InternalPalette<T> internalPalette) {
    }

    private int getOrAssignObject(T object) {
        final int id = this.internalPalette.get(object);
        if (id != INVALID_ID) {
            return id;
        }
        return assignState(object);
    }

    private int assignState(T object) {
        final int id = this.assignedStates++;
        // Check if more bits are needed
        if (1 << this.bits == id) {
            // The backing palette/data array isn't big enough, so
            // it should be expanded
            expand(this.bits + 1);
        }
        // Things got expanded to the global
        // palette, just return the global id
        if (this.internalPalette == this.globalPalette) {
            // The assigned states value got maxed
            this.assignedStates = this.globalPalette.assignedStates();
            return this.internalPalette.get(object);
        }
        // Now the next id can be assigned
        this.internalPalette.assign(id, object);
        return id;
    }

    private void expandForAssign(int statesToAdd) {
        expand(BitHelper.requiredBits(this.assignedStates + statesToAdd - 1));
    }

    private void expand(int bits) {
        expand(bits, this.objects.getCapacity());
    }

    @SuppressWarnings("ConstantConditions")
    private void expand(int bits, int capacity) {
        // Don't shrink using this method
        if (bits <= this.bits) {
            return;
        }
        final InternalPalette<T> old = this.internalPalette;
        // Create the new internal palette
        // Bits will never be smaller than ARRAY_BACKED_BITS
        if (bits < ARRAY_BACKED_BITS) {
            bits = ARRAY_BACKED_BITS;
        }
        if (bits <= MAX_MAP_BACKED_BITS) {
            // Should only happen once
            if (bits <= ARRAY_BACKED_BITS) {
                this.internalPalette = new ArrayBackedInternalPalette<>(bits);
                init(this.internalPalette);
                // Only upgrade if it isn't already upgraded to a MapBackedInternalPalette
            } else if (!(this.internalPalette instanceof MapBackedInternalPalette)) {
                this.internalPalette = new MapBackedInternalPalette<>((1 << bits) + 1);
                init(this.internalPalette);
                // Copy the old contents
                if (old != null) {
                    int i = 0;
                    for (T object : old.getEntries()) {
                        this.internalPalette.assign(i++, object);
                    }
                }
            }
        } else {
            // Upgrade even more, just use the global palette
            this.internalPalette = this.globalPalette;
        }
        this.bits = bits;
        final VariableValueArray oldStates = this.objects;
        this.objects = new VariableValueArray(bits, capacity);
        // Copy the states to the new array
        if (oldStates != null) {
            for (int i = 0; i < capacity; i++) {
                this.objects.set(i, oldStates.get(i));
            }
        }
    }

    @Override
    public int getCapacity() {
        return this.objects.getCapacity();
    }

    @Override
    public T get(int index) {
        final int id = this.objects.get(index);
        final T object = this.internalPalette.get(id);
        checkNotNull(object, "Failed to find mapping for %s", id);
        return object;
    }

    @Override
    public T set(int index, T object) {
        final T oldObject = get(index);
        if (oldObject == object) {
            return oldObject;
        }
        final int id = getOrAssignObject(object);
        this.objects.set(index, id);
        return oldObject;
    }

    @Override
    public Palette<T> getPalette() {
        return this.palette;
    }

    @Override
    public VariableValueArray getBacking() {
        return this.objects;
    }

    public Tuple<Collection<T>, long[]> serialize() {
        final int capacity = this.objects.getCapacity();

        final MapBackedInternalPalette<T> palette = new MapBackedInternalPalette<>(16);
        // Air is always assigned to id 0
        init(palette);
        int id = 1;
        for (int i = 0; i < capacity; i++) {
            final T object = this.internalPalette.get(this.objects.get(i));
            if (palette.get(object) == INVALID_ID) {
                palette.assign(id++, object);
            }
        }
        final int bits = BitHelper.requiredBits(id - 1);

        final VariableValueArray valueArray = new VariableValueArray(bits, capacity);
        for (int i = 0; i < capacity; i++) {
            valueArray.set(i, palette.get(this.internalPalette.get(this.objects.get(i))));
        }
        return new Tuple<>(palette.getEntries(), valueArray.getBacking());
    }

    @Override
    public PaletteBasedArray<T> copy() {
        return new LanternPaletteBasedArray<>(this);
    }

    protected interface InternalPalette<T> {

        default PaletteType getType() {
            return PaletteType.LOCAL;
        }

        @Nullable
        T get(int id);

        int get(T state);

        void assign(int id, T state);

        int assignedStates();

        Collection<T> getEntries();

        InternalPalette<T> copy();
    }

    static class GlobalInternalPalette<T> implements InternalPalette<T> {

        private final Palette<T> globalPalette;

        GlobalInternalPalette(Palette<T> globalPalette) {
            checkState(globalPalette.getType() == PaletteType.GLOBAL);
            this.globalPalette = globalPalette;
        }

        @Override
        public int get(T state) {
            return this.globalPalette.getId(state);
        }

        @Override
        public void assign(int id, T state) {
            throw new IllegalStateException("This should never happen");
        }

        @Override
        public int assignedStates() {
            return this.globalPalette.size();
        }

        @Override
        public PaletteType getType() {
            return PaletteType.GLOBAL;
        }

        @Override
        public T get(int id) {
            return this.globalPalette.get(id).orElse(null);
        }

        @Override
        public Collection<T> getEntries() {
            return this.globalPalette.getEntries();
        }

        @Override
        public InternalPalette<T> copy() {
            return this;
        }
    }

    @SuppressWarnings("unchecked")
    static class ArrayBackedInternalPalette<T> implements InternalPalette<T> {

        private final Object[] objects;
        private final List<T> objectsList;

        private int assignedStates = 0;

        ArrayBackedInternalPalette(int bits) {
            this(new Object[1 << bits], 0);
        }

        private ArrayBackedInternalPalette(Object[] objects, int assignedStates) {
            this.objectsList = (List<T>) Arrays.asList(objects);
            this.objects = objects;
            this.assignedStates = assignedStates;
        }

        @Override
        public T get(int id) {
            return id < this.assignedStates ? (T) this.objects[id] : null;
        }

        @Override
        public int get(T state) {
            for (int i = 0; i < this.assignedStates; i++) {
                final T other = (T) this.objects[i];
                if (other == state) {
                    return i;
                } else if (other == null) {
                    break;
                }
            }
            return INVALID_ID;
        }

        @Override
        public void assign(int id, T state) {
            this.objects[id] = state;
            this.assignedStates = Math.max(this.assignedStates, id + 1);
        }

        @Override
        public int assignedStates() {
            return this.assignedStates;
        }

        @Override
        public Collection<T> getEntries() {
            return this.objectsList.subList(0, this.assignedStates);
        }

        @Override
        public InternalPalette copy() {
            return new ArrayBackedInternalPalette<>(Arrays.copyOf(this.objects, this.objects.length), this.assignedStates);
        }
    }

    static class MapBackedInternalPalette<T> implements InternalPalette<T> {

        private final List<T> objects;
        private final Object2IntMap<T> idByObject;

        MapBackedInternalPalette(int size) {
            this(new ArrayList<>(size), new Object2IntOpenHashMap<>(size));
        }

        private MapBackedInternalPalette(List<T> blockStates, Object2IntMap<T> idByBlockState) {
            this.objects = blockStates;
            this.idByObject = idByBlockState;
            this.idByObject.defaultReturnValue(INVALID_ID);
        }

        @Override
        public T get(int id) {
            return id < this.objects.size() ? this.objects.get(id) : null;
        }

        @Override
        public int get(T object) {
            return this.idByObject.getInt(object);
        }

        @Override
        public void assign(int id, T object) {
            this.idByObject.put(object, id);
            while (this.objects.size() <= id) {
                this.objects.add(null);
            }
            this.objects.set(id, object);
        }

        @Override
        public int assignedStates() {
            return this.objects.size();
        }

        @Override
        public Collection<T> getEntries() {
            return this.objects;
        }

        @Override
        public InternalPalette<T> copy() {
            return new MapBackedInternalPalette<>(new ArrayList<>(this.objects),
                    new Object2IntOpenHashMap<>(this.idByObject));
        }
    }
}
