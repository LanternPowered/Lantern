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

import com.google.common.collect.ImmutableList
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import it.unimi.dsi.fastutil.objects.Object2LongMap
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.server.LanternGame
import org.lanternpowered.server.advancement.criteria.LanternScoreCriterion
import org.lanternpowered.server.entity.player.LanternPlayer
import org.lanternpowered.server.game.Lantern
import org.lanternpowered.server.network.vanilla.advancement.NetworkAdvancement
import org.lanternpowered.server.network.vanilla.advancement.NetworkAdvancementDisplay
import org.lanternpowered.server.network.vanilla.packet.type.play.AdvancementsPacket
import org.lanternpowered.server.registry.type.advancement.AdvancementRegistry
import org.lanternpowered.server.registry.type.advancement.AdvancementTreeRegistry
import org.lanternpowered.server.util.gson.fromJson
import org.spongepowered.api.advancement.Advancement
import org.spongepowered.api.advancement.AdvancementTree
import org.spongepowered.api.advancement.DisplayInfo
import org.spongepowered.api.advancement.TreeLayoutElement
import org.spongepowered.api.advancement.criteria.AdvancementCriterion
import org.spongepowered.api.advancement.criteria.AndCriterion
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.ArrayList
import java.util.Date
import java.util.HashMap
import java.util.HashSet
import java.util.function.Consumer

class LanternPlayerAdvancements(val player: LanternPlayer) {

    // TODO: Hook saving/loading into user data service

    private val progress: MutableMap<Advancement, LanternAdvancementProgress> = HashMap()

    // All the "dirty" progress that should be updated for the client
    @JvmField
    val dirtyProgress: MutableSet<LanternAdvancementProgress> = HashSet()

    /**
     * Gets the [Path] of the save file.
     *
     * @return The path
     */
    private val savePath: Path
        get() = LanternGame.gameDirectory
                .resolve("advancements")
                .resolve(this.player.uniqueId.toString().toLowerCase() + ".json")

    fun init() {
        init0()

        // Load progress from the file
        val file = savePath
        if (Files.exists(file)) {
            try {
                Files.newBufferedReader(file).use { reader -> loadProgressFromJson(gson.fromJson(reader)) }
            } catch (e: IOException) {
                Lantern.getLogger().error("Failed to load the advancements progress for the player: ${player.uniqueId}", e)
            }
        }
    }

    fun initClient() {
        this.player.connection.send(createUpdateMessage(true)!!)
    }

    fun save() {
        val file = this.savePath
        try {
            // Make sure the parent directory exists
            val parent = file.parent
            if (!Files.exists(parent)) {
                Files.createDirectories(parent)
            }
            Files.newBufferedWriter(file).use { writer -> gson.toJson(saveProgressToJson(), writer) }
        } catch (e: IOException) {
            Lantern.getLogger().error("Failed to save the advancements progress for the player: ${player.uniqueId}", e)
        }
    }

    /**
     * Reloads the progress. This should be called when the
     * [Advancement]s are getting reloaded.
     */
    fun reload() {
        // Save the progress
        val progressMap = saveProgress()
        // Initialize the new advancements
        this.init0()
        // Load the progress again
        this.loadProgress(progressMap)
        // Resend the advancements to the player
        this.initClient()
    }

    private fun init0() {
        this.cleanup()

        // Load all the advancements into this progress tracker
        for (advancement in AdvancementRegistry) {
            val progress = get(advancement)
            // Update the visibility
            progress.dirtyVisibility = true
            this.dirtyProgress.add(progress)
        }
    }

    fun update() {
        val advancementsMessage = this.createUpdateMessage(false)
        if (advancementsMessage != null)
            this.player.connection.send(advancementsMessage)
    }

    fun cleanup() {
        // Clear all the current progress
        this.dirtyProgress.clear()
        this.progress.values.forEach(Consumer { obj: LanternAdvancementProgress -> obj.cleanup() })
        this.progress.clear()
    }

    /**
     * Gets the [LanternAdvancementProgress] for the specified [Advancement].
     *
     * @param advancement The advancement
     * @return The advancement progress
     */
    operator fun get(advancement: Advancement): LanternAdvancementProgress {
        return this.progress.computeIfAbsent(advancement) {
            LanternAdvancementProgress(this, advancement as LanternAdvancement)
        }
    }

