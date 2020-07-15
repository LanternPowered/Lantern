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

import org.lanternpowered.api.catalog.CatalogType
import org.lanternpowered.api.text.translation.Translation
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.world.dimension.LanternDimensionType
import org.lanternpowered.server.world.portal.LanternPortalAgentType
import org.spongepowered.api.ResourceKey
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.entity.living.player.gamemode.GameMode
import org.spongepowered.api.world.SerializationBehavior
import org.spongepowered.api.world.WorldArchetype
import org.spongepowered.api.world.difficulty.Difficulty
import org.spongepowered.api.world.gen.GeneratorType

internal data class LanternWorldArchetype(
        private val key: ResourceKey,
        private val name: Translation,
        private val gameMode: GameMode,
        private val dimensionType: LanternDimensionType<*>,
        private val generatorType: GeneratorType?,
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
        private val isSeedRandomized: Boolean,
        private val generateBonusChest: Boolean,
        private val seed: Long,
        internal val keepSpawnLoaded: Boolean?,
        internal val waterEvaporates: Boolean?,
        internal val allowPlayerRespawns: Boolean?,
        val buildHeight: Int
) : CatalogType by DefaultCatalogType.Named(key, name), WorldArchetype {

    override fun getDifficulty() = this.difficulty
    override fun isEnabled() = this.enabled
    override fun doesLoadOnStartup() = this.loadsOnStartup
    override fun doesKeepSpawnLoaded() = this.keepSpawnLoaded ?: this.dimensionType.keepSpawnLoaded
    override fun doesGenerateSpawnOnLoad() = this.generateSpawnOnLoad
    override fun getSeed() = this.seed
    override fun isSeedRandomized() = this.isSeedRandomized
    override fun getGameMode() = this.gameMode
    override fun getGeneratorType() = this.generatorType ?: this.dimensionType.defaultGeneratorType
    override fun isHardcore() = this.hardcore
    override fun areCommandsEnabled() = this.commandsEnabled
    override fun doesGenerateBonusChest() = this.generateBonusChest
    override fun getDimensionType() = this.dimensionType
    override fun getPortalAgentType() = this.portalAgentType
    override fun getSerializationBehavior() = this.serializationBehavior
    override fun isPVPEnabled() = this.pvpEnabled
    override fun areStructuresEnabled() = this.generateStructures
    override fun getGeneratorSettings(): DataContainer = this.generatorSettings?.copy() ?: getGeneratorType().defaultGeneratorSettings

    fun allowPlayerRespawns() = this.allowPlayerRespawns ?: this.dimensionType.allowsPlayerRespawns
    fun waterEvaporates() = this.waterEvaporates ?: this.dimensionType.doesWaterEvaporate
}
