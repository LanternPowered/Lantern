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
package org.lanternpowered.server.util.palette

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2IntMap
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import org.lanternpowered.api.util.palette.GlobalPalette
import org.lanternpowered.api.util.palette.Palette
import org.lanternpowered.api.util.palette.PaletteBasedArray
import org.lanternpowered.api.util.palette.PaletteBasedArrayFactory
import org.lanternpowered.api.util.palette.SerializedPaletteBasedArray
import org.lanternpowered.api.util.uncheckedCast
import org.lanternpowered.server.util.BitHelper
import org.lanternpowered.server.util.VariableValueArray
import java.util.ArrayList

object LanternPaletteBasedArrayFactory : PaletteBasedArrayFactory {

    override fun <T : Any> of(size: Int, default: () -> T): PaletteBasedArray<T> {
        val palette = ExpandablePalette<T>()
        return LanternPaletteBasedArray(VariableValueArray(palette.bits, size), default, null, palette)
    }

    override fun <T : Any> of(size: Int, globalPalette: GlobalPalette<T>): PaletteBasedArray<T> {
        val palette = ExpandablePalette(globalPalette)
        return LanternPaletteBasedArray(VariableValueArray(palette.bits, size), { globalPalette.default }, globalPalette, palette)
    }

    private fun <T : Any> create(values: IntArray, palette: Palette<T>, globalPalette: GlobalPalette<T>?, default: () -> T): PaletteBasedArray<T> {
        @Suppress("NAME_SHADOWING")
        val palette = ExpandablePalette.of(palette, globalPalette)

        val bits = BitHelper.requiredBits(palette.size - 1)
        val backing = VariableValueArray(bits, values.size)

        for (index in values.indices)
            backing[index] = values[index]

        return LanternPaletteBasedArray(backing, default, globalPalette, palette)
    }

    override fun <T : Any> of(values: IntArray, palette: Palette<T>, default: () -> T): PaletteBasedArray<T> =
            create(values, palette, palette as? GlobalPalette<T>, default)

    override fun <T : Any> of(values: IntArray, palette: Palette<T>, globalPalette: GlobalPalette<T>, default: () -> T): PaletteBasedArray<T> =
            create(values, palette, globalPalette, default)

    private fun <T : Any> create(
            values: VariableValueArray, palette: Palette<T>, globalPalette: GlobalPalette<T>?, default: () -> T): PaletteBasedArray<T> {
        val array = LanternPaletteBasedArray(values, default, globalPalette, palette)
        // Optimize the array and force to make copies
        array.minimize(force = true)
        return array
    }

    override fun <T : Any> of(values: VariableValueArray, palette: Palette<T>, globalPalette: GlobalPalette<T>, default: () -> T):
            PaletteBasedArray<T> = create(values, palette, globalPalette, default)

    override fun <T : Any> of(values: VariableValueArray, palette: Palette<T>, default: () -> T):
            PaletteBasedArray<T> = create(values, palette, palette as? GlobalPalette<T>, default)

    private fun <T : Any> create(data: SerializedPaletteBasedArray<T>, globalPalette: GlobalPalette<T>?, default: () -> T): PaletteBasedArray<T> {
        @Suppress("NAME_SHADOWING")
        val palette = ExpandablePalette.of(data.palette, globalPalette)

        val bits = BitHelper.requiredBits(palette.size - 1)
        val size = data.values.size * (Long.SIZE_BITS / bits)
        val backing = VariableValueArray(bits, size, data.values)

        return LanternPaletteBasedArray(backing, default, globalPalette, palette)
    }

    override fun <T : Any> deserialize(data: SerializedPaletteBasedArray<T>, default: () -> T): PaletteBasedArray<T> =
            create(data, null, default)

    override fun <T : Any> deserialize(
            data: SerializedPaletteBasedArray<T>, globalPalette: GlobalPalette<T>, default: () -> T
    ): PaletteBasedArray<T> = create(data, globalPalette, default)
}

