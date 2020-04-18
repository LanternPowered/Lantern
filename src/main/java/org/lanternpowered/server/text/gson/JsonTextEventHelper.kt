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
package org.lanternpowered.server.text.gson

import com.google.gson.JsonParseException
import org.lanternpowered.api.registry.CatalogRegistry
import org.lanternpowered.api.registry.get
import org.lanternpowered.server.data.io.store.item.ItemStackStore
import org.lanternpowered.server.data.persistence.json.JsonDataFormat
import org.lanternpowered.server.inventory.LanternItemStack
import org.lanternpowered.server.network.item.ItemStackContextualValueType
import org.lanternpowered.server.text.LanternTexts.fromLegacy
import org.lanternpowered.server.text.LanternTexts.toLegacy
import org.lanternpowered.server.text.action.LanternClickActionCallbacks
import org.lanternpowered.server.text.action.LanternClickActionCallbacks.commandPattern
import org.lanternpowered.server.text.action.LanternClickActionCallbacks.getCallbackForUUID
import org.lanternpowered.server.text.action.LanternClickActionCallbacks.getOrCreateIdForCallback
import org.lanternpowered.server.text.translation.TranslationContext
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.DataQuery
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.entity.EntityType
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.text.action.ClickAction
import org.spongepowered.api.text.action.ClickAction.ChangePage
import org.spongepowered.api.text.action.ClickAction.ExecuteCallback
import org.spongepowered.api.text.action.ClickAction.OpenUrl
import org.spongepowered.api.text.action.ClickAction.RunCommand
import org.spongepowered.api.text.action.ClickAction.SuggestCommand
import org.spongepowered.api.text.action.HoverAction
import org.spongepowered.api.text.action.HoverAction.ShowEntity
import org.spongepowered.api.text.action.HoverAction.ShowItem
import org.spongepowered.api.text.action.HoverAction.ShowText
import org.spongepowered.api.text.action.TextActions
import org.spongepowered.api.util.Coerce
import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.net.URI
import java.net.URISyntaxException
import java.util.UUID

internal object JsonTextEventHelper {

    private val SHOW_ENTITY_ID = DataQuery.of("id")
    private val SHOW_ENTITY_TYPE = DataQuery.of("type")
    private val SHOW_ENTITY_NAME = DataQuery.of("name")

    fun parseClickAction(action: String, value: String): ClickAction<*>? {
        when (action) {
            "open_url", "open_file" -> {
                var uri: URI? = null
                if (action == "open_url") {
                    try {
                        uri = URI(value)
                    } catch (ignored: URISyntaxException) {
                    }
                } else {
                    uri = File(value).toURI()
                }
                if (uri != null) {
                    try {
                        return TextActions.openUrl(uri.toURL())
                    } catch (ignored: MalformedURLException) {
                    }
                }
            }
            "run_command" -> {
                // Check for a valid click action callback
                val result = commandPattern.matchEntire(value.trim { it <= ' ' }.toLowerCase())
                if (result != null) {
                    val uniqueId = UUID.fromString(result.groupValues[1])
                    val callback = getCallbackForUUID(uniqueId)
                    if (callback != null) {
                        return TextActions.executeCallback(callback)
                    }
                }
                return TextActions.runCommand(value)
            }
            "suggest_command" -> return TextActions.suggestCommand(value)
            "change_page" -> {
                val page = Coerce.asInteger(value)
                if (page.isPresent)
                    return TextActions.changePage(page.get())
            }
            else -> throw IllegalArgumentException("Unknown click action type: $action")
        }
        return null
    }

    fun parseHoverAction(action: String, value: String?): HoverAction<*> {
        val dataView: DataView
        return when (action) {
            "show_text" -> TextActions.showText(fromLegacy(value!!))
            "show_item" -> {
                dataView = try {
                    JsonDataFormat.readContainer(value, false)
                } catch (e: IOException) {
                    throw JsonParseException("Failed to parse the item data container", e)
                }
                ItemStackContextualValueType.deserializeFromNetwork(dataView)
                val itemStack: ItemStack = ItemStackStore.INSTANCE.deserialize(dataView)
                TextActions.showItem(itemStack.createSnapshot())
            }
            "show_entity" -> {
                dataView = try {
                    JsonDataFormat.readContainer(value, false)
                } catch (e: IOException) {
                    throw JsonParseException("Failed to parse the entity data container", e)
                }
                val uuid = UUID.fromString(dataView.getString(SHOW_ENTITY_ID).get())
                val name = dataView.getString(SHOW_ENTITY_NAME).get()
                val entityType = if (dataView.contains(SHOW_ENTITY_TYPE)) {
                    CatalogRegistry.get<EntityType<*>>(CatalogKey.resolve(dataView.getString(SHOW_ENTITY_TYPE).get()))
                } else null
                TextActions.showEntity(uuid, name, entityType)
            }
            else -> throw IllegalArgumentException("Unknown hover action type: $action")
        }
    }

    fun raw(clickAction: ClickAction<*>): RawAction {
        return when (clickAction) {
            is ChangePage -> RawAction("change_page", clickAction.result.toString())
            is OpenUrl -> {
                val url = clickAction.result
                val scheme = url.protocol
                val host = url.host
                if ("file".equals(scheme, ignoreCase = true) && (host == null || host == "")) {
                    RawAction("open_file", url.file)
                } else {
                    RawAction("open_url", url.toExternalForm())
                }
            }
            is ExecuteCallback -> {
                val uniqueId = getOrCreateIdForCallback(clickAction.result)
                RawAction("run_command", LanternClickActionCallbacks.commandBase + uniqueId.toString())
            }
            is RunCommand -> RawAction("run_command", clickAction.result)
            is SuggestCommand -> RawAction("suggest_command", clickAction.result)
            else -> throw IllegalArgumentException("Unknown click action type: " + clickAction.javaClass.name)
        }
    }

    fun raw(hoverAction: HoverAction<*>): RawAction {
        return when (hoverAction) {
            is ShowText -> RawAction("show_text", toLegacy(hoverAction.result))
            is ShowEntity -> {
                val ref = hoverAction.result
                val dataContainer = DataContainer.createNew()
                        .set(SHOW_ENTITY_ID, ref.uniqueId.toString())
                        .set(SHOW_ENTITY_NAME, ref.name)
                ref.type.ifPresent { type ->
                    dataContainer[SHOW_ENTITY_TYPE] = type.key.toString()
                }
                RawAction("show_entity", JsonDataFormat.writeAsString(dataContainer))
            }
            is ShowItem -> {
                val itemStackSnapshot = hoverAction.result
                val itemStack = itemStackSnapshot.createStack() as LanternItemStack
                val ctx = TranslationContext.current()
                val dataView: DataView
                dataView = if (ctx.forcesTranslations()) {
                    ItemStackContextualValueType.serializeForNetwork(itemStack)
                } else {
                    ItemStackStore.INSTANCE.serialize(itemStack)
                }
                RawAction("show_item", JsonDataFormat.writeAsString(dataView))
            }
            else -> throw IllegalArgumentException("Unknown hover action type: " + hoverAction.javaClass.name)
        }
    }

    internal class RawAction(val action: String, val value: String)
}