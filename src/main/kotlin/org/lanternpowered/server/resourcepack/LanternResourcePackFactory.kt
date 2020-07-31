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

import com.google.common.hash.Hashing
import com.google.common.io.ByteStreams
import org.lanternpowered.api.util.collections.concurrentHashMapOf
import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.server.util.PathUtils
import org.spongepowered.api.resourcepack.ResourcePack
import java.io.FileNotFoundException
import java.net.URI
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths

object LanternResourcePackFactory : ResourcePack.Factory {

    private val resourcePacks = concurrentHashMapOf<String, ResourcePack>()
    private val resourcePacksByKey = concurrentHashMapOf<CacheKey, ResourcePack>()

    // The folder the level resource packs should be stored if
    // they should be hashed, not sure how sponge will handle it
    private val levelPacksFolder = Paths.get("resource-packs")

    private data class CacheKey(private val uri: URI, private val unchecked: Boolean)

    private fun fromUri(uri: URI, unchecked: Boolean): ResourcePack {
        var actualUri = uri
        val key = CacheKey(actualUri, unchecked)
        var resourcePack = this.resourcePacksByKey[key]
        if (resourcePack != null)
            return resourcePack
        val path = actualUri.toString()
        val plainPath = path.replace("[^\\p{L}\\p{Nd}]+".toRegex(), "")
        var hash: String? = null
        var id = "{URI:$path"
        if (!unchecked) {
            val url: URL
            if (path.startsWith("level://")) {
                val path0 = path.replaceFirst("level://".toRegex(), "")
                val file = this.levelPacksFolder.resolve(path0)
                if (!Files.exists(file)) {
                    throw FileNotFoundException("Cannot find the file: \"${file.toAbsolutePath()}\" which" +
                            " is required to generate the hash for \"$path\"")
                }
                url = PathUtils.toURL(file)
                actualUri = file.toUri()
            } else {
                url = PathUtils.toURL(actualUri)
            }
            url.openStream().use { input ->
                @Suppress("DEPRECATION")
                hash = Hashing.sha1().hashBytes(ByteStreams.toByteArray(input)).toString()
            }
            id += ";Hash:" + hash!!
        }
        id += "}"
        resourcePack = LanternResourcePack(actualUri, plainPath, id, hash)
        this.resourcePacks[id] = resourcePack
        this.resourcePacksByKey[key] = resourcePack
        return resourcePack
    }

    fun getById(id: String) = this.resourcePacks[id].asOptional()

    override fun fromUri(uri: URI) = fromUri(uri, false)
    override fun fromUriUnchecked(uri: URI) = fromUri(uri, true)
}

private data class LanternResourcePack(
        private val uri: URI,
        private val id: String,
        private val name: String,
        private val hash: String?
) : ResourcePack {
    override fun getUri() = this.uri
    override fun getName() = this.name
    override fun getId() = this.id
    override fun getHash() = this.hash.asOptional()
}
