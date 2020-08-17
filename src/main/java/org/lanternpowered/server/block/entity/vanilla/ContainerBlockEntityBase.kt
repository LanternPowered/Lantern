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
package org.lanternpowered.server.block.entity.vanilla

import org.lanternpowered.api.world.Location
import org.lanternpowered.server.block.entity.BlockEntityCreationData
import org.lanternpowered.server.block.entity.LanternBlockEntity
import org.lanternpowered.server.block.vanilla.container.action.ContainerAnimationAction
import org.lanternpowered.server.inventory.AbstractContainer
import org.lanternpowered.server.inventory.InventoryViewerListener
import org.lanternpowered.server.world.LanternWorldNew
import org.spongepowered.api.entity.living.player.Player
import java.util.HashSet
import kotlin.time.Duration
import kotlin.time.seconds

abstract class ContainerBlockEntityBase(creationData: BlockEntityCreationData) :
        LanternBlockEntity(creationData), InventoryViewerListener {

    protected val viewers = HashSet<Player>()

    /**
     * The delay that will be used to play the open/close sounds.
     */
    private var soundDelay = 0.seconds

    /**
     * Gets the delay that should be used to
     * play the open sound.
     *
     * @return The open sound delay
     */
    protected val openSoundDelay: Duration
        get() = 0.25.seconds

    /**
     * Gets the delay that should be used to
     * play the open sound.
     *
     * @return The open sound delay
     */
    protected val closeSoundDelay: Duration
        get() = 0.5.seconds

    override fun onViewerAdded(viewer: Player, container: AbstractContainer, callback: InventoryViewerListener.Callback) {
        if (!this.viewers.add(viewer) || this.viewers.size != 1)
            return
        this.soundDelay = this.openSoundDelay
        val location = this.location
        val world = location.world as LanternWorldNew
        world.addBlockAction(location.blockPosition, this.block.type, ContainerAnimationAction.OPEN)
    }

    override fun onViewerRemoved(viewer: Player, container: AbstractContainer, callback: InventoryViewerListener.Callback) {
        if (!this.viewers.remove(viewer) || this.viewers.size != 0)
            return
        this.soundDelay = this.closeSoundDelay
        val location = this.location
        val world = location.world as LanternWorldNew
        world.addBlockAction(location.blockPosition, block.type, ContainerAnimationAction.CLOSE)
    }

    /**
     * Plays the open sound at the [Location].
     *
     * @param location The location
     */
    protected abstract fun playOpenSound(location: Location)

    /**
     * Plays the close sound at the [Location].
     *
     * @param location The location
     */
    protected abstract fun playCloseSound(location: Location)

    override fun update(deltaTime: Duration) {
        super.update(deltaTime)

        if (this.soundDelay > Duration.ZERO) {
            this.soundDelay -= deltaTime
            if (this.soundDelay <= Duration.ZERO) {
                val location = this.location
                if (this.viewers.size > 0) {
                    this.playOpenSound(location)
                } else {
                    this.playCloseSound(location)
                }
            }
        }
    }
}
