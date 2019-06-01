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
package org.lanternpowered.server.network.channel

import org.spongepowered.api.Platform
import org.spongepowered.api.network.packet.PacketBinding
import org.spongepowered.api.network.packet.PacketHandler
import org.spongepowered.api.network.packet.RequestPacket
import org.spongepowered.api.network.packet.RequestPacketHandler
import org.spongepowered.api.network.packet.ResponsePacket
import org.spongepowered.api.network.packet.ResponsePacketHandler
import org.spongepowered.api.network.packet.TransactionalPacketBinding

internal class LanternTransactionalPacketBinding<M : RequestPacket<R>, R : ResponsePacket>(
        private val opcode: Int,
        private val requestBinding: LanternPacketBinding<M>,
        private val responseBinding: LanternPacketBinding<R>
) : TransactionalPacketBinding<M, R> {

    internal var requestHandler: RequestPacketHandler<M, R>? = null
    internal val responseHandlers = mutableListOf<ResponsePacketHandler<M, R>>()

    override fun getOpcode() = this.opcode
    override fun getRequestBinding() = this.requestBinding
    override fun getResponseBinding() = this.responseBinding

    override fun setRequestHandler(requestHandleSide: Platform.Type, requestHandler: RequestPacketHandler<M, R>) = apply {
        if (requestHandleSide == Platform.Type.SERVER) {
            this.requestHandler = requestHandler
        }
    }

    override fun setRequestHandler(requestHandler: RequestPacketHandler<M, R>) = apply {
        this.requestHandler = requestHandler
    }

    override fun addResponseHandler(responseHandleSide: Platform.Type, responseHandler: PacketHandler<R>): TransactionalPacketBinding<M, R> {
        if (responseHandleSide == Platform.Type.SERVER) {
            addResponseHandler(responseHandler)
        }
        return this
    }

    override fun addResponseHandler(responseHandleSide: Platform.Type, responseHandler: ResponsePacketHandler<M, R>) = apply {
        if (responseHandleSide == Platform.Type.SERVER) {
            this.responseHandlers.add(responseHandler)
        }
    }

    override fun addResponseHandler(responseHandler: PacketHandler<R>) = apply {
        this.responseHandlers.add(ResponsePacketHandler<M, R> { responseMessage, _, connection, side ->
            responseHandler.handleMessage(responseMessage, connection, side)
        })
    }

    override fun addResponseHandler(responseHandler: ResponsePacketHandler<M, R>) = apply {
        this.responseHandlers.add(responseHandler)
    }
}
