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
package org.lanternpowered.server.util

import java.util.UUID

object UUIDHelper {

    /**
     * A regex to match [UUID]s.
     */
    val regex: Regex = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}".toRegex(RegexOption.IGNORE_CASE)

    /**
     * Attempts to parse the given [value] as an [UUID].
     */
    fun parseOrNull(value: String): UUID? {
        return try {
            UUID.fromString(value)
        } catch (ex: Exception) {
            null
        }
    }

    /**
     * Parses the [UUID] from a flat string (without dashes).
     *
     * @param flat The flat string
     * @return The uuid
     */
    @JvmStatic
    fun parseFlatString(flat: String): UUID {
        check(flat.length == 32) { "length must be 32" }
        val most = java.lang.Long.parseLong(flat, 0, 16, 16)
        val least = java.lang.Long.parseLong(flat, 16, 32, 16)
        return UUID(most, least)
    }

    /**
     * Converts the [UUID] to a flat string (without dashes).
     *
     * @param uuid The uuid
     * @return The flat string
     */
    @JvmStatic
    fun toFlatString(uuid: UUID): String {
        val most = uuid.mostSignificantBits
        val least = uuid.leastSignificantBits
        fun Long.toPart(): String {
            val s = this.toString(16)
            // Guarantee that the length is 16 characters
            return if (s.length != 16) "0".repeat(16 - s.length) + s else s
        }
        return most.toPart() + least.toPart()
    }
}
