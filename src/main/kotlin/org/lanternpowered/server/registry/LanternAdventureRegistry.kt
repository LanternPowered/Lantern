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
package org.lanternpowered.server.registry

import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.util.Index
import org.lanternpowered.api.boss.BossBarColor
import org.lanternpowered.api.boss.BossBarFlag
import org.lanternpowered.api.boss.BossBarOverlay
import org.lanternpowered.api.text.format.NamedTextColor
import org.lanternpowered.api.text.format.TextDecoration
import org.spongepowered.api.adventure.AdventureRegistry
import java.util.Optional
import org.lanternpowered.api.util.optional.asOptional

object LanternAdventureRegistry : AdventureRegistry {

    private val decorations = ForIndex(TextDecoration.NAMES)
    private val namedColors = ForIndex(NamedTextColor.NAMES)
    private val clickEventActions = ForIndex(ClickEvent.Action.NAMES)
    private val hoverEventActions = ForIndex(HoverEvent.Action.NAMES)
    private val bossBarColors = ForIndex(BossBarColor.NAMES)
    private val bossBarOverlays = ForIndex(BossBarOverlay.NAMES)
    private val bossBarFlags = ForIndex(BossBarFlag.NAMES)
    private val soundSources = ForIndex(Sound.Source.NAMES)

    override fun getBossBarFlags(): AdventureRegistry.OfType<BossBarFlag> = this.bossBarFlags
    override fun getBossBarOverlays(): AdventureRegistry.OfType<BossBarOverlay> = this.bossBarOverlays
    override fun getBossBarColors(): AdventureRegistry.OfType<BossBarColor> = this.bossBarColors
    override fun getSoundSources(): AdventureRegistry.OfType<Sound.Source> = this.soundSources
    override fun getNamedColors(): AdventureRegistry.OfType<NamedTextColor> = this.namedColors
    override fun getDecorations(): AdventureRegistry.OfType<TextDecoration> = this.decorations
    override fun getClickEventActions(): AdventureRegistry.OfType<ClickEvent.Action> = this.clickEventActions
    override fun getHoverEventActions(): AdventureRegistry.OfType<HoverEvent.Action<*>> = this.hoverEventActions

    private class ForIndex<T : Any>(private val registry: Index<String, T>) : AdventureRegistry.OfType<T> {
        override fun getKey(value: T): String = this.registry.key(value)!!
        override fun getValue(key: String): Optional<T> = this.registry.value(key).asOptional()
        override fun keys(): Set<String> = this.registry.keys()
    }
}
