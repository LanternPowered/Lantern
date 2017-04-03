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
package org.lanternpowered.server.advancement;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector2i;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.network.objects.LocalizedText;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutAdvancements;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public final class AdvancementTree {

    /**
     * The default background of the {@link AdvancementTree}.
     */
    public static final String DEFAULT_BACKGROUND = "minecraft:textures/gui/advancements/backgrounds/stone.png";

    private static final String ROOT_ADVANCEMENT = "root:root";
    private static final AtomicInteger TREE_COUNTER = new AtomicInteger();

    private final Map<Advancement, Vector2i> advancements = new HashMap<>();
    private final int internalId;

    private String backgroundTexture = DEFAULT_BACKGROUND;

    // Changes since the last tick
    private final List<Advancement> addedAdvancements = new ArrayList<>();
    private final List<Advancement> removedAdvancements = new ArrayList<>();

    // All the players tracking this tree
    private final List<LanternPlayer> trackers = new ArrayList<>();

    public AdvancementTree() {
        this.internalId = TREE_COUNTER.getAndIncrement();
        AdvancementTrees.INSTANCE.add(this);
    }

    List<LanternPlayer> getTrackers() {
        return this.trackers;
    }

    /**
     * Adds a new tracker ({@link Player}).
     *
     * @param tracker The tracker
     */
    public void addTracker(Player tracker) {
        checkNotNull(tracker, "tracker");
        final LanternPlayer player = (LanternPlayer) tracker;
        final MessagePlayOutAdvancements message = createAdvancementsMessage(Locale.ENGLISH, player.getAdvancementsProgress(), true);
        if (message != null) {
            player.getConnection().send(message);
        }
        this.trackers.add(player);
    }

    /**
     * Gets the background texture of this tree.
     *
     * @return The background texture
     */
    public String getBackgroundTexture() {
        return this.backgroundTexture;
    }

    /**
     * Adds the {@link Advancement} to this tree at the specified x and y coordinates.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @param advancement The advancement
     */
    public void addAdvancement(int x, int y, Advancement advancement) {
        checkNotNull(advancement, "advancement");
        checkArgument(!this.advancements.containsKey(advancement), "The advancement %s is already present in this tree", advancement.getId());
        this.advancements.put(advancement, new Vector2i(x, y));
        this.addedAdvancements.add(advancement);
    }

    /**
     * Removes the specified {@link Advancement}.
     *
     * @param advancement The advancement
     */
    public void removeAdvancement(Advancement advancement) {
        checkNotNull(advancement, "advancement");
        if (this.advancements.remove(advancement) != null) {
            this.removedAdvancements.add(advancement);
        }
    }

    private String formatId0(String id, @Nullable String extra) {
        if (extra != null) {
            return "tab" + this.internalId + '_' + extra + '_' + id;
        } else {
            return "tab" + this.internalId + '_' + id;
        }
    }

    private String formatId0(String id) {
        return formatId0(id, null);
    }

    private String formatCriterion0(String id) {
        return id + "_crit";
    }

    void clearDirty() {
        this.addedAdvancements.clear();
        this.removedAdvancements.clear();
    }

    final static class GlobalAdvancementsData {

        private final List<String> removed;
        private final List<MessagePlayOutAdvancements.AdvStruct> addedStructs;

        private GlobalAdvancementsData(@Nullable List<String> removed, @Nullable List<MessagePlayOutAdvancements.AdvStruct> addedStructs) {
            this.removed = removed == null ? Collections.emptyList() : removed;
            this.addedStructs = addedStructs == null ? Collections.emptyList() : addedStructs;
        }
    }

    @Nullable
    GlobalAdvancementsData createGlobalData(Locale locale, boolean initial) {
        final String rootId = formatId0(ROOT_ADVANCEMENT);

        final List<String> removed = initial || this.removedAdvancements.isEmpty() ? null :
                this.removedAdvancements.stream().map(Advancement::getId).collect(Collectors.toList());
        List<MessagePlayOutAdvancements.AdvStruct> addedStructs = null;

        final Collection<Advancement> advancements = initial ? this.advancements.keySet() : this.addedAdvancements;
        if (initial || !advancements.isEmpty()) {
            addedStructs = new ArrayList<>();
            if (initial) {

                /*
                addedStructs.add(createStruct(rootId, null, createDisplay(new LocalizedText(Text.of("hoja"), Locale.ENGLISH),
                        ItemTypes.NONE, AdvancementFrameTypes.TASK, this.backgroundTexture, 0, 0)));*/

                addedStructs.add(createStruct(rootId, ":", null));
            }
            boolean first = true;
            for (Advancement advancement : advancements) {
                final String id = formatId0(advancement.getId());

                final String parentId;
                final Optional<Advancement> optParent = advancement.getParent();
                if (optParent.isPresent()) {
                    final Advancement parent = optParent.get();
                    if (this.advancements.containsKey(parent)) {
                        parentId = formatId0(parent.getId());
                    } else {
                        parentId = formatId0(advancement.getId(), "dummy");
                        addedStructs.add(createStruct(parentId, rootId, null));
                    }
                } else {
                    parentId = rootId;
                }

                final Vector2i pos = this.advancements.get(advancement);
                addedStructs.add(createStruct(id, parentId, createDisplay(new LocalizedText(advancement.getTitle(), locale),
                        advancement.getIcon(), advancement.getFrameType(), /*first ? this.backgroundTexture : */null, pos.getX(), pos.getY())));
                first = false;
            }
        }

        return removed == null && addedStructs == null ? null : new GlobalAdvancementsData(removed, addedStructs);
    }

    @Nullable
    private MessagePlayOutAdvancements createAdvancementsMessage(Locale locale, AdvancementsProgress progress, boolean initial) {
        return createAdvancementsMessage(createGlobalData(locale, initial), progress, initial);
    }

    @Nullable
    MessagePlayOutAdvancements createAdvancementsMessage(@Nullable GlobalAdvancementsData advancementsData,
            AdvancementsProgress progress, boolean initial) {
        Map<String, Object2LongMap<String>> progressMap = null;
        if (initial) {
            progressMap = new HashMap<>();
            final String rootId = formatId0(ROOT_ADVANCEMENT);
            progressMap.put(rootId, createProgress(rootId, System.currentTimeMillis()));
        }

        for (Advancement advancement : this.advancements.keySet()) {
            final AdvancementProgress progress1 = progress.getOrNull(advancement);
            if (progress1 != null && (initial || progress1.isDirty())) {
                final long achieveTime = progress1.getAchieveTime();
                if (progressMap == null) {
                    progressMap = new HashMap<>();
                }
                final String id = formatId0(advancement.getId());
                progressMap.put(id, createProgress(id, achieveTime));
            }
        }

        return advancementsData == null && progressMap == null ? null : new MessagePlayOutAdvancements(false,
                advancementsData == null ? Collections.emptyList() : advancementsData.addedStructs,
                advancementsData == null ? Collections.emptyList() : advancementsData.removed,
                progressMap == null ? Collections.emptyMap() : progressMap);
    }

    private Object2LongMap<String> createProgress(String id, long time) {
        return Object2LongMaps.singleton(formatCriterion0(id), time);
    }

    private MessagePlayOutAdvancements.AdvStruct.Display createDisplay(LocalizedText title, ItemType icon,
            AdvancementFrameType frameType, @Nullable String background, int x, int y) {
        return new MessagePlayOutAdvancements.AdvStruct.Display(title, icon, frameType, background, x, y);
    }

    private MessagePlayOutAdvancements.AdvStruct createStruct(String id, @Nullable String parentId,
            @Nullable MessagePlayOutAdvancements.AdvStruct.Display display) {
        final String criterion = formatCriterion0(id);
        final List<String> criteria = Collections.singletonList(criterion);
        final List<List<String>> requirements = Collections.singletonList(criteria);
        return new MessagePlayOutAdvancements.AdvStruct(id, parentId, display, criteria, requirements);
    }
}
