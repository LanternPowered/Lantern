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
package org.lanternpowered.server.console

import net.minecrell.terminalconsole.SimpleTerminalConsole
import net.minecrell.terminalconsole.TerminalConsoleAppender
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.io.IoBuilder
import org.apache.logging.log4j.io.LoggerPrintStream
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.lanternpowered.api.audience.Audience
import org.lanternpowered.api.plugin.name
import org.lanternpowered.api.text.textOf
import org.lanternpowered.server.LanternGame
import org.lanternpowered.server.LanternServerNew
import org.lanternpowered.server.cause.LanternCauseStack
import org.lanternpowered.server.permission.ProxySubject
import org.lanternpowered.server.util.PrettyPrinter
import org.lanternpowered.server.util.ThreadHelper
import org.spongepowered.api.SystemSubject
import org.spongepowered.api.command.exception.CommandException
import org.spongepowered.api.service.permission.PermissionService
import org.spongepowered.api.service.permission.SubjectReference
import org.spongepowered.api.util.Tristate
import java.io.IOException
import java.io.PrintStream
import java.nio.file.Path
import java.time.Duration

class LanternConsole(
        private val server: LanternServerNew
) : SimpleTerminalConsole(), ProxySubject, SystemSubject {

    companion object {

        @JvmField internal val redirectFqcns = mutableSetOf(
                PrintStream::class.java.name, LoggerPrintStream::class.java.name, PrettyPrinter::class.java.name)

        @JvmField internal val ignoreFqcns = mutableSetOf<String>()

        private const val historyFileName = "console_history.txt"
        private val historySaveInterval = Duration.ofMinutes(2)
    }

    private val game: LanternGame
        get() = this.server.game

    private val consoleHistoryFile: Path by lazy { this.server.game.configDirectory.resolve(historyFileName) }
    private val lock = Any()

    override val subjectCollectionIdentifier: String get() = PermissionService.SUBJECTS_SYSTEM
    override var internalSubject: SubjectReference? = null

    @Volatile private var active = false
    private var readerThread: Thread? = null
    private var lineReader: LineReader? = null
    private var lastHistoryWrite = System.currentTimeMillis()

    fun init() {
        // Register the fqcn for the console source
        redirectFqcns.add(this::class.java.name)
        // Register the fqcn for the audience
        redirectFqcns.add(Audience::class.java.name)
        // Ignore the cause stack as fqcn, stack traces will
        // already be printed nicely with PrettyPrinter
        ignoreFqcns.add(LanternCauseStack::class.java.name)

        val logger = this.game.logger
        System.setOut(IoBuilder.forLogger(logger).setLevel(Level.INFO).buildPrintStream())
        System.setErr(IoBuilder.forLogger(logger).setLevel(Level.ERROR).buildPrintStream())
    }

    override fun start() {
        this.active = true
        synchronized(this.lock) {
            this.readerThread = ThreadHelper.newThread({ super.start() }, "console").apply {
                this.isDaemon = true
                start()
            }
        }
    }

    override fun buildReader(builder: LineReaderBuilder): LineReader {
        builder.appName(this.game.lanternPlugin.name)
        builder.completer(ConsoleCommandCompleter(this.game, this))

        val lineReader = super.buildReader(builder).also { this.lineReader = it }
        lineReader.setVariable(LineReader.HISTORY_FILE, this.consoleHistoryFile)
        return lineReader
    }

    override fun isRunning() = this.active

    override fun runCommand(rawCommand: String) {
        var command = rawCommand.trim()
        if (command.isNotEmpty()) {
            command = if (command.startsWith("/")) command.substring(1) else command
            this.game.syncScheduler.submit {
                try {
                    this.game.commandManager.process(this, command)
                } catch (e: CommandException) {
                    sendMessage(textOf("Failed to execute command: $command, reason: ${e.message}"))
                }
            }
        }
        val now = System.currentTimeMillis()
        if (now - this.lastHistoryWrite > historySaveInterval.toMillis()) {
            this.lastHistoryWrite = now
            saveHistory()
        }
    }

    override fun shutdown() {
        this.game.syncScheduler.submit { this.server.shutdown() }
    }

    fun stop() {
        synchronized(this.lock) {
            val readerThread = this.readerThread ?: return
            this.active = false
            // Wait until the read thread finishes
            readerThread.interrupt()
            val terminal = TerminalConsoleAppender.getTerminal()
            terminal?.writer()?.println()
            // Now we can safely save the history
            saveHistory()
            // Cleanup
            this.lineReader = null
            this.readerThread = null
        }
    }

    private fun saveHistory() {
        val lineReader = this.lineReader
        if (lineReader != null) {
            val history = lineReader.history
            try {
                history.save()
            } catch (e: IOException) {
                this.game.logger.error("Error while saving the console history!", e)
            }
        }
    }

    override fun getIdentifier() = "console"
    override fun getPermissionDefault(permission: String) = Tristate.TRUE
}
