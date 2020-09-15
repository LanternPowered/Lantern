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
package org.lanternpowered.server.world

import com.google.common.collect.Sets
import org.lanternpowered.server.entity.player.LanternPlayer
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.vanilla.packet.type.play.WorldBorderPacket
import org.lanternpowered.server.world.chunk.Chunks
import org.lanternpowered.server.world.chunk.LanternChunkLayout
import org.spongepowered.api.world.WorldBorder
import org.spongepowered.math.vector.Vector3d
import java.time.Duration
import kotlin.math.roundToInt
import kotlin.time.seconds
import kotlin.time.toJavaDuration
import kotlin.time.toKotlinDuration

class LanternWorldBorder : WorldBorder {

    companion object {
        private const val Boundary = Chunks.MaxBlockXZ + 1
    }

    /**
     * All the players tracking this world border.
     */
    private val players = Sets.newConcurrentHashSet<LanternPlayer>()

    // World border properties
    @JvmField
    var centerX = 0.0

    @JvmField
    var centerZ = 0.0

    // The current radius of the border

    @JvmField
    var startDiameter = 60000000.0

    @JvmField
    var endDiameter = startDiameter

    @JvmField
    var warningDistance = 5.0

    var warningTime = 15.seconds

    @JvmField
    var damage = 1.0

    @JvmField
    var damageThreshold = 5.0

    // The remaining time will be stored in this
    // for the first world tick
    @JvmField
    var lerpTime: Long = 0

    // Shrink or growing times
    private var startTime: Long = -1
    private var endTime: Long = 0

    fun addPlayer(player: LanternPlayer) {
        if (!players.add(player))
            return
        player.connection.send(WorldBorderPacket.Initialize(this.centerX, this.centerZ, this.diameter,
                this.newDiameter, this.timeRemainingMillis, Boundary, this.roundedWarningDistance, this.warningTimeSeconds))
    }

    fun removePlayer(player: LanternPlayer) {
        this.players.remove(player)
    }

    private fun broadcast(supplier: () -> Packet) {
        if (this.players.isEmpty())
            return
        val packet = supplier()
        for (player in this.players)
            player.connection.send(packet)
    }

    val roundedWarningDistance: Int
        get() = this.warningDistance.roundToInt()

    override fun getNewDiameter(): Double = this.endDiameter

    override fun getDiameter(): Double {
        if (this.startTime == -1L)
            this.updateCurrentTime()
        if (this.startDiameter == this.endDiameter)
            return this.startDiameter
        val lerpTime = this.endTime - this.startTime
        if (lerpTime == 0L)
            return this.startDiameter
        var elapsedTime = System.currentTimeMillis() - this.startTime
        elapsedTime = if (elapsedTime > lerpTime) lerpTime else if (elapsedTime < 0) 0 else elapsedTime
        val lerpFactor = elapsedTime.toDouble() / lerpTime
        val diameter = this.startDiameter + (this.endDiameter - this.startDiameter) * lerpFactor
        this.startDiameter = diameter
        this.setCurrentTime(lerpTime - elapsedTime)
        return diameter
    }

    override fun setDiameter(diameter: Double) {
        this.setDiameter(diameter, diameter, Duration.ofMillis(0))
    }

    override fun setDiameter(diameter: Double, time: Duration) {
        this.setDiameter(this.diameter, diameter, time)
    }

    override fun setDiameter(startDiameter: Double, endDiameter: Double, duration: Duration) {
        check(startDiameter >= 0) { "The start diameter cannot be negative!" }
        check(endDiameter >= 0) { "The end diameter cannot be negative!" }
        val millis = duration.toMillis()

        // Only shrink or grow if needed
        if (millis == 0L || startDiameter == endDiameter) {
            this.startDiameter = endDiameter
            this.endDiameter = endDiameter
            this.updateCurrentTime(0)
            this.broadcast { WorldBorderPacket.UpdateDiameter(endDiameter) }
        } else {
            this.startDiameter = startDiameter
            this.endDiameter = endDiameter
            this.updateCurrentTime(millis)
            this.broadcast { WorldBorderPacket.UpdateLerpedDiameter(startDiameter, endDiameter, millis) }
        }
    }

    val timeRemainingMillis: Long
        get() {
            if (this.startTime == -1L)
                this.updateCurrentTime()
            return (this.endTime - System.currentTimeMillis()).coerceAtLeast(0)
        }

    override fun getTimeRemaining(): Duration = Duration.ofMillis(this.timeRemainingMillis)

    override fun setCenter(x: Double, z: Double) {
        this.centerX = x
        this.centerZ = z
        this.broadcast { WorldBorderPacket.UpdateCenter(this.centerX, this.centerZ) }
    }

    override fun getCenter(): Vector3d = Vector3d(this.centerX, 0.0, this.centerZ)

    val warningTimeSeconds: Int
        get() = this.warningTime.inSeconds.toInt()

    override fun getWarningTime(): Duration = this.warningTime.toJavaDuration()

    override fun setWarningTime(time: Duration) {
        this.warningTime = time.toKotlinDuration()
        this.broadcast { WorldBorderPacket.UpdateWarningTime(this.warningTimeSeconds) }
    }

    override fun getWarningDistance(): Double = this.warningDistance

    override fun setWarningDistance(distance: Double) {
        this.warningDistance = distance
        this.broadcast { WorldBorderPacket.UpdateWarningDistance(distance.roundToInt()) }
    }

    override fun getDamageThreshold(): Double = this.damageThreshold

    override fun setDamageThreshold(distance: Double) {
        this.damageThreshold = distance
    }

    override fun getDamageAmount(): Double = this.damage

    override fun setDamageAmount(damage: Double) {
        this.damage = damage
    }

    fun updateCurrentTime() {
        this.updateCurrentTime(this.lerpTime)
    }

    private fun setCurrentTime(time: Long) {
        this.updateCurrentTime(time)
        this.lerpTime = time
    }

    private fun updateCurrentTime(time: Long) {
        this.startTime = System.currentTimeMillis()
        this.endTime = this.startTime + time
    }
}