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
package org.lanternpowered.api.util.palette

/**
 * Represents a palette.
 */
interface Palette<T : Any> {

    /**
     * Gets or assigns the id for the given [T].
     *
     * @param obj The type
     * @return The id
     */
    fun getIdOrAssign(obj: T): Int

    /**
     * Gets the id for the given [T].
     *
     * @param obj The object
     * @return The id, or null if not found
     */
    fun getNullableId(obj: T): Int? {
        val id = getId(obj)
        return if (id == INVALID_ID) null else id
    }

    /**
     * Gets the id for the given [T].
     *
     * @param obj The obj
     * @return The id, or [INVALID_ID] if not found
     */
    fun getId(obj: T): Int

    /**
     * Requires the id that is assigned to the given object.
     *
     * @param obj The object
     * @return The id
     */
    fun requireId(obj: T): Int {
        val id = getId(obj)
        check(id != INVALID_ID) { "The given object $obj doesn't exist." }
        return id
    }

    /**
     * Gets the object that is assigned to the given id.
     *
     * @param id The id
     * @return The object, or null if not found
     */
    operator fun get(id: Int): T?

    /**
     * Requires the object that is assigned to the given id.
     *
     * @param id The id
     * @return The object
     */
    fun require(id: Int): T = get(id) ?: error("The given id $id doesn't exist.")

    /**
     * Gets all the objects that are assigned.
     *
     * @return The assigned objects
     */
    val entries: Collection<T>

    /**
     * The size of the palette. (the amount of objects that are assigned.)
     */
    val size: Int

    /**
     * Creates a copy of this palette.
     */
    fun copy(): Palette<T>

    companion object {

        /**
         * Represents a invalid id.
         */
        const val INVALID_ID = -1
    }
}
