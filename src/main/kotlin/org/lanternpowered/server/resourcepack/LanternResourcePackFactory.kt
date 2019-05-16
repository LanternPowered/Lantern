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
package org.lanternpowered.server.resourcepack

import com.google.common.hash.Hashing
import com.google.common.io.ByteStreams
import org.lanternpowered.api.ext.*
import org.lanternpowered.server.util.PathUtils
import org.spongepowered.api.resourcepack.ResourcePack
import java.io.FileNotFoundException
import java.net.URI
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.ConcurrentHashMap

class LanternResourcePackFactory : ResourcePack.Factory {

    private val resourcePacks = ConcurrentHashMap<String, ResourcePack>()
    private val resourcePacksByKey = ConcurrentHashMap<CacheKey, ResourcePack>()

    // The folder the level resource packs should be stored if
    // they should be hashed, not sure how sponge will handle it
    private val levelPacksFolder = Paths.get("resource-packs")

    private data class CacheKey(private val uri: URI, private val unchecked: Boolean)

    private fun fromUri(uri: URI, unchecked: Boolean): ResourcePack {
        var actualUri = uri
        val key = CacheKey(actualUri, unchecked)
        var resourcePack = this.resourcePacksByKey[key]
        if (resourcePack != null) {
            return resourcePack
        }
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
                    throw FileNotFoundException("Cannot find the file: \"" + file.toAbsolutePath() + "\" which" +
                            " is required to generate the hash for \"" + path + "\"")
                }
                url = PathUtils.toURL(file)
                actualUri = file.toUri()
            } else {
                url = PathUtils.toURL(actualUri)
            }
            url.openStream().use { input ->
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

    fun getById(id: String) = this.resourcePacks[id].optional()

    override fun fromUri(uri: URI) = fromUri(uri, false)
    override fun fromUriUnchecked(uri: URI) = fromUri(uri, true)
}
