package org.lanternpowered.server.network.value

import org.lanternpowered.api.util.math.floorToInt
import org.spongepowered.math.GenericMath

/**
 * Represents an angle that's packed into a single byte.
 */
inline class PackedAngle(val packed: Byte) {

    /**
     * The packed angle in degrees.
     */
    val degrees: Double
        get() = (this.packed.toDouble() / 256.0) * 360.0

    companion object {

        /**
         * A packed angle of zero degrees or radians.
         */
        val Zero = PackedAngle(0)

        /**
         * Constructs a [PackedAngle] from the given angle degrees.
         */
        fun ofDegrees(angle: Double): PackedAngle {
            val wrapped = GenericMath.wrapAngleDeg(angle.toFloat())
            return PackedAngle(((wrapped / 360.0) * 256.0).floorToInt().toByte())
        }
    }
}
