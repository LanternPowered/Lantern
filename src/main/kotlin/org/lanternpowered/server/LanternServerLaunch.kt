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

import joptsimple.BuiltinHelpFormatter
import net.minecrell.terminalconsole.TerminalConsoleAppender
import org.apache.logging.log4j.LogManager
import org.lanternpowered.api.Platform
import org.lanternpowered.launch.LanternClassLoader
import org.lanternpowered.launch.transformer.Exclusion
import org.lanternpowered.server.plugin.LanternPluginManager
import org.lanternpowered.server.util.SyncLanternThread

object LanternServerLaunch {

    @JvmStatic
    fun main(args: Array<String>) {
        val classLoader = LanternClassLoader.get()
        classLoader.addTransformerExclusion(Exclusion.forPackage("org.objectweb.asm"))
        classLoader.addTransformerExclusion(Exclusion.forPackage("org.lanternpowered.server.transformer"))
        classLoader.addTransformerExclusion(Exclusion.forClass("org.lanternpowered.server.util.BytecodeUtils"))
        classLoader.addTransformerExclusion(Exclusion.forClass("org.lanternpowered.server.util.UncheckedExceptions"))

        // TODO: Re-add the fast value container transformer, but with a more flexible and type-safe system
        val thread = SyncLanternThread(Runnable { start(args) }, "init")
        thread.start()
    }

    private fun start(arguments: Array<String>) {
        val logger = LogManager.getLogger(LanternPluginManager.LANTERN_ID)

        val options = LaunchOptions.PARSER.parse(*arguments)
        if (options.has(LaunchOptions.HELP)) {
            if (System.console() != null) {
                val terminal = TerminalConsoleAppender.getTerminal()
                if (terminal != null)
                    LaunchOptions.PARSER.formatHelpWith(BuiltinHelpFormatter(terminal.width, 3))
            }
            LaunchOptions.PARSER.printHelpOn(System.err)
            return
        } else if (options.has(LaunchOptions.VERSION)) {
            val pack = Platform::class.java.`package`
            logger.info(pack.implementationTitle + ' ' + pack.implementationVersion)
            logger.info(pack.specificationTitle + ' ' + pack.specificationVersion)
            return
        }

        val server = LanternServerNew()
        server.launch(options)
    }
}