    val unlockedAdvancementTrees: Collection<AdvancementTree>
        get() {
            val builder = ImmutableList.builder<AdvancementTree>()
            for (tree in AdvancementTreeRegistry) {
                val advancement = tree.rootAdvancement
                val progress = get(advancement)
                if (!progress.dirtyVisibility && progress.visible ||
                        progress.dirtyVisibility && shouldBeVisible(advancement)) {
                    builder.add(tree)
                }
            }
            return builder.build()
        }

    private fun loadProgress(progressMap: Map<String, Map<String, Instant>>) {
        for (advancement in AdvancementRegistry) {
            val entry = progressMap[advancement.key.toString()]
            if (entry != null)
                this.get(advancement).loadProgress(entry)
        }
    }

    private fun saveProgress(): Map<String, Map<String, Instant>> {
        val progressMap: MutableMap<String, Map<String, Instant>> = HashMap()
        for (entry in this.progress.values) {
            val entryProgress = entry.saveProgress()
            if (entryProgress.isNotEmpty())
                progressMap[entry.advancement.key.toString()] = entryProgress
        }
        return progressMap
    }

    private fun loadProgressFromJson(json: JsonObject) {
        for (advancement in AdvancementRegistry) {
            val entry = json.getAsJsonObject(advancement.key.toString())
            if (entry != null)
                this.loadAdvancementProgressFromJson(get(advancement), entry)
        }
    }

    private fun saveProgressToJson(): JsonObject {
        val json = JsonObject()
        for (entry in this.progress.values) {
            val entryJson = saveAdvancementProgressToJson(entry)
            if (entryJson != null)
                json.add(entry.advancement.key.toString(), entryJson)
        }
        return json
    }

    private fun loadAdvancementProgressFromJson(progress: LanternAdvancementProgress, json: JsonObject) {
        if (!json.has("criteria")) {
            return
        }
        val progressMap: MutableMap<String, Instant> = HashMap()
        // Convert all the string instants
        for ((key, value) in json.getAsJsonObject("criteria").entrySet()) {
            try {
                progressMap[key] = Instant.ofEpochMilli(DATE_TIME_FORMATTER.parse(value.asString).time)
            } catch (e: ParseException) {
                e.printStackTrace()
                progressMap[key] = Instant.now()
            }
        }
        progress.loadProgress(progressMap)
    }

    private fun saveAdvancementProgressToJson(progress: LanternAdvancementProgress): JsonObject? {
        val progressMap = progress.saveProgress()
        val done = progress.achieved()
        if (progressMap.isEmpty() && !done) {
            return null
        }
        val json = JsonObject()
        json.addProperty("done", done)
        if (!progressMap.isEmpty()) {
            val progressJson = JsonObject()
            for ((key, value) in progressMap) {
                progressJson.addProperty(key, DATE_TIME_FORMATTER.format(Date(value.toEpochMilli())))
            }
            json.add("criteria", progressJson)
        }
        return json
    }

    private fun createUpdateMessage(init: Boolean): AdvancementsPacket? {
        if (!init && dirtyProgress.isEmpty()) {
            return null
        }
        val added: MutableList<NetworkAdvancement> = ArrayList()
        val progressMap: MutableMap<String, Object2LongMap<String>> = HashMap()
        val removed: List<String>
        if (init) {
            removed = emptyList()
            for (progress in dirtyProgress) {
                // Update the visibility
                update(progress, null, null, null, true)
            }
            for (progress in progress.values) {
                if (progress.visible) {
                    // Add the advancement
                    added.add(createAdvancement(progress.advancement))
                    val progressMap1 = progress.collectProgress()
                    if (!progressMap1.isEmpty()) {
                        // Fill the progress map
                        progressMap[progress.advancement.key.toString()] = progressMap1
                    }
                }
            }
        } else {
            removed = ArrayList()
            for (progress in dirtyProgress) {
                update(progress, added, removed, progressMap, false)
            }
        }
        // Clear the dirty progress
        dirtyProgress.clear()
        return AdvancementsPacket(init, added, removed, progressMap)
    }

