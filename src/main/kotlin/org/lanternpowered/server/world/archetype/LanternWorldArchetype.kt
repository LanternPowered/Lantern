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
package org.lanternpowered.server.world.archetype

import org.lanternpowered.api.catalog.CatalogType
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.world.dimension.LanternDimensionType
import org.lanternpowered.api.key.NamespacedKey
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.entity.living.player.gamemode.GameMode
import org.spongepowered.api.world.SerializationBehavior
import org.spongepowered.api.world.WorldArchetype
import org.spongepowered.api.world.difficulty.Difficulty
import org.spongepowered.api.world.gen.GeneratorModifierType

internal data class LanternWorldArchetype(
        private val key: NamespacedKey,
        private val gameMode: GameMode,
        private val dimensionType: LanternDimensionType,
        private val generatorModifier: GeneratorModifierType,
        private val generatorSettings: DataContainer?,
        private val difficulty: Difficulty,
        private val serializationBehavior: SerializationBehavior,
        private val portalAgentType: LanternPortalAgentType<*>,
        private val hardcore: Boolean,
        private val enabled: Boolean,
        private val loadsOnStartup: Boolean,
        private val pvpEnabled: Boolean,
        private val generateStructures: Boolean,
        private val commandsEnabled: Boolean,
        private val generateSpawnOnLoad: Boolean,
        private val generateBonusChest: Boolean,
        val seedProvider: SeedProvider,
        internal val keepSpawnLoaded: Boolean?,
        internal val waterEvaporates: Boolean?,
        internal val allowPlayerRespawns: Boolean?,
        val buildHeight: Int
) : CatalogType by DefaultCatalogType(key), WorldArchetype {

    override fun getDifficulty(): Difficulty = this.difficulty
    override fun isEnabled(): Boolean = this.enabled
    override fun doesLoadOnStartup(): Boolean = this.loadsOnStartup
    override fun doesKeepSpawnLoaded(): Boolean = this.keepSpawnLoaded ?: this.dimensionType.keepSpawnLoaded
    override fun doesGenerateSpawnOnLoad(): Boolean = this.generateSpawnOnLoad
    override fun getSeed(): Long = this.seedProvider.get()
    override fun isSeedRandomized(): Boolean = this.seedProvider == SeedProvider.Random
    override fun getGameMode(): GameMode = this.gameMode
    override fun getGeneratorModifier(): GeneratorModifierType = this.generatorModifier
    override fun isHardcore(): Boolean = this.hardcore
    override fun areCommandsEnabled(): Boolean = this.commandsEnabled
    override fun doesGenerateBonusChest(): Boolean = this.generateBonusChest
    override fun getDimensionType(): LanternDimensionType = this.dimensionType
    override fun getSerializationBehavior(): SerializationBehavior = this.serializationBehavior
    override fun isPVPEnabled(): Boolean = this.pvpEnabled
    override fun areStructuresEnabled(): Boolean = this.generateStructures

    // TODO: getGeneratorType().defaultGeneratorSettings
    override fun getGeneratorSettings(): DataContainer = this.generatorSettings?.copy() ?: DataContainer.createNew()

    fun allowPlayerRespawns() = this.allowPlayerRespawns ?: this.dimensionType.allowsPlayerRespawns
    fun doesWaterEvaporate() = this.waterEvaporates ?: this.dimensionType.doesWaterEvaporate
}
