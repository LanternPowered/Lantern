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
package org.lanternpowered.server.service.world

import org.lanternpowered.api.service.world.WorldStorage
import org.lanternpowered.api.service.world.WorldStorageService
import java.io.Closeable
import java.nio.file.Path
import java.util.UUID
import java.util.concurrent.CompletableFuture

/**
 * A world data service that will handle world data similar like a vanilla
 * minecraft server. A vanilla world should be able to be pasted in and be
 * completely compatible, if the world data is upgraded to the supported
 * version.
 *
 * @property directory The directory where all worlds will be saved
 */
class DefaultWorldStorageService(
        override val directory: Path
) : WorldStorageService, Closeable {

    override val all: Collection<WorldStorage>
        get() = TODO("Not yet implemented")

    override fun get(uniqueId: UUID): WorldStorage? {
        TODO("Not yet implemented")
    }

    override fun getByName(directoryName: String): WorldStorage? {
        TODO("Not yet implemented")
    }

    override fun create(directoryName: String, uniqueId: UUID?): CompletableFuture<WorldStorage?> {
        TODO("Not yet implemented")
    }

    override fun copy(sourceName: String, copyName: String, uniqueId: UUID?): CompletableFuture<WorldStorage?> {
        TODO("Not yet implemented")
    }

    override fun move(oldName: String, newName: String): CompletableFuture<WorldStorage?> {
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }
}
