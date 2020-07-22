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

import net.kyori.adventure.key.KeyedValue
import org.lanternpowered.api.namespace.NamespacedKey
import org.lanternpowered.api.boss.BossBar
import org.lanternpowered.api.util.optional.emptyOptional
import org.lanternpowered.api.util.optional.optional
import org.lanternpowered.api.world.World
import org.lanternpowered.api.world.WorldArchetype
import org.lanternpowered.api.world.WorldProperties
import org.lanternpowered.server.config.WorldConfigObject
import org.lanternpowered.server.world.archetype.LanternWorldArchetype
import org.lanternpowered.server.world.dimension.LanternDimensionType
import org.lanternpowered.server.world.portal.LanternPortalAgentType
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.entity.living.player.gamemode.GameMode
import org.spongepowered.api.entity.living.trader.WanderingTrader
import org.spongepowered.api.world.SerializationBehavior
import org.spongepowered.api.world.SerializationBehaviors
import org.spongepowered.api.world.WorldBorder
import org.spongepowered.api.world.difficulty.Difficulty
import org.spongepowered.api.world.dimension.DimensionTypes
import org.spongepowered.api.world.gamerule.GameRule
import org.spongepowered.api.world.gen.GeneratorType
import org.spongepowered.api.world.gen.GeneratorTypes
import org.spongepowered.api.world.teleport.PortalAgentTypes
import org.spongepowered.api.world.weather.Weather
import org.spongepowered.math.vector.Vector3i
import java.time.Duration
import java.util.Optional
import java.util.UUID
import kotlin.random.Random

