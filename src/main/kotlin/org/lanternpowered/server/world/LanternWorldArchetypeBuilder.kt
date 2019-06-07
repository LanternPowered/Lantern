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

import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.ext.*
import org.lanternpowered.api.plugin.PluginContainer
import org.lanternpowered.api.text.translation.FixedTranslation
import org.lanternpowered.server.catalog.AbstractCatalogBuilder
import org.lanternpowered.server.game.registry.type.world.GeneratorModifierRegistryModule
import org.lanternpowered.server.world.dimension.LanternDimensionType
import org.lanternpowered.server.world.portal.LanternPortalAgentType
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.data.DataContainer
import org.spongepowered.api.entity.living.player.gamemode.GameMode
import org.spongepowered.api.entity.living.player.gamemode.GameModes
import org.spongepowered.api.text.translation.Translation
import org.spongepowered.api.world.DimensionType
import org.spongepowered.api.world.DimensionTypes
import org.spongepowered.api.world.SerializationBehavior
import org.spongepowered.api.world.SerializationBehaviors
import org.spongepowered.api.world.WorldArchetype
import org.spongepowered.api.world.difficulty.Difficulties
import org.spongepowered.api.world.difficulty.Difficulty
import org.spongepowered.api.world.gen.GeneratorType
import org.spongepowered.api.world.storage.WorldProperties
import org.spongepowered.api.world.teleport.PortalAgentType
import org.spongepowered.api.world.teleport.PortalAgentTypes
import java.util.UUID
import java.util.concurrent.ThreadLocalRandom

class LanternWorldArchetypeBuilder : AbstractCatalogBuilder<WorldArchetype, WorldArchetype.Builder>(), WorldArchetype.Builder {

    private lateinit var gameMode: GameMode
    private lateinit var difficulty: Difficulty
    private lateinit var portalAgentType: LanternPortalAgentType<*>
    private lateinit var generatorModifiers: Collection<WorldGeneratorModifier>
    private lateinit var serializationBehavior: SerializationBehavior
    private lateinit var dimensionType: LanternDimensionType<*>

    // If not specified, fall back to dimension default
    private var generatorType: GeneratorType? = null
    private var generatorSettings: DataContainer? = null
    private var keepsSpawnLoaded: Boolean? = null
    private var waterEvaporates: Boolean? = null // Non-sponge property
    private var allowPlayerRespawns: Boolean? = null // Non-sponge property

    private var buildHeight = 0 // Non-sponge property
    private var hardcore = false
    private var enabled = false
    private var loadsOnStartup = false
    private var usesMapFeatures = false
    private var generateBonusChest = false
    private var commandsAllowed = false // No builder method available
    private var pvpEnabled = false
    private var generateSpawnOnLoad = false
    private var seed: Long? = null

    init {
        reset()
    }

    override fun from(archetype: WorldArchetype) = apply {
        archetype as LanternWorldArchetype
        this.difficulty = archetype.difficulty
        this.hardcore = archetype.isHardcore
        this.enabled = archetype.isEnabled
        this.gameMode = archetype.gameMode
        this.keepsSpawnLoaded = archetype.keepsSpawnLoaded
        this.usesMapFeatures = archetype.usesMapFeatures()
        this.generatorModifiers = archetype.generatorModifiers
        this.dimensionType = archetype.dimensionType
        this.generatorType = archetype.generatorType
        this.generatorSettings = archetype.generatorSettings
        this.generateBonusChest = archetype.doesGenerateBonusChest()
        this.commandsAllowed = archetype.areCommandsAllowed()
        this.waterEvaporates = archetype.waterEvaporates
        this.buildHeight = archetype.buildHeight
        this.allowPlayerRespawns = archetype.allowPlayerRespawns
        this.pvpEnabled = archetype.isPVPEnabled
        this.generateSpawnOnLoad = archetype.doesGenerateSpawnOnLoad()
        this.portalAgentType = archetype.portalAgentType
        this.seed = if (archetype.isSeedRandomized) null else archetype.seed
    }

    override fun from(properties: WorldProperties) = apply {
        properties as LanternWorldProperties
        this.difficulty = properties.difficulty
        this.hardcore = properties.isHardcore
        this.enabled = properties.isEnabled
        this.gameMode = properties.gameMode
        this.keepsSpawnLoaded = properties.doesKeepSpawnLoaded()
        this.usesMapFeatures = properties.usesMapFeatures()
        this.seed = properties.seed
        this.generatorModifiers = properties.generatorModifiers
        this.dimensionType = properties.dimensionType
        this.generatorType = properties.generatorType
        this.generatorSettings = properties.generatorSettings.copy()
        this.generateBonusChest = properties.doesGenerateBonusChest()
        this.waterEvaporates = properties.doesWaterEvaporate()
        this.buildHeight = properties.buildHeight
        this.pvpEnabled = properties.isPVPEnabled
        this.generateSpawnOnLoad = properties.doesGenerateSpawnOnLoad()
        this.portalAgentType = properties.portalAgentType
    }

