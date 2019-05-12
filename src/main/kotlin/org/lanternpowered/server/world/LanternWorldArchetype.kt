/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.world

import org.lanternpowered.api.catalog.CatalogType
import org.lanternpowered.api.text.translation.Translation
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.world.dimension.LanternDimensionType
import org.lanternpowered.server.world.portal.LanternPortalAgentType
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.data.DataContainer
import org.spongepowered.api.entity.living.player.gamemode.GameMode
import org.spongepowered.api.world.gen.GeneratorType
import org.spongepowered.api.world.SerializationBehavior
import org.spongepowered.api.world.WorldArchetype
import org.spongepowered.api.world.difficulty.Difficulty
import org.spongepowered.api.world.gen.WorldGeneratorModifier

internal data class LanternWorldArchetype(
        private val key: CatalogKey,
        private val name: Translation,
        private val gameMode: GameMode,
        private val dimensionType: LanternDimensionType<*>,
        private val generatorType: GeneratorType?,
        private val generatorModifiers: Collection<WorldGeneratorModifier>,
        private val generatorSettings: DataContainer?,
        private val difficulty: Difficulty,
        private val serializationBehavior: SerializationBehavior,
        private val portalAgentType: LanternPortalAgentType<*>,
        private val hardcore: Boolean,
        private val enabled: Boolean,
        private val loadsOnStartup: Boolean,
        private val usesMapFeatures: Boolean,
        private val pvpEnabled: Boolean,
        private val generateBonusChest: Boolean,
        private val commandsAllowed: Boolean,
        private val generateSpawnOnLoad: Boolean,
        private val isSeedRandomized: Boolean,
        private val seed: Long,
        internal val keepsSpawnLoaded: Boolean?,
        internal val waterEvaporates: Boolean?,
        internal val allowPlayerRespawns: Boolean?,
        val buildHeight: Int
) : CatalogType by DefaultCatalogType.Named(key, name), WorldArchetype {

    override fun getDifficulty() = this.difficulty
    override fun isEnabled() = this.enabled
    override fun loadOnStartup() = this.loadsOnStartup
    override fun doesKeepSpawnLoaded() = this.keepsSpawnLoaded ?: this.dimensionType.keepSpawnLoaded
    override fun doesGenerateSpawnOnLoad() = this.generateSpawnOnLoad
    override fun getSeed() = this.seed
    override fun isSeedRandomized() = this.isSeedRandomized
    override fun getGameMode() = this.gameMode
    override fun getGeneratorType() = this.generatorType ?: this.dimensionType.defaultGeneratorType
    override fun getGeneratorModifiers() = this.generatorModifiers
    override fun usesMapFeatures() = this.usesMapFeatures
    override fun isHardcore() = this.hardcore
    override fun areCommandsAllowed() = this.commandsAllowed
    override fun doesGenerateBonusChest() = this.generateBonusChest
    override fun getDimensionType() = this.dimensionType
    override fun getPortalAgentType() = this.portalAgentType
    override fun getSerializationBehavior() = this.serializationBehavior
    override fun isPVPEnabled() = this.pvpEnabled
    override fun getGeneratorSettings(): DataContainer = this.generatorSettings?.copy() ?: getGeneratorType().generatorSettings

    fun allowPlayerRespawns() = this.allowPlayerRespawns ?: this.dimensionType.allowsPlayerRespawns
    fun waterEvaporates() = this.waterEvaporates ?: this.dimensionType.doesWaterEvaporate
}
