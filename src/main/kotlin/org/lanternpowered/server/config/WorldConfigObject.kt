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
package org.lanternpowered.server.config

import org.lanternpowered.api.util.Tristate
import org.lanternpowered.api.world.difficulty.Difficulties
import org.spongepowered.api.entity.living.player.gamemode.GameModes

class ChunksObject : ConfigObject() {

    // TODO
    var test by setting(default = true, name = "test")

    companion object : Factory<ChunksObject> by { ChunksObject() }
}

class WorldGameModeObject : ConfigObject() {

    var mode by setting(default = GameModes.SURVIVAL.get(), name = "mode",
            description = "The default game mode of this world.")

    var forced by setting(default = true, name = "forced",
            description = "Whether players are forced to the default game mode on join.")

    companion object : Factory<WorldGameModeObject> by { WorldGameModeObject() }
}

class WorldConfigObject : ConfigObject() {

    val chunks by ChunksObject.with(name = "chunks")

    var pvp by setting(default = true, name = "pvp",
            description = "Enable if this world allows PVP combat.")

    var hardcore by setting(default = false, name = "hardcore",
            description = "Enable if this world should use the hardcore mode.")

    var enabled by setting(default = true, name = "enabled",
            description = "Enable if this world should be allowed to load.")

    var loadOnStartup by setting(default = false, name = "load-on-startup",
            description = "Enable if this world should load on startup.")

    var keepSpawnLoaded by setting(default = true, name = "keep-spawn-loaded",
            description = "Enable if this world's spawn should remain loaded with no players.")

    var waterEvaporates by setting(default = Tristate.UNDEFINED, name = "water-evaporates",
            description = "Enable if the water in this world should evaporate.")

    var allowPlayerRespawns by setting(default = true, name = "allow-player-respawns",
            description = "Enable if the player may respawn in this world.")

    var maxBuildHeight by setting(default = 255, name = "max-build-height",
            description = "The maximum build height of this world.")

    var lowHorizon by setting(default = false, name = "low-horizon",
            description = "Enable this if the the horizon of this world should be lower, from y = 63 to y = 0")

    var difficulty by setting(default = Difficulties.NORMAL.get(), name = "difficulty",
            description = "The difficulty of this world.")

    val gameMode by WorldGameModeObject.with(name = "game-mode")

    var viewDistance by setting(default = ViewDistance.USE_GLOBAL_SETTING, name = "view-distance",
            description = "The view distance. The value must be between ${ViewDistance.MINIMUM} and ${ViewDistance.MAXIMUM} (inclusive). Or\n" +
                          "set the value to ${ViewDistance.USE_GLOBAL_SETTING} if you want to use the setting defined in the global config.")

    companion object : Factory<WorldConfigObject> by { WorldConfigObject() }
}
