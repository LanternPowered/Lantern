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
package org.lanternpowered.server.console

import com.google.inject.Singleton
import net.minecrell.terminalconsole.SimpleTerminalConsole
import net.minecrell.terminalconsole.TerminalConsoleAppender
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.io.IoBuilder
import org.apache.logging.log4j.io.LoggerPrintStream
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.lanternpowered.api.ext.*
import org.lanternpowered.api.util.Named
import org.lanternpowered.server.cause.LanternCauseStack
import org.lanternpowered.server.game.DirectoryKeys
import org.lanternpowered.server.game.Lantern
import org.lanternpowered.server.game.LanternGame
import org.lanternpowered.server.permission.ProxySubject
import org.lanternpowered.server.plugin.InternalPluginsInfo.Implementation
import org.lanternpowered.server.text.LanternTexts
import org.lanternpowered.server.util.PrettyPrinter
import org.lanternpowered.server.util.ThreadHelper
import org.spongepowered.api.Console
import org.spongepowered.api.command.exception.CommandException
import org.spongepowered.api.command.manager.CommandManager
import org.spongepowered.api.plugin.PluginContainer
import org.spongepowered.api.scheduler.TaskExecutorService
import org.spongepowered.api.service.permission.PermissionService
import org.spongepowered.api.service.permission.SubjectReference
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.TextElement
import org.spongepowered.api.text.TextTemplate
import org.spongepowered.api.text.channel.MessageChannel
import org.spongepowered.api.util.Tristate
import java.io.IOException
import java.io.PrintStream
import java.nio.file.Path
import java.time.Duration

@Singleton
object LanternConsole : SimpleTerminalConsole(), ProxySubject, Console {

    @JvmField internal val redirectFqcns = mutableSetOf(
            PrintStream::class.java.name, LoggerPrintStream::class.java.name, PrettyPrinter::class.java.name)

    @JvmField internal val ignoreFqcns = mutableSetOf<String>()

    private const val historyFileName = "console_history.txt"
    private val historySaveInterval = Duration.ofMinutes(2)

    private val logger: Logger by inject()
    private val commandManager: CommandManager by inject()
    private val game: LanternGame by inject()

    @Named(DirectoryKeys.CONFIG) private val configFolder: Path by inject()
    @Named(Implementation.IDENTIFIER) private val pluginContainer: PluginContainer by inject()

    private val consoleHistoryFile: Path by lazy { this.configFolder.resolve(historyFileName) }

    private val syncExecutor: TaskExecutorService = game.syncScheduler.createExecutor(pluginContainer)
    private val lock = Any()

    override val subjectCollectionIdentifier: String get() = PermissionService.SUBJECTS_SYSTEM
    override var internalSubject: SubjectReference? = null

    private var messageChannel: MessageChannel = MessageChannel.toPlayersAndServer()

    @Volatile private var active = false
    private var readerThread: Thread? = null
    private var lineReader: LineReader? = null
    private var lastHistoryWrite = System.currentTimeMillis()

    fun init() {
        initializeSubject()

        // Register the fqcn for the console source
        redirectFqcns.add(this::class.java.name)
        // Register the fqcn for the message channel
        redirectFqcns.add(MessageChannel::class.java.name)
        // Ignore the cause stack as fqcn, stack traces will
        // already be printed nicely with PrettyPrinter
        ignoreFqcns.add(LanternCauseStack::class.java.name)

        System.setOut(IoBuilder.forLogger(this.logger).setLevel(Level.INFO).buildPrintStream())
        System.setErr(IoBuilder.forLogger(this.logger).setLevel(Level.ERROR).buildPrintStream())
    }

    override fun start() {
        this.active = true
        synchronized(this.lock) {
            this.readerThread = ThreadHelper.newThread({ super.start() }, "console").apply {
                isDaemon = true
                start()
            }
        }
    }

    override fun buildReader(builder: LineReaderBuilder): LineReader {
        builder.appName(this.pluginContainer.name)
        builder.completer(ConsoleCommandCompleter())

        val lineReader = super.buildReader(builder).also { this.lineReader = it }
        lineReader.setVariable(LineReader.HISTORY_FILE, this.consoleHistoryFile)
        return lineReader
    }

    override fun isRunning() = this.active

    override fun runCommand(rawCommand: String) {
        var command = rawCommand.trim()
        if (command.isNotEmpty()) {
            command = if (command.startsWith("/")) command.substring(1) else command
            this.syncExecutor.execute {
                try {
                    this.commandManager.process(this, command)
                } catch (e: CommandException) {
                    sendMessage(Text.of("Failed to execute command: $command, reason: ${e.message}"))
                }
            }
        }
        val now = System.currentTimeMillis()
        if (now - this.lastHistoryWrite > this.historySaveInterval.toMillis()) {
            this.lastHistoryWrite = now
            saveHistory()
        }
    }

    override fun shutdown() {
        this.syncExecutor.execute { Lantern.getServer().shutdown() }
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
                this.logger.error("Error while saving the console history!", e)
            }
        }
    }

    override fun getIdentifier() = "Console"

    override fun sendMessage(message: Text) { println(LanternTexts.toLegacy(message)) }

    override fun sendMessages(vararg messages: Text) { messages.forEach(this::sendMessage) }
    override fun sendMessages(messages: Iterable<Text>) { messages.forEach(this::sendMessage) }

    override fun sendMessage(template: TextTemplate) { sendMessage(template.apply().build()) }
    override fun sendMessage(template: TextTemplate, params: Map<String, TextElement>) {
        sendMessage(template.apply(params).build())
    }

    override fun getPermissionDefault(permission: String) = Tristate.TRUE

    override fun getMessageChannel() = this.messageChannel
    override fun setMessageChannel(channel: MessageChannel) { this.messageChannel = channel }
}
