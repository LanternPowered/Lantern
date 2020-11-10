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
package org.lanternpowered.server.network.protocol

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap

/**
 * Represents the state of connection.
 *
 * @property id The id of the protocol state
 * @property protocol The protocol instance (packet registries) bound to the state
 */
enum class ProtocolState(
        val id: Int,
        val protocol: Protocol
) {

    /**
     * The handshake state (initial).
     */
    Handshake(-1, HandshakeProtocol),

    /**
     * The normal play state.
     */
    Play(0, PlayProtocol),

    /**
     * The status (or ping) state.
     */
    Status(1, StatusProtocol),

    /**
     * The login state.
     */
    Login(2, LoginProtocol);

    companion object {

        private val lookup = Int2ObjectOpenHashMap<ProtocolState>()

        /**
         * Gets the protocol state using it's id.
         *
         * @param id the id
         * @return the protocol state
         */
        fun byId(id: Int): ProtocolState? = this.lookup[id]

        init {
            for (state in values())
                this.lookup[state.id] = state
        }
    }
}
