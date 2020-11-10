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
package org.lanternpowered.server.network.vanilla.packet.handler.play

import io.netty.util.AttributeKey
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.lanternpowered.api.Lantern
import org.lanternpowered.api.cause.CauseContextKeys
import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.cause.withFrame
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.textOf
import org.lanternpowered.api.text.translatableTextOf
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.api.util.text.normalizeSpaces
import org.lanternpowered.server.LanternServer
import org.lanternpowered.server.entity.player.LanternPlayer
import org.lanternpowered.server.network.NetworkContext
import org.lanternpowered.server.network.NetworkSession
import org.lanternpowered.server.network.attribute.computeIfAbsent
import org.lanternpowered.server.network.packet.PacketHandler
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientSendChatMessagePacket
import org.lanternpowered.server.permission.Permissions
import org.lanternpowered.server.text.ClickActionCallbacks
import org.spongepowered.api.command.CommandCause
import java.util.regex.Pattern
import kotlin.math.max
import kotlin.time.milliseconds

object ClientSendChatMessageHandler : PacketHandler<ClientSendChatMessagePacket> {

    override fun handle(ctx: NetworkContext, packet: ClientSendChatMessagePacket) {
        val session = ctx.session

        val player = ctx.session.player
        player.resetIdleTime()
        player.resetOpenedSignPosition()

        val message = packet.message

        CauseStack.withFrame { frame ->
            frame.pushCause(player)
            frame.addContext(CauseContextKeys.PLAYER, player)

            // Attempt to handle the callback
            val callbackResult = ClickActionCallbacks.parseCallbackCommand(message)
            if (callbackResult != null) {
                val consumer = callbackResult.orNull()
                if (consumer == null) {
                    player.sendMessage(textOf("The callback you provided was not valid. Keep in mind that callbacks will expire " +
                            "after ${ClickActionCallbacks.expireDuration.inMinutes} minutes, so you might want to consider clicking " +
                            "faster next time!"))
                } else {
                    val commandCause = CommandCause.create()
                    consumer.accept(commandCause)
                }
            }

            if (!this.isAllowedString(message)) {
                ctx.session.close(translatableTextOf("multiplayer.disconnect.illegal_characters"))
                return
            }

            this.checkSpam(session, player)

            val possibleCommand = message.normalizeSpaces()
            if (possibleCommand.startsWith("/")) {
                Lantern.commandManager.process(player, possibleCommand.substring(1))
            } else {
                this.handleChatMessage(ctx.server, player, message)
            }
        }
    }

    private val urlPattern = Pattern.compile("(?:(https?)://)?([-\\w_.]+\\.\\w{2,})(/\\S*)?")

    private fun handleChatMessage(server: LanternServer, player: LanternPlayer, message: String) {
        var text: Text = textOf(message)
        if (server.config.chat.clickableUrls && player.hasPermission(Permissions.Chat.FORMAT_URLS)) {
            // Make the urls clickable
            text = text.replaceText(this.urlPattern) { url -> url.clickEvent(ClickEvent.openUrl(url.content())) }
        }
        player.simulateChat(text, CauseStack.currentCause)
    }

    private fun isAllowedString(string: String): Boolean =
            string.toCharArray().none(this::isAllowedCharacter)

    private fun isAllowedCharacter(character: Char): Boolean =
            character != LegacyComponentSerializer.SECTION_CHAR && character >= ' ' && character != '\u007F'

    // region Checking for spam

    private val chatDataKey: AttributeKey<ChatData> = AttributeKey.valueOf("chat-data")

    private data class ChatData(
            var threshold: Double = 0.0,
            var lastTime: Long = -1
    )

    private fun checkSpam(session: NetworkSession, player: LanternPlayer) {
        val chatData = session.attr(this.chatDataKey).computeIfAbsent { ChatData() }
        val currentTime = System.currentTimeMillis()
        if (chatData.lastTime != -1L) {
            val elapsedTime = (currentTime - chatData.lastTime).milliseconds.inSeconds
            chatData.threshold = max(0.0, chatData.threshold - elapsedTime)
        }
        chatData.lastTime = currentTime
        chatData.threshold += 1.0 / session.server.config.chat.maxMessagesPerSecond
        if (player.hasPermission(Permissions.Chat.BYPASS_SPAM_CHECK) || chatData.threshold <= 1.0)
            return
        session.close(translatableTextOf("disconnect.spam"))
    }

    // endregion
}