private class LanternPaletteBasedArray<T : Any>(
        backing: VariableValueArray,
        private val default: () -> T,
        private val globalPalette: GlobalPalette<T>?,
        palette: Palette<T>
) : PaletteBasedArray<T> {

    override val size: Int
        get() = this.backing.size

    override var backing: VariableValueArray = backing
        private set

    override var palette: Palette<T> = palette
        private set

    init {
        if (palette is ExpandablePalette<T>)
            palette.expansionListener = this::onResize
    }

    override fun get(index: Int): T {
        val id = this.backing[index]
        return this.palette[id] ?: this.default()
    }

    override fun set(index: Int, obj: T) {
        val id = this.palette.getIdOrAssign(obj)
        this.backing[index] = id
    }

    override fun copy(): PaletteBasedArray<T> = LanternPaletteBasedArray(
            this.backing.copy(), this.default, this.globalPalette, this.palette.copy())

    private fun onResize(bits: Int, oldPalette: Palette<T>, newPalette: Palette<T>) {
        if (newPalette is GlobalPalette<*>) {
            // The mapped ids also changed when changing to the global palette,
            // so the whole array needs to be updated
            val backing = VariableValueArray(bits, this.size)
            for (index in 0 until this.size) {
                val id = this.backing[index]
                backing[index] = newPalette.getId(oldPalette.require(id))
            }
            this.backing = backing
            // Also override the returned palette, this allows instance checks
            // on the returned palette whether it's global.
            this.palette = newPalette
        } else {
            this.backing = this.backing.copyWithBitsPerValue(bits)
        }
    }

    /**
     * Minimizes this palette based array so that it uses the
     * minimum amount of bits per value. It's useful to call
     * this when a lot of modifications have been done to the
     * array so there are a lot of unused entries in the
     * palette.
     */
    fun minimize(force: Boolean = false) {
        val palette = ExpandablePalette(this.globalPalette)
        for (index in 0 until this.size) {
            val id = this.backing[index]
            val obj = this.palette.require(id)
            palette.getIdOrAssign(obj)
        }
        val bits = BitHelper.requiredBits(palette.size - 1)
        // The size of the array didn't change, so there's no point to minimize
        if (!force && (bits == this.backing.bitsPerValue || palette.useGlobal))
            return
        val idRemapping = Int2IntOpenHashMap()
        for ((id, obj) in palette.entries.withIndex())
            idRemapping[this.palette.requireId(obj)] = id
        val backing = VariableValueArray(bits, this.size)
        for (index in 0 until this.size)
            backing[index] = idRemapping[this.backing[index]]
        palette.expansionListener = this::onResize
        this.palette = palette
        this.backing = backing
    }

    override fun serialize(): SerializedPaletteBasedArray<T> {
        val size = this.backing.size
        val palette = MapBackedInternalPalette<T>(16, Int.MAX_VALUE)
        for (index in 0 until size)
            palette.getIdOrAssign(get(index))
        val bits = BitHelper.requiredBits(palette.size - 1)
        val valueArray = VariableValueArray(bits, size)
        for (index in 0 until size)
            valueArray[index] = palette.getId(get(index))
        return SerializedPaletteBasedArray(palette.entries, valueArray.backing)
    }
}

private interface InternalPalette<T : Any> : Palette<T> {

    /**
     * Assigns the id to the given [obj].
     *
     * This method doesn't remove old mappings, this means
     * that two ids can reference to the same object.
     *
     * @param id The id
     * @param obj The object
     */
    fun assign(id: Int, obj: T)
}

private class ExpandablePalette<T : Any>(
        private val globalPalette: GlobalPalette<T>? = null,
        private var backing: Palette<T> = ArrayBackedInternalPalette(ARRAY_BACKED_BITS)
) : Palette<T> {

    companion object {
        private const val ARRAY_BACKED_BITS = 4
        private const val MAX_MAP_BACKED_BITS = 8
        private const val MAX_MAP_IDS = 1 shl MAX_MAP_BACKED_BITS

        fun <T : Any> of(palette: Palette<T>, globalPalette: GlobalPalette<T>? = palette as? GlobalPalette<T>): Palette<T> =
                of(palette.entries, globalPalette)

        fun <T : Any> of(collection: Collection<T>, globalPalette: GlobalPalette<T>?): Palette<T> {
            val bits = BitHelper.requiredBits(collection.size - 1)
            val backing = when {
                bits <= ARRAY_BACKED_BITS -> ArrayBackedInternalPalette(ARRAY_BACKED_BITS)
                bits <= MAX_MAP_BACKED_BITS || globalPalette == null -> MapBackedInternalPalette<T>(collection.size, MAX_MAP_IDS)
                else -> globalPalette
            }
            if (backing is GlobalPalette<T>)
                return backing
            // Copy the mappings
            for (obj in collection)
                backing.getIdOrAssign(obj)
            return ExpandablePalette(globalPalette, backing)
        }
    }

    var expansionListener: ((bits: Int, oldPalette: Palette<T>, newPalette: Palette<T>) -> Unit)? = null

    var bits: Int = 0
        private set

    val useGlobal: Boolean get() = this.bits > MAX_MAP_BACKED_BITS && this.globalPalette != null

    fun expandBits(bits: Int) {
        // Limit the minimum bits
        @Suppress("NAME_SHADOWING")
        val bits = bits.coerceAtLeast(ARRAY_BACKED_BITS)
        // Don't shrink using this method
        if (bits <= this.bits)
            return
        val old = this.backing
        if (bits <= MAX_MAP_BACKED_BITS || this.globalPalette == null) {
            // Should only happen once
            if (bits <= ARRAY_BACKED_BITS) {
                this.backing = ArrayBackedInternalPalette(bits)
            // Only upgrade if it isn't already upgraded to a MapBackedInternalPalette
            } else if (this.backing !is MapBackedInternalPalette<T>) {
                val backing = MapBackedInternalPalette<T>((1 shl bits) + 1, MAX_MAP_IDS)
                // Copy the old contents
                for ((id, obj) in old.entries.withIndex())
                    backing.assign(id, obj)
                this.backing = backing
            }
            this.bits = bits
        } else {
            // Upgrade even more, just use the global palette, this means that the
            // array bits per value are also increased to the max of the registry
            this.backing = this.globalPalette
            this.bits = BitHelper.requiredBits(this.backing.size - 1)
        }
        this.expansionListener?.invoke(this.bits, old, this.backing)
    }

    override fun getIdOrAssign(obj: T): Int {
        var id = this.backing.getIdOrAssign(obj)
        if (id != PALETTE_FULL)
            return id
        expandBits(BitHelper.requiredBits(this.size))
        id = this.backing.getIdOrAssign(obj)
        check(id != PALETTE_FULL)
        return id
    }

    override fun copy(): ExpandablePalette<T> = ExpandablePalette(this.globalPalette, this.backing.copy())

    override fun getId(obj: T): Int = this.backing.getId(obj)
    override fun get(id: Int): T? = this.backing[id]

    override val entries: Collection<T>
        get() = this.backing.entries

    override val size: Int
        get() = this.backing.size
}

