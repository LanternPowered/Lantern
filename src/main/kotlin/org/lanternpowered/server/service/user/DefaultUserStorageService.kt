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
package org.lanternpowered.server.service.user

import com.github.benmanes.caffeine.cache.Caffeine
import org.lanternpowered.api.service.user.UserStorage
import org.lanternpowered.api.service.user.UserStorageService
import org.lanternpowered.server.util.UUIDHelper
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID
import kotlin.streams.asSequence

/**
 * A world data service that will handle user data similar like a vanilla
 * minecraft server. A vanilla user should be able to be pasted in and be
 * completely compatible, if the user data is upgraded to the supported
 * version.
 *
 * The directory structure will be the following for example:
 *  /users
 *     /data               - A directory containing all the user data
 *        /playerdata
 *        /advancements
 *        /statistics
 *
 * @property directory The directory where all the users will be stored
 */
class DefaultUserStorageService(private val directory: Path) : UserStorageService {

    private val dataDirectory = this.directory.resolve("data")

    private val cache = Caffeine.newBuilder().weakValues()
            .build<UUID, UserStorage> { uniqueId -> LanternUserStorage(uniqueId, this.dataDirectory) }

    override fun get(uniqueId: UUID): UserStorage = this.cache.get(uniqueId)!!

    override fun exists(uniqueId: UUID): Boolean = LanternUserStorage.exists(uniqueId, this.dataDirectory)

    override fun getIfPresent(uniqueId: UUID): UserStorage? {
        if (!this.exists(uniqueId))
            return null
        return this.get(uniqueId)
    }

    override fun sequence(): Sequence<UserStorage> {
        val dataDirectory = this.dataDirectory.resolve(LanternUserStorage.DATA_DIRECTORY)
        if (!Files.exists(dataDirectory))
            return emptySequence()
        return Files.walk(dataDirectory)
                .asSequence()
                .map { file ->
                    if (!Files.isRegularFile(file))
                        return@map null
                    var name = file.fileName.toString()
                    if (!name.endsWith(".dat"))
                        return@map null
                    name = name.substring(0, name.length - ".dat".length)
                    val uniqueId = UUIDHelper.tryParse(name)
                            ?: return@map null
                    this.get(uniqueId)
                }
                .filterNotNull()
    }
}
