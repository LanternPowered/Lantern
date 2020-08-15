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
package org.lanternpowered.server.advancement

import net.kyori.adventure.text.event.HoverEvent
import org.lanternpowered.api.text.LiteralText
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.appendNewline
import org.lanternpowered.api.text.isNotEmpty
import org.lanternpowered.api.text.textOf
import org.lanternpowered.api.text.translatableTextOf
import org.lanternpowered.api.util.collections.immutableListBuilderOf
import org.lanternpowered.api.util.collections.immutableListOf
import org.spongepowered.api.advancement.DisplayInfo

fun generateToast(name: String, displayInfo: DisplayInfo?): List<Text> {
    if (displayInfo == null)
        return immutableListOf(textOf("Achieved: $name"))
    val builder = immutableListBuilderOf<Text>()
    val type = displayInfo.type as LanternAdvancementType
    builder.add(translatableTextOf("advancements.toast.${type.key.value}").style(type.textStyle))
    builder.add(displayInfo.title)
    return builder.build()
}

fun generateTextRepresentation(name: String, displayInfo: DisplayInfo?): Text {
    if (displayInfo == null)
        return textOf(name)
    val type = displayInfo.type as LanternAdvancementType
    val title = displayInfo.title
    val description = displayInfo.description
    val hoverTextBuilder = LiteralText.builder().append(title.style(type.textStyle))
    if (description.isNotEmpty())
        hoverTextBuilder.appendNewline().append(description)
    val hoverEvent: HoverEvent<*>? = HoverEvent.showText(hoverTextBuilder.build())
    return LiteralText.builder().append("[").append(title.hoverEvent(hoverEvent)).build()
}