    private fun update(
            progress: LanternAdvancementProgress,
            added: MutableList<NetworkAdvancement>?,
            removed: MutableList<String>?,
            progressByAdvancement: MutableMap<String, Object2LongMap<String>>?,
            force: Boolean
    ) {
        val advancement = progress.advancement
        var updateParentAndChildren = false
        if (progress.dirtyVisibility || force) {
            val visible = shouldBeVisible(advancement)
            progress.dirtyVisibility = false
            if (progress.visible != visible) {
                progress.visible = visible
                if (visible && added != null) {
                    added.add(createAdvancement(advancement))
                    // The progress is now visible, send the complete data
                    if (progressByAdvancement != null) {
                        val progressByCriterion = progress.collectProgress()
                        if (!progressByCriterion.isEmpty())
                            progressByAdvancement[advancement.key.toString()] = progressByCriterion
                        // The progress is already updated, prevent from doing it again
                        progress.dirtyProgress = false
                    }
                } else removed?.add(advancement.key.toString())
                updateParentAndChildren = true
            }
        }
        if (progress.visible && progress.dirtyProgress && progressByAdvancement != null)
            progressByAdvancement[advancement.key.toString()] = progress.collectProgress()
        // Reset dirty state, even if nothing changed
        progress.dirtyProgress = false
        if (updateParentAndChildren) {
            for (child in advancement.children)
                this.update(get(child), added, removed, progressByAdvancement, true)
            val parent = advancement.parent.orNull()
            if (parent != null)
                this.update(get(parent), added, removed, progressByAdvancement, true)
        }
    }

    /**
     * Gets whether the [Advancement] should be visible.
     *
     * @param advancement The advancement
     * @return Whether it should be visible
     */
    private fun shouldBeVisible(advancement: Advancement): Boolean {
        if (!advancement.tree.isPresent)
            return false
        if (this.shouldBeVisibleByChildren(advancement))
            return true
        var display = advancement.displayInfo.orNull()
        if (display == null || display.isHidden)
            return false
        var depth = 2
        var parent = advancement
        while (depth-- > 0) {
            parent = parent.parent.orNull() ?: break
            display = advancement.displayInfo.orNull()
            if (display == null)
                return false
            if (this[advancement].achieved())
                return true
            if (display.isHidden)
                return false
        }
        return false
    }

    private fun shouldBeVisibleByChildren(advancement: Advancement): Boolean {
        if (this[advancement].achieved())
            return true
        return advancement.children
                .any { child -> this.shouldBeVisibleByChildren(child) }
    }

    companion object {

        private val gson = GsonBuilder().setPrettyPrinting().create()
        private val DATE_TIME_FORMATTER = SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z")

        fun createCriteria(criterion: AdvancementCriterion): Pair<List<AdvancementCriterion>, Array<Array<String>>> {
            val criteria = mutableListOf<AdvancementCriterion>()
            val names = mutableListOf<Array<String>>()
            if (criterion is AndCriterion) {
                for (child in criterion.criteria) {
                    if (child is LanternScoreCriterion) {
                        for (id in child.ids)
                            names.add(arrayOf(id))
                    } else {
                        names.add(arrayOf(child.name))
                    }
                    criteria.add(child)
                }
            } else {
                if (criterion is LanternScoreCriterion) {
                    for (id in criterion.ids)
                        names.add(arrayOf(id))
                } else {
                    names.add(arrayOf(criterion.name))
                }
                criteria.add(criterion)
            }
            return criteria to names.toTypedArray()
        }

        private fun createAdvancement(advancement: Advancement): NetworkAdvancement {
            val parentId = advancement.parent.map { obj -> obj.key.formatted }.orNull()
            val background = if (parentId == null) advancement.tree.get().backgroundPath else null
            val displayInfo = advancement.displayInfo.orNull()
            val layoutElement = (advancement as LanternAdvancement).layoutElement
            val criteriaRequirements = advancement.clientCriteria.second
            val criteria = HashSet<String>()
            for (array in criteriaRequirements)
                criteria.addAll(array.asList())
            val display = if (displayInfo != null) createDisplay(displayInfo, layoutElement!!, background) else null
            return NetworkAdvancement(advancement.key.toString(), parentId, display, criteria, criteriaRequirements)
        }

        private fun createDisplay(
                displayInfo: DisplayInfo, layoutElement: TreeLayoutElement, background: String?
        ): NetworkAdvancementDisplay {
            return NetworkAdvancementDisplay(displayInfo.title, displayInfo.description, displayInfo.icon,
                    displayInfo.type, background, layoutElement.position, displayInfo.doesShowToast(), displayInfo.isHidden)
        }
    }
}