class LanternWorldProperties(
        private val key: NamespacedKey,
        private val uniqueId: UUID
) : WorldProperties {

    // TODO: Link some settings to config

    private var world: World? = null

    private val config: WorldConfigObject = TODO()

    private var serializationBehavior = SerializationBehaviors.AUTOMATIC.get()
    private var generateBonusChest = false
    private var generateSpawnOnLoad = true
    private var loadOnStartup = false
    private var keepSpawnLoaded = false
    private var structuresEnabled = true
    private var commandsEnabled = true
    private var viewDistance = 10 // TODO: Proper default
    private var seed = Random.nextLong()
    private var spawnPosition = Vector3i.ZERO // TODO: Calculate based on generated terrain
    private var portalAgentType: LanternPortalAgentType<*> = PortalAgentTypes.DEFAULT.get() as LanternPortalAgentType<*>
    private var generatorType = GeneratorTypes.DEFAULT.get()
    private var dimensionType: LanternDimensionType<*> = DimensionTypes.OVERWORLD.get() as LanternDimensionType<*>

    // TODO: Move some things to game rules?

    override fun getWorld(): Optional<World> = this.world.optional()

    /**
     * Sets the world instance attached to these
     * properties, internal use only.
     */
    fun setWorld(world: World?) { this.world = world }

    /**
     * The maximum build height in this world.
     */
    var maxBuildHeight: Int
        get() = this.config.maxBuildHeight
        set(value) { this.config.maxBuildHeight = value }

    /**
     * Whether player are able to respawn in this world when they die.
     */
    var allowsPlayerRespawns: Boolean
        get() = this.config.allowPlayerRespawns
        set(value) { this.config.allowPlayerRespawns = value }

    /**
     * Whether water evaporates when being placed in this world.
     */
    var doesWaterEvaporate: Boolean = false

    override fun getKey(): NamespacedKey = this.key
    override fun getUniqueId(): UUID = this.uniqueId

    fun loadFrom(archetype: WorldArchetype) {
        archetype as LanternWorldArchetype
        this.serializationBehavior = archetype.serializationBehavior
        this.config.hardcore = archetype.isHardcore
        this.generateBonusChest = archetype.doesGenerateBonusChest()
        this.generateSpawnOnLoad = archetype.doesGenerateSpawnOnLoad()
        this.loadOnStartup = archetype.doesLoadOnStartup()
        this.keepSpawnLoaded = archetype.doesKeepSpawnLoaded()
        this.structuresEnabled = archetype.areStructuresEnabled()
        this.commandsEnabled = archetype.areCommandsEnabled()
        this.allowsPlayerRespawns = archetype.allowPlayerRespawns()
        this.config.pvp = archetype.isPVPEnabled
        this.config.enabled = archetype.isEnabled
        this.seed = archetype.seed
        this.config.difficulty = archetype.difficulty
        this.config.gameMode.mode = archetype.gameMode
        this.config.maxBuildHeight = archetype.buildHeight
        this.generatorType = archetype.generatorType
        this.dimensionType = archetype.dimensionType
    }

    override fun isInitialized(): Boolean = true // TODO: When are properties "not" initialized?

    override fun getSerializationBehavior(): SerializationBehavior = this.serializationBehavior
    override fun setSerializationBehavior(behavior: SerializationBehavior) { this.serializationBehavior = behavior }

    override fun isHardcore(): Boolean = this.config.hardcore
    override fun setHardcore(state: Boolean) { this.config.hardcore = state }

    override fun doesGenerateBonusChest(): Boolean = this.generateBonusChest
    override fun setGenerateBonusChest(state: Boolean) { this.generateBonusChest = state }

    override fun isPVPEnabled(): Boolean = this.config.pvp
    override fun setPVPEnabled(enabled: Boolean) { this.config.pvp = enabled }

    override fun isEnabled(): Boolean = this.config.enabled
    override fun setEnabled(state: Boolean) { this.config.enabled = state }

    override fun areStructuresEnabled(): Boolean = this.structuresEnabled
    override fun setStructuresEnabled(state: Boolean) { this.structuresEnabled = state }

    override fun doesGenerateSpawnOnLoad(): Boolean = this.generateSpawnOnLoad
    override fun setGenerateSpawnOnLoad(state: Boolean) { this.generateSpawnOnLoad = state }

    override fun doesLoadOnStartup(): Boolean = this.loadOnStartup
    override fun setLoadOnStartup(state: Boolean) { this.loadOnStartup }

    override fun getSeed(): Long = this.seed
    override fun setSeed(seed: Long) { this.seed = seed }

    override fun areCommandsEnabled(): Boolean = this.commandsEnabled
    override fun setCommandsEnabled(state: Boolean) { this.commandsEnabled = state }

    override fun doesKeepSpawnLoaded(): Boolean = this.keepSpawnLoaded
    override fun setKeepSpawnLoaded(state: Boolean) { this.keepSpawnLoaded = state }

    override fun getSpawnPosition(): Vector3i = this.spawnPosition
    override fun setSpawnPosition(position: Vector3i) { this.spawnPosition = position }

    override fun getGameMode(): GameMode = this.config.gameMode.mode
    override fun setGameMode(gameMode: GameMode) { this.config.gameMode.mode = gameMode }

    override fun getPortalAgentType(): LanternPortalAgentType<*> = this.portalAgentType

    override fun getViewDistance(): Int = this.viewDistance

    override fun setViewDistance(viewDistance: Int) {
        check(viewDistance > 0) { "The view distance must be greater than zero." }
        this.viewDistance = viewDistance
    }

    override fun getDifficulty(): Difficulty = this.config.difficulty

    override fun setDifficulty(difficulty: Difficulty) {
        this.config.difficulty = difficulty

        // TODO: Send update to players
    }

    override fun getDimensionType(): LanternDimensionType<*> = this.dimensionType

    override fun getGeneratorType(): GeneratorType = this.generatorType

    override fun setGeneratorType(type: GeneratorType) {
        this.generatorType = type
        // TODO: Apply new terrain generator
    }

    override fun getWorldBorder(): WorldBorder {
        TODO("Not yet implemented")
    }

    override fun setDayTime(time: Duration) {
        TODO("Not yet implemented")
    }

    override fun getGameTime(): Duration {
        TODO("Not yet implemented")
    }

    override fun getRunningWeatherDuration(): Duration {
        TODO("Not yet implemented")
    }

    override fun getRemainingWeatherDuration(): Duration {
        TODO("Not yet implemented")
    }

    override fun <V : Any> setGameRule(gameRule: GameRule<V>, value: V) {
        TODO("Not yet implemented")
    }

    override fun getGameRules(): MutableMap<GameRule<*>, *> {
        TODO("Not yet implemented")
    }

    override fun <V : Any> getGameRule(gameRule: GameRule<V>): V {
        TODO("Not yet implemented")
    }

    override fun setWeather(weather: Weather) {
        TODO("Not yet implemented")
    }

    override fun setWeather(weather: Weather, duration: Duration) {
        TODO("Not yet implemented")
    }

    override fun setGeneratorSettings(generatorSettings: DataContainer) {
        TODO("Not yet implemented")
    }

    override fun getDayTime(): Duration {
        TODO("Not yet implemented")
    }

    override fun getWeather(): Weather {
        TODO("Not yet implemented")
    }

    override fun getGeneratorSettings(): DataContainer {
        TODO("Not yet implemented")
    }

    // TODO: What are these "custom" boss bars? Are these just boss
    //  bars that should persist? Or something else?

    override fun getCustomBossBars(): List<KeyedValue<BossBar>> = emptyList()
    override fun setCustomBossBars(bars: List<KeyedValue<BossBar>>?) {}

    // TODO: Introduce wandering entities? Maybe a different system
    //  compared on vanilla? More than one trader?

    override fun setWanderingTrader(trader: WanderingTrader?) {}
    override fun getWanderingTraderSpawnDelay(): Int = Int.MAX_VALUE
    override fun setWanderingTraderSpawnDelay(delay: Int) {}
    override fun getWanderTraderUniqueId(): Optional<UUID> = emptyOptional()
    override fun getWanderingTraderSpawnChance(): Int = 1
    override fun setWanderingTraderSpawnChance(chance: Int) {}
}
