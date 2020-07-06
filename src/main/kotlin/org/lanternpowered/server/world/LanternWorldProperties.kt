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

import org.lanternpowered.api.world.WorldArchetype
import org.lanternpowered.api.world.WorldProperties
import org.spongepowered.api.boss.BossBar
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.entity.living.player.gamemode.GameMode
import org.spongepowered.api.entity.living.trader.WanderingTrader
import org.spongepowered.api.world.SerializationBehavior
import org.spongepowered.api.world.SerializationBehaviors
import org.spongepowered.api.world.WorldBorder
import org.spongepowered.api.world.difficulty.Difficulty
import org.spongepowered.api.world.dimension.DimensionType
import org.spongepowered.api.world.gamerule.GameRule
import org.spongepowered.api.world.gen.GeneratorType
import org.spongepowered.api.world.teleport.PortalAgentType
import org.spongepowered.api.world.weather.Weather
import org.spongepowered.math.vector.Vector3i
import java.time.Duration
import java.util.Optional
import java.util.UUID

class LanternWorldProperties(private val directoryName: String) : WorldProperties {

    private var serializationBehavior = SerializationBehaviors.AUTOMATIC.get()

    override fun getDirectoryName(): String = this.directoryName

    fun load(archetype: WorldArchetype) {

    }

    override fun isHardcore(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setStructuresEnabled(state: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setDayTime(time: Duration) {
        TODO("Not yet implemented")
    }

    override fun setViewDistance(viewDistance: Int) {
        TODO("Not yet implemented")
    }

    override fun doesGenerateBonusChest(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getCustomBossBars(): MutableList<BossBar> {
        TODO("Not yet implemented")
    }

    override fun isEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setGenerateBonusChest(state: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getGameTime(): Duration {
        TODO("Not yet implemented")
    }

    override fun doesGenerateSpawnOnLoad(): Boolean {
        TODO("Not yet implemented")
    }

    override fun areStructuresEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun doesKeepSpawnLoaded(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setWanderingTrader(trader: WanderingTrader?) {
        TODO("Not yet implemented")
    }

    override fun getRunningWeatherDuration(): Duration {
        TODO("Not yet implemented")
    }

    override fun getWanderingTraderSpawnDelay(): Int {
        TODO("Not yet implemented")
    }

    override fun setKeepSpawnLoaded(state: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setSeed(seed: Long) {
        TODO("Not yet implemented")
    }

    override fun getRemainingWeatherDuration(): Duration {
        TODO("Not yet implemented")
    }

    override fun setGameMode(gamemode: GameMode) {
        TODO("Not yet implemented")
    }

    override fun <V : Any> setGameRule(gameRule: GameRule<V>, value: V) {
        TODO("Not yet implemented")
    }

    override fun setHardcore(state: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setDifficulty(difficulty: Difficulty) {
        TODO("Not yet implemented")
    }

    override fun setEnabled(state: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setWanderingTraderSpawnChance(chance: Int) {
        TODO("Not yet implemented")
    }

    override fun getGeneratorType(): GeneratorType {
        TODO("Not yet implemented")
    }

    override fun setSerializationBehavior(behavior: SerializationBehavior) {
        this.serializationBehavior = behavior
    }

    override fun getGameRules(): MutableMap<GameRule<*>, *> {
        TODO("Not yet implemented")
    }

    override fun <V : Any> getGameRule(gameRule: GameRule<V>): V {
        TODO("Not yet implemented")
    }

    override fun setGeneratorType(type: GeneratorType) {
        TODO("Not yet implemented")
    }

    override fun getViewDistance(): Int {
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

    override fun getDifficulty(): Difficulty {
        TODO("Not yet implemented")
    }

    override fun getUniqueId(): UUID {
        TODO("Not yet implemented")
    }

    override fun isPVPEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setLoadOnStartup(state: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getSeed(): Long {
        TODO("Not yet implemented")
    }

    override fun getDayTime(): Duration {
        TODO("Not yet implemented")
    }

    override fun setCustomBossBars(bars: MutableList<BossBar>?) {
        TODO("Not yet implemented")
    }

    override fun setSpawnPosition(position: Vector3i?) {
        TODO("Not yet implemented")
    }

    override fun getSpawnPosition(): Vector3i {
        TODO("Not yet implemented")
    }

    override fun setWanderingTraderSpawnDelay(delay: Int) {
        TODO("Not yet implemented")
    }

    override fun getDimensionType(): DimensionType {
        TODO("Not yet implemented")
    }

    override fun getSerializationBehavior(): SerializationBehavior {
        TODO("Not yet implemented")
    }

    override fun setPVPEnabled(enabled: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getPortalAgentType(): PortalAgentType {
        TODO("Not yet implemented")
    }

    override fun doesLoadOnStartup(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setGenerateSpawnOnLoad(state: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getWeather(): Weather {
        TODO("Not yet implemented")
    }

    override fun getWanderTraderUniqueId(): Optional<UUID> {
        TODO("Not yet implemented")
    }

    override fun getGeneratorSettings(): DataContainer {
        TODO("Not yet implemented")
    }

    override fun areCommandsEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getWorldBorder(): WorldBorder {
        TODO("Not yet implemented")
    }

    override fun getGameMode(): GameMode {
        TODO("Not yet implemented")
    }

    override fun getWanderingTraderSpawnChance(): Int {
        TODO("Not yet implemented")
    }

    override fun isInitialized(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setCommandsEnabled(state: Boolean) {
        TODO("Not yet implemented")
    }
}
