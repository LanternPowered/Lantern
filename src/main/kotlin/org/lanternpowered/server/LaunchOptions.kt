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

import joptsimple.ArgumentAcceptingOptionSpec
import joptsimple.OptionParser
import joptsimple.OptionSpec

object LaunchOptions {

    val PARSER: OptionParser = OptionParser()

    val HELP: OptionSpec<Void> =
            PARSER.acceptsAll(listOf("help", "h", "?"), "Show this help text").forHelp()

    val VERSION: OptionSpec<Void> =
            PARSER.acceptsAll(listOf("version", "v"), "Display the Lantern version")

    val PLUGINS_DIRECTORY: ArgumentAcceptingOptionSpec<String> =
            PARSER.acceptsAll(listOf("plugins-directory", "plugins-dir"), "The path to the plugins directory").withRequiredArg()

    val CONFIG_DIRECTORY: ArgumentAcceptingOptionSpec<String> =
            PARSER.acceptsAll(listOf("config-directory", "config-dir"), "The path to the config directory").withRequiredArg()

    val WORLDS_DIRECTORY: ArgumentAcceptingOptionSpec<String> =
            PARSER.acceptsAll(listOf("worlds-directory", "worlds-dir"), "The path to the worlds directory").withRequiredArg()

    val PORT: ArgumentAcceptingOptionSpec<Int> =
            PARSER.acceptsAll(listOf("port", "p"), "The port to launch the server on").withRequiredArg().ofType(Int::class.java)
}