/**
 * A return code that represents that a palette is full
 * and can't contain any more mappings.
 */
private const val PALETTE_FULL = -2

private class ArrayBackedInternalPalette<T : Any>(
        private val objects: Array<T?>,
        private var assignedStates: Int = 0
) : InternalPalette<T> {

    private val objectsList: List<T?> = objects.asList()

    constructor(bits: Int) : this(arrayOfNulls<Any?>(1 shl bits).uncheckedCast(), 0)

    override fun get(id: Int): T? = if (id < this.assignedStates) this.objects[id] else null

    override fun getId(obj: T): Int {
        for (i in 0 until this.assignedStates) {
            val other = this.objects[i]
            if (other == obj) {
                return i
            } else if (other == null) {
                break
            }
        }
        return Palette.INVALID_ID
    }

    override fun getIdOrAssign(obj: T): Int {
        var index = getId(obj)
        if (index != Palette.INVALID_ID)
            return index
        if (this.assignedStates == this.objects.size)
            return PALETTE_FULL
        index = this.assignedStates
        assign(index, obj)
        return index
    }

    override fun assign(id: Int, obj: T) {
        this.objects[id] = obj
        this.assignedStates = this.assignedStates.coerceAtLeast(id + 1)
    }

    override val size: Int get() = this.assignedStates

    override val entries: Collection<T>
        get() = this.objectsList.subList(0, this.assignedStates).uncheckedCast()

    override fun copy(): Palette<T> =
            ArrayBackedInternalPalette(this.objects.copyOf(), this.assignedStates)
}

private class MapBackedInternalPalette<T : Any> private constructor(
        private val objects: MutableList<T?>,
        private val idByObject: Object2IntMap<T>,
        private val maxSize: Int
) : InternalPalette<T> {

    constructor(allocatedSize: Int, maxSize: Int) : this(ArrayList<T?>(allocatedSize), Object2IntOpenHashMap<T>(allocatedSize), maxSize)

    init {
        this.idByObject.defaultReturnValue(Palette.INVALID_ID)
    }

    override fun get(id: Int): T? = if (id < this.objects.size) this.objects[id] else null
    override fun getId(obj: T): Int = this.idByObject.getInt(obj)

    override fun assign(id: Int, obj: T) {
        this.idByObject[obj] = id
        while (this.objects.size <= id) {
            this.objects.add(null)
        }
        this.objects[id] = obj
    }

    override fun getIdOrAssign(obj: T): Int {
        var index = getId(obj)
        if (index != Palette.INVALID_ID)
            return index
        index = this.objects.size
        if (index >= this.maxSize)
            return PALETTE_FULL
        assign(index, obj)
        return index
    }

    override val size: Int get() = this.objects.size

    override val entries: Collection<T>
        get() = this.objects.uncheckedCast()

    override fun copy(): Palette<T> =
            MapBackedInternalPalette(ArrayList(this.objects), Object2IntOpenHashMap(this.idByObject), this.maxSize)
}
