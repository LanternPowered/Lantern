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
package org.lanternpowered.api.service.profile

import org.lanternpowered.api.util.collections.toImmutableList
import java.time.Instant

/**
 * Represents the name history of a user.
 */
class NameHistory(entries: Iterable<Entry>) {

    init {
        check(entries.iterator().hasNext()) { "There must be at least one entry." }
    }

    /**
     * A list with all the entries, sorted by when the name was
     * changed. The last element in the list is the current name.
     */
    val entries: List<Entry> = entries.sorted().toImmutableList()

    /**
     * The current name.
     */
    val currentName: String
        get() = this.entries.last().name

    /**
     * An entry of the name history.
     *
     * @property name The name
     * @property changedToAt The time at which the name was changed,
     *                       or `null` if this was the initial name
     */
    data class Entry(
            val name: String,
            val changedToAt: Instant?
    ) : Comparable<Entry> {

        /**
         * Whether this entry is the first name that was ever used.
         */
        val isInitialName: Boolean
            get() = this.changedToAt == null

        override fun compareTo(other: Entry): Int =
                if (this.changedToAt == null) -1 else this.changedToAt.compareTo(other.changedToAt)
    }
}
