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
package org.lanternpowered.server.util.gson

import com.google.gson.Gson
import com.google.gson.JsonElement
import org.lanternpowered.api.util.type.typeTokenOf
import java.io.Reader

inline fun <reified T> Gson.fromJson(json: String): T =
        this.fromJson(json, typeTokenOf<T>().type)

inline fun <reified T> Gson.fromJson(reader: Reader): T =
        this.fromJson(reader, typeTokenOf<T>().type)

inline fun <reified T> Gson.fromJson(element: JsonElement): T =
        this.fromJson(element, typeTokenOf<T>().type)
