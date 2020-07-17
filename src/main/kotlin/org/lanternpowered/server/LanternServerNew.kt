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
package org.lanternpowered.server

import org.lanternpowered.api.Server
import org.lanternpowered.api.cause.CauseStackManager
import org.lanternpowered.api.entity.player.Player
import org.lanternpowered.api.service.world.WorldStorageService
import org.lanternpowered.api.world.WorldManager
import org.lanternpowered.server.cause.LanternCauseStackManager
import org.lanternpowered.server.event.lifecycle.LanternStartedServerEvent
import org.lanternpowered.server.event.lifecycle.LanternStartingServerEvent
import org.lanternpowered.server.service.world.DefaultWorldStorageService
import org.lanternpowered.server.util.SyncLanternThread
import org.lanternpowered.server.world.LanternTeleportHelper
import org.lanternpowered.server.world.LanternWorldManager
import org.spongepowered.api.Game
import org.spongepowered.api.entity.living.player.server.ServerPlayer
import org.spongepowered.api.profile.GameProfileManager
import org.spongepowered.api.resourcepack.ResourcePack
import org.spongepowered.api.scheduler.Scheduler
import org.spongepowered.api.scoreboard.Scoreboard
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.channel.MessageChannel
import org.spongepowered.api.user.UserManager
import org.spongepowered.api.world.TeleportHelper
import org.spongepowered.api.world.storage.ChunkLayout
import java.net.InetSocketAddress
import java.util.Optional
import java.util.UUID
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class LanternServerNew : Server {

    private val game: LanternGame = LanternGame
    private lateinit var worldManager: LanternWorldManager
    private lateinit var ioExecutor: ExecutorService

    /**
     * Initializes the game and starts the server.
     */
    fun init() {
        this.game.init()
        this.game.setServer(this)

        // TODO: Make configurable with launch options?
        val worldsDirectory = this.game.gameDirectory.resolve("worlds")
        val worldStorageService = this.game.serviceProvider.register<WorldStorageService> {
            this.game.lanternPlugin to DefaultWorldStorageService(worldsDirectory)
        }

        this.ioExecutor = Executors.newCachedThreadPool() // TODO: Use a specific amount of threads
        this.worldManager = LanternWorldManager(this.ioExecutor, worldStorageService)

        // TODO: Do things

        this.game.eventManager.post(LanternStartingServerEvent(this.game, this))

        // TODO: Do things

        this.game.eventManager.post(LanternStartedServerEvent(this.game, this))
    }

    override fun isDedicatedServer(): Boolean = true
    override fun getGame(): Game = this.game
    override fun getWorldManager(): WorldManager = this.worldManager
    override fun getTeleportHelper(): TeleportHelper = LanternTeleportHelper

    override fun onMainThread(): Boolean =
            Thread.currentThread() is SyncLanternThread

    override fun getOnlinePlayers(): Collection<ServerPlayer> {
        TODO("Not yet implemented")
    }

    override fun setMessageChannel(channel: MessageChannel?) {
        TODO("Not yet implemented")
    }

    override fun setBroadcastChannel(channel: MessageChannel?) {
        TODO("Not yet implemented")
    }

    override fun getTicksPerSecond(): Double = 20.0

    override fun getPlayerIdleTimeout(): Int {
        TODO("Not yet implemented")
    }

    override fun setPlayerIdleTimeout(timeout: Int) {
        TODO("Not yet implemented")
    }

    override fun getUserManager(): UserManager {
        TODO("Not yet implemented")
    }

    override fun getDefaultResourcePack(): Optional<ResourcePack> {
        TODO("Not yet implemented")
    }

    override fun getServerScoreboard(): Optional<Scoreboard> {
        TODO("Not yet implemented")
    }

    override fun getScheduler(): Scheduler {
        TODO("Not yet implemented")
    }

    override fun getCauseStackManager(): CauseStackManager = LanternCauseStackManager

    override fun sendMessage(message: Text?) {
        TODO("Not yet implemented")
    }

    override fun getMaxPlayers(): Int {
        TODO("Not yet implemented")
    }

    override fun getBoundAddress(): Optional<InetSocketAddress> {
        TODO("Not yet implemented")
    }

    override fun getMessageChannel(): MessageChannel {
        TODO("Not yet implemented")
    }

    override fun hasWhitelist(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getPlayer(uniqueId: UUID): Optional<Player> {
        TODO("Not yet implemented")
    }

    override fun getPlayer(name: String): Optional<Player> {
        TODO("Not yet implemented")
    }

    override fun getMotd(): Text {
        TODO("Not yet implemented")
    }

    override fun getBroadcastChannel(): MessageChannel {
        TODO("Not yet implemented")
    }

    override fun shutdown() {
        TODO("Not yet implemented")
    }

    override fun shutdown(kickMessage: Text) {
        TODO("Not yet implemented")
    }

    override fun getOnlineMode(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getGameProfileManager(): GameProfileManager {
        TODO("Not yet implemented")
    }

    override fun setHasWhitelist(enabled: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getRunningTimeTicks(): Int {
        TODO("Not yet implemented")
    }

    override fun getChunkLayout(): ChunkLayout {
        TODO("Not yet implemented")
    }
}