    override fun enabled(state: Boolean) = apply { this.enabled = state }
    override fun loadsOnStartup(state: Boolean) = apply { this.loadsOnStartup = state }
    override fun keepsSpawnLoaded(state: Boolean) = apply { this.keepsSpawnLoaded = state }
    override fun generateSpawnOnLoad(state: Boolean) = apply { this.generateSpawnOnLoad = state }
    override fun seed(seed: Long) = apply { this.seed = seed }
    override fun randomSeed() = apply { this.seed = null }
    override fun gameMode(gameMode: GameMode) = apply { this.gameMode = gameMode }
    override fun generator(type: GeneratorType) = apply { this.generatorType = type }
    override fun dimension(type: DimensionType) = apply { this.dimensionType = type as LanternDimensionType<*> }
    override fun difficulty(difficulty: Difficulty) = apply { this.difficulty = difficulty }
    override fun usesMapFeatures(enabled: Boolean) = apply { this.usesMapFeatures = enabled }
    override fun hardcore(enabled: Boolean) = apply { this.hardcore = enabled }
    override fun generatorSettings(settings: DataContainer) = apply { this.generatorSettings = settings }
    override fun portalAgent(type: PortalAgentType) = apply { this.portalAgentType = type as LanternPortalAgentType<*> }
    override fun pvp(enabled: Boolean) = apply { this.pvpEnabled = enabled }
    override fun commandsAllowed(state: Boolean) = apply { this.commandsAllowed = state }
    override fun generateBonusChest(state: Boolean) = apply { this.generateBonusChest = state }
    override fun serializationBehavior(behavior: SerializationBehavior) = apply { this.serializationBehavior = behavior }

    override fun generatorModifiers(vararg modifiers: WorldGeneratorModifier) = apply {
        val entries = hashSetOf<WorldGeneratorModifier>()
        val registry = GeneratorModifierRegistryModule
        for (modifier in modifiers) {
            check(registry.get(modifier.key).isPresent) { "Modifier not registered: ${modifier.key} of type ${modifier.javaClass.name}" }
            entries.add(modifier)
        }
        this.generatorModifiers = entries
    }

    fun waterEvaporates(evaporates: Boolean) = apply { this.waterEvaporates = evaporates }

    fun buildHeight(buildHeight: Int) = apply {
        check(buildHeight <= 256) { "the build height cannot be greater then 256" }
        this.buildHeight = buildHeight
    }

    override fun build(key: CatalogKey, name: Translation) = throw UnsupportedOperationException("Overridden")

    override fun build(): WorldArchetype {
        val key = this.key ?: run {
            val plugin = CauseStack.currentOrEmpty().first<PluginContainer>()
            val pluginId = plugin?.id ?: "unknown"
            CatalogKey.of(pluginId, UUID.randomUUID().toString())
        }
        val name = this.name ?: FixedTranslation(key.value)
        return LanternWorldArchetype(key, name,
                allowPlayerRespawns = this.allowPlayerRespawns,
                buildHeight = this.buildHeight,
                commandsAllowed = this.commandsAllowed,
                difficulty = this.difficulty,
                dimensionType = this.dimensionType,
                enabled = this.enabled,
                gameMode = this.gameMode,
                generateBonusChest = this.generateBonusChest,
                generateSpawnOnLoad = this.generateSpawnOnLoad,
                generatorSettings = this.generatorSettings,
                generatorModifiers = this.generatorModifiers,
                generatorType = this.generatorType,
                hardcore = this.hardcore,
                isSeedRandomized = this.seed == null,
                keepsSpawnLoaded = this.keepsSpawnLoaded,
                loadsOnStartup = this.loadsOnStartup,
                seed = this.seed ?: ThreadLocalRandom.current().nextLong(),
                serializationBehavior = this.serializationBehavior,
                portalAgentType = this.portalAgentType,
                pvpEnabled = this.pvpEnabled,
                usesMapFeatures = this.usesMapFeatures,
                waterEvaporates = this.waterEvaporates
        )
    }

    override fun reset() = apply {
        this.usesMapFeatures = true
        this.gameMode = GameModes.SURVIVAL
        this.difficulty = Difficulties.NORMAL
        this.portalAgentType = PortalAgentTypes.DEFAULT as LanternPortalAgentType<*>
        this.hardcore = false
        this.keepsSpawnLoaded = null
        this.loadsOnStartup = false
        this.generateSpawnOnLoad = true
        this.enabled = true
        this.generateBonusChest = false
        this.commandsAllowed = true
        this.dimensionType = DimensionTypes.OVERWORLD as LanternDimensionType<*>
        this.generatorModifiers = emptySet()
        this.seed = null
        this.generatorType = null
        this.generatorSettings = null
        this.waterEvaporates = null
        this.buildHeight = 256
        this.serializationBehavior = SerializationBehaviors.AUTOMATIC
    }
}
