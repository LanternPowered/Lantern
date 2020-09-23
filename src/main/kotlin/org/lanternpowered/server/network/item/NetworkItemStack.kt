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
package org.lanternpowered.server.network.item

import io.netty.handler.codec.DecoderException
import io.netty.handler.codec.EncoderException
import org.lanternpowered.api.data.persistence.DataQuery
import org.lanternpowered.api.data.persistence.DataView
import org.lanternpowered.api.data.persistence.DataViewSafetyMode
import org.lanternpowered.api.data.persistence.dataQueryOf
import org.lanternpowered.api.data.persistence.getOrCreateView
import org.lanternpowered.api.item.ItemType
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.locale.Locale
import org.lanternpowered.api.text.toText
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.server.data.io.store.item.ItemStackStore
import org.lanternpowered.server.inventory.LanternItemStack
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.value.ContextualValueCodec
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.text.NetworkText
import org.lanternpowered.api.data.Keys
import org.spongepowered.api.data.persistence.DataContainer
import kotlin.random.Random

/**
 * A codec which can be used to encode/decode [ItemStack]s. This codec translates
 * server side item stacks to ones that can be displayed on the client.
 */
object NetworkItemStack : ContextualValueCodec<ItemStack?> {

    // TODO: Minimize the amount of data send within certain contexts, e.g.
    //   in survival the doesn't need to sync all item data back to
    //   the server, so we only need to send necessary data
    //   In creative, this is not possible because of how the creative
    //   inventory works

    private val INTERNAL_ID = dataQueryOf("\$iid")
    private val UNIQUE_ID = dataQueryOf("\$uid")
    private val TEMP_NAME = dataQueryOf("\$name")
    private val TEMP_LORE = dataQueryOf("\$lore")

    override fun write(ctx: CodecContext, buf: ByteBuffer, value: ItemStack?) {
        if (value == null || value.isEmpty) {
            buf.writeBoolean(false)
        } else {
            val networkType = NetworkItemTypeRegistry.getByType(value.type)
                    ?: throw EncoderException("The item type isn't registered: " + value.type.key)
            val dataView = this.serialize(networkType, value, ctx.session.locale)
            buf.writeBoolean(true)
            buf.writeVarInt(networkType.networkId)
            buf.writeByte(value.quantity.toByte())
            buf.writeDataView(dataView.getView(ItemStackStore.TAG).orNull())
        }
    }

    /**
     * Serializes the [ItemStack] into a [DataView] that can be send over the network.
     */
    fun serialize(value: ItemStack, locale: Locale): DataView {
        val networkType = NetworkItemTypeRegistry.getByType(value.type)
                ?: throw EncoderException("The item type isn't registered: " + value.type.key)
        return this.serialize(networkType, value, locale)
    }

    private fun serialize(networkType: NetworkItemType, value: ItemStack, locale: Locale): DataView {
        value as LanternItemStack
        val dataView = ItemStackStore.INSTANCE.serialize(value)
        val tagView = dataView.getOrCreateView(ItemStackStore.TAG)
        val displayView by lazy { tagView.getOrCreateView(ItemStackStore.DISPLAY) }
        // Add our custom internal id to identify the item, this must always
        // be applied. Otherwise custom items may be seen as e.g. valid crafting
        // ingredients, because the recipes ignore the tag because it's not
        // present on the vanilla items.
        tagView[INTERNAL_ID] = networkType.internalId
        if (value.maxStackQuantity == 1 && networkType.originalMaxStackSize != 1) {
            // Add an unique id to prevent the item from stacking, this reduces
            // the glitches that otherwise occur when changing the stack size on the
            // server side.
            tagView[UNIQUE_ID] = Random.nextLong(Long.MAX_VALUE)
        }
        // Inject the custom display name or translations, if needed
        var displayName = value.get(Keys.DISPLAY_NAME).orNull()
        if (displayName == null && !networkType.isVanilla)
            displayName = value.type.toText()
        if (displayName != null) {
            val rendered = NetworkText.renderer.renderIfNeeded(displayName, locale)
            if (rendered != null) {
                val oldDisplayName = displayView.get(ItemStackStore.NAME).orNull()
                if (oldDisplayName != null)
                    displayView[TEMP_NAME] = oldDisplayName
                displayView[ItemStackStore.NAME] = NetworkText.serialize(rendered)
            }
        }
        // Inject the lore translations
        val lore = value.get(Keys.LORE).orNull()
        if (lore != null) {
            val rendered = NetworkText.renderer.renderListIfNeeded(lore, locale)
            if (rendered != null) {
                val oldLore = displayView.get(ItemStackStore.LORE).orNull()
                if (oldLore != null)
                    displayView[TEMP_LORE] = oldLore
                displayView[ItemStackStore.LORE] = rendered.map(NetworkText::serialize)
            }
        }
        return dataView
    }

    override fun read(ctx: CodecContext, buf: ByteBuffer): ItemStack? {
        val isPresent = buf.readBoolean()
        if (!isPresent)
            return null
        val networkId = buf.readVarInt()
        if (networkId == -1)
            return null
        val quantity = buf.readByte().toInt()
        val tagView = buf.readDataView()
        var itemType: ItemType? = null
        if (tagView != null) {
            val internalId = tagView.getInt(INTERNAL_ID).orNull()
            if (internalId != null) {
                val networkType = NetworkItemTypeRegistry.getByInternalId(internalId)
                        ?: throw DecoderException("Received ItemStack with unknown internal id: $internalId")
                itemType = networkType.type
            }
            this.readTagView(tagView)
        }
        // Must be a vanilla item type.
        if (itemType == null) {
            val networkType = NetworkItemTypeRegistry.getByNetworkId(networkId)
            itemType = networkType?.type
            if (itemType == null) {
                // We know the id, but it's not implemented yet
                if (NetworkItemTypeRegistry.getVanillaKeyByNetworkId(networkId) != null)
                    return null
                throw DecoderException("Received ItemStack with unknown network id: $networkId")
            }
        }
        val dataView = DataContainer.createNew(DataViewSafetyMode.NO_DATA_CLONED)
        dataView[ItemStackStore.QUANTITY] = quantity
        if (tagView != null)
            dataView[ItemStackStore.TAG] = tagView
        val itemStack = LanternItemStack(itemType, quantity)
        ItemStackStore.INSTANCE.deserialize(itemStack, dataView)
        return itemStack
    }

    fun deserialize(dataView: DataView): LanternItemStack {
        val tagView = dataView.getView(ItemStackStore.TAG).orNull()
        if (tagView != null) {
            val internalId = tagView.getInt(INTERNAL_ID).orNull()
            if (internalId != null) {
                val networkType = NetworkItemTypeRegistry.getByInternalId(internalId)
                        ?: throw DecoderException("Received ItemStack with unknown internal id: $internalId")
                dataView[ItemStackStore.IDENTIFIER] = networkType.type.key.formatted
            }
            this.readTagView(tagView)
        }
        return ItemStackStore.INSTANCE.deserialize(dataView)
    }

    private fun readTagView(tagView: DataView) {
        val displayView = tagView.getView(ItemStackStore.DISPLAY).orNull()

        tagView.remove(INTERNAL_ID)
        tagView.remove(UNIQUE_ID)

        fun copyTemp(view: DataView, temp: DataQuery, destination: DataQuery) =
                view[temp].ifPresent { value -> view[destination] = value }

        if (displayView != null) {
            copyTemp(displayView, TEMP_NAME, ItemStackStore.NAME)
            copyTemp(displayView, TEMP_LORE, ItemStackStore.LORE)
        }
    }
}
