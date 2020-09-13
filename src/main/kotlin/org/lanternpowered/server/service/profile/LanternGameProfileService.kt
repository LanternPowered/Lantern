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
package org.lanternpowered.server.service.profile

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.spotify.futures.CompletableFutures
import io.netty.buffer.Unpooled
import io.netty.handler.codec.base64.Base64
import io.netty.handler.codec.http.HttpHeaderNames
import org.lanternpowered.api.profile.GameProfile
import org.lanternpowered.api.profile.ProfileProperty
import org.lanternpowered.api.profile.copy
import org.lanternpowered.api.profile.copyWithoutProperties
import org.lanternpowered.api.service.profile.GameProfileService
import org.lanternpowered.api.service.profile.NameHistory
import org.lanternpowered.api.util.collections.removeFirst
import org.lanternpowered.api.util.collections.toImmutableList
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.server.network.http.HttpCode
import org.lanternpowered.server.network.http.NettyHttpClient
import org.lanternpowered.server.network.http.SimpleHttpResponse
import org.lanternpowered.server.util.UUIDHelper
import org.lanternpowered.server.util.gson.fromJson
import java.io.IOException
import java.time.Instant
import java.util.UUID
import java.util.concurrent.CompletableFuture
import kotlin.time.minutes
import kotlin.time.toJavaDuration

class LanternGameProfileService(
        private val httpClient: NettyHttpClient
) : GameProfileService {

    companion object {

        private val gson = Gson()
    }

    private val profileCache = Caffeine.newBuilder()
            .expireAfterWrite(1.minutes.toJavaDuration())
            .build(this::requestProfile)

    private val profileByNameCache = Caffeine.newBuilder()
            .expireAfterWrite(1.minutes.toJavaDuration())
            .build<String, CachedProfile?>()

    private fun handleError(response: SimpleHttpResponse): Nothing {
        if (response.code == HttpCode.NotFound)
            throw IOException("Not found.")
        if (response.body.isNotEmpty()) {
            val json = gson.fromJson<JsonObject>(response.body)
            val error = json.get("error")?.asString
            val message = json.get("errorMessage")?.asString
            if (error != null) {
                when (error) {
                    "IllegalArgumentException" -> throw IOException(IllegalArgumentException(message))
                    else -> throw IOException("$error: $message")
                }
            }
        }
        throw IOException("Invalid response: ${response.code}")
    }

    override fun getBasicProfile(uniqueId: UUID): CompletableFuture<GameProfile?> {
        return this.profileCache.get(uniqueId)!!.thenApply { profile ->
            if (profile == null)
                return@thenApply null
            profile.signed.copyWithoutProperties()
        }
    }

    override fun getBasicProfile(name: String, time: Instant?): CompletableFuture<GameProfile?> {
        var url = "https://api.mojang.com/user/profiles/minecraft/$name"
        if (time != null)
            url += "?at=${time.epochSecond}"
        return this.httpClient.get(url).thenApply { response ->
            when (response.code) {
                HttpCode.Ok -> this.parseProfile(gson.fromJson(response.body))
                HttpCode.NoContent -> null
                else -> this.handleError(response)
            }
        }
    }

    override fun getBasicProfiles(names: Iterable<String>, time: Instant?): CompletableFuture<Map<String, GameProfile>> {
        val nameList = names.toList()
        if (nameList.isEmpty())
            return CompletableFuture.completedFuture(emptyMap())
        return if (time != null) {
            this.requestUniqueIds(nameList, time)
        } else {
            this.requestUniqueIds(nameList)
        }
    }

    private fun requestUniqueIds(names: List<String>): CompletableFuture<Map<String, GameProfile>> {
        // The API limits each request to 10 names
        val futures = names
                .chunked(10) { chunk -> this.requestPartialUniqueIds(chunk) }
        return CompletableFutures.allAsList(futures)
                .thenApply { maps ->
                    val result = HashMap<String, GameProfile>()
                    for (map in maps)
                        result.putAll(map)
                    result
                }
    }

    private fun requestPartialUniqueIds(names: List<String>): CompletableFuture<Map<String, GameProfile>> {
        val bodyJson = JsonArray()
        for (name in names)
            bodyJson.add(name)
        val mutableNames = names.toMutableList()
        val body = gson.toJson(bodyJson)
        val headers = mapOf(HttpHeaderNames.CONTENT_TYPE to "application/json")
        return this.httpClient.post("https://api.mojang.com/profiles/minecraft", body, headers)
                .thenApply { response ->
                    when (response.code) {
                        HttpCode.Ok -> {
                            val array = gson.fromJson<JsonArray>(response.body)
                            array.associate { element ->
                                element as JsonObject
                                val correctedName = element["name"]?.asString
                                val originalName = mutableNames
                                        .removeFirst { name -> name.equals(correctedName, ignoreCase = true) }
                                originalName to this.parseProfile(element)
                            }
                        }
                        HttpCode.NoContent -> emptyMap()
                        else -> this.handleError(response)
                    }
                }
    }

    private fun requestUniqueIds(names: List<String>, time: Instant): CompletableFuture<Map<String, GameProfile>> {
        val futures = names
                .map { name -> name to this.getBasicProfile(name, time) }
                .toMap()
        return CompletableFutures.allAsMap(futures)
                .thenApply { map ->
                    val result = HashMap<String, GameProfile>()
                    for ((key, value) in map) {
                        if (value != null)
                            result[key] = value
                    }
                    result
                }
    }

    override fun getNameHistory(uniqueId: UUID): CompletableFuture<NameHistory?> {
        val flatUniqueId = UUIDHelper.toFlatString(uniqueId)
        return this.httpClient.get("https://api.mojang.com/user/profiles/$flatUniqueId/names").thenApply { response ->
            when (response.code) {
                HttpCode.Ok -> {
                    val array = gson.fromJson<JsonArray>(response.body)
                    val entries = array.asSequence()
                            .map { element ->
                                element as JsonObject
                                val name = element["name"].asString
                                val changedToAt = element["changedToAt"]?.let { Instant.ofEpochMilli(it.asLong) }
                                NameHistory.Entry(name, changedToAt)
                            }
                            .toImmutableList()
                    NameHistory(entries)
                }
                HttpCode.NoContent -> null
                else -> this.handleError(response)
            }
        }
    }

    override fun getProfile(uniqueId: UUID, signed: Boolean): CompletableFuture<GameProfile?> {
        return this.profileCache.get(uniqueId)!!.thenApply { profile ->
            if (profile == null)
                return@thenApply null
            if (signed) {
                profile.notSigned.copy()
            } else {
                profile.signed.copy()
            }
        }
    }

    override fun getProfile(name: String, signed: Boolean): CompletableFuture<GameProfile?> {
        val cachedProfile = this.profileByNameCache.getIfPresent(name)
        if (cachedProfile != null)
            return CompletableFuture.completedFuture(if (signed) cachedProfile.signed else cachedProfile.notSigned)
        return this.getBasicProfile(name).thenCompose { basicProfile ->
            if (basicProfile == null) null else this.getProfile(basicProfile.uniqueId, signed)
        }
    }

    private class CachedProfile(
            val signed: GameProfile
    ) {

        /**
         * A game profile where the properties don't have a signature.
         */
        val notSigned: GameProfile by lazy {
            if (this.signed.propertyMap.isEmpty)
                return@lazy this.signed
            val copy = GameProfile.of(this.signed.uniqueId, this.signed.name.orNull())
            for (property in this.signed.propertyMap.values())
                copy.addProperty(property.withoutSignature())
            copy
        }

        private fun decodeBase64(value: String): String =
                Base64.decode(Unpooled.wrappedBuffer(value.toByteArray(Charsets.UTF_8))).toString(Charsets.UTF_8)

        private fun encodeBase64(value: String): String =
                Base64.encode(Unpooled.wrappedBuffer(value.toByteArray(Charsets.UTF_8))).toString(Charsets.UTF_8)

        private fun ProfileProperty.withoutSignature(): ProfileProperty {
            if (!this.hasSignature())
                return this
            val decoded = decodeBase64(this.value)
            val json = try {
                gson.fromJson<JsonObject>(decoded)
            } catch (t: Throwable) {
                // Not valid json, we can't do anything about this
                return this
            }
            if (!json.has("signatureRequired"))
                return this
            json.remove("signatureRequired")
            val encoded = encodeBase64(gson.toJson(json))
            return ProfileProperty.of(this.name, encoded)
        }
    }

    private fun requestProfile(uniqueId: UUID): CompletableFuture<CachedProfile?> {
        val flatUniqueId = UUIDHelper.toFlatString(uniqueId)
        // Always request signed data, signed data will be removed later,
        // this reduces the number of requests we need to do. Since we are
        // already limited to a single request per minute per unique id,
        // this is a huge benefit.
        val url = "https://sessionserver.mojang.com/session/minecraft/profile/$flatUniqueId?unsigned=false"
        return this.httpClient.get(url).thenApply { response ->
            val cachedProfile = when (response.code) {
                HttpCode.Ok -> CachedProfile(this.parseProfile(gson.fromJson(response.body)))
                HttpCode.NoContent -> null
                else -> this.handleError(response)
            }
            if (cachedProfile != null)
                this.profileByNameCache.put(cachedProfile.signed.name.get(), cachedProfile)
            cachedProfile
        }
    }

    private fun parseProfile(json: JsonObject): GameProfile {
        val uniqueId = UUIDHelper.parseFlatString(json["id"].asString)
        val name = json["name"].asString
        val profile = GameProfile.of(uniqueId, name)

        val propertiesJson = json.getAsJsonArray("properties")
        if (propertiesJson != null) {
            for (propertyJson in propertiesJson) {
                propertyJson as JsonObject
                val propertyName = propertyJson["name"].asString
                val value = propertyJson["value"].asString
                val signature = propertyJson["signature"]?.asString
                profile.addProperty(ProfileProperty.of(propertyName, value, signature))
            }
        }

        return profile
    }
}
