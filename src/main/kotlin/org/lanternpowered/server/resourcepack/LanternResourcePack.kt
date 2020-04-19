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
package org.lanternpowered.server.resourcepack

import org.lanternpowered.api.util.optional.optional
import org.spongepowered.api.resourcepack.ResourcePack
import java.net.URI

data class LanternResourcePack(
        private val uri: URI,
        private val id: String,
        private val name: String,
        private val hash: String?
) : ResourcePack {

    override fun getUri() = this.uri
    override fun getName() = this.name
    override fun getId() = this.id
    override fun getHash() = this.hash.optional()
}
