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

import com.flowpowered.math.vector.Vector2d;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.network.objects.LocalizedText;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutAdvancements;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;

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

public final class AdvancementTree extends Styleable {

    public static AdvancementTreeBuilder builder() {
        return new AdvancementTreeBuilder();
    }

    private static final String ROOT_ADVANCEMENT = "root:root";
    private static final AtomicInteger TREE_COUNTER = new AtomicInteger();

    private final Map<Advancement, Vector2d> advancements = new HashMap<>();
    private final int internalId;

    private final String background;

    // Changes since the last tick
    private final List<Advancement> addedAdvancements = new ArrayList<>();
    private final List<Advancement> removedAdvancements = new ArrayList<>();

    // All the players tracking this tree
    private final List<LanternPlayer> trackers = new ArrayList<>();
    private final List<LanternPlayer> addedTrackers = new ArrayList<>();

    @Nullable private final Advancement rootAdvancement;
    private final Vector2d rootPosition;

    private double xOffset;
    private double yOffset;

    private boolean refresh;

    AdvancementTree(String pluginId, String id, String name, Text title, Text description, ItemStackSnapshot icon, FrameType frameType,
            String background, @Nullable Advancement rootAdvancement, Vector2d rootPosition, boolean showToast) {
        super(pluginId, id, name, title, description, icon, frameType, showToast);
        this.background = background;
        this.rootAdvancement = rootAdvancement;
        this.rootPosition = rootPosition;
        this.internalId = TREE_COUNTER.getAndIncrement();
        if (rootAdvancement != null) {
            this.advancements.put(rootAdvancement, rootPosition);
        }
        AdvancementTrees.INSTANCE.add(this);
    }

    List<LanternPlayer> getTrackers() {
        return this.trackers;
    }

    List<LanternPlayer> getUpdateTrackers() {
        return this.trackers.stream().filter(p -> !this.addedTrackers.contains(p)).collect(Collectors.toList());
    }

    /**
     * Adds a new tracker ({@link Player}).
     *
     * @param tracker The tracker
     */
    public void addTracker(Player tracker) {
        checkNotNull(tracker, "tracker");
        final LanternPlayer player = (LanternPlayer) tracker;
        if (this.trackers.add(player)) {
            this.addedTrackers.add(player);
            final MessagePlayOutAdvancements message = createAdvancementsMessage(Locale.ENGLISH, player.getAdvancementsProgress(), INITIALIZE);
            if (message != null) {
                player.getConnection().send(message);
            }
        }
    }

    /**
     * Adds a new tracker ({@link Player}).
     *
     * @param tracker The tracker
     */
    public void addRawTracker(Player tracker) {
        checkNotNull(tracker, "tracker");
        final LanternPlayer player = (LanternPlayer) tracker;
        if (this.trackers.add(player)) {
            this.addedTrackers.add(player);
        }
    }

    /**
     * Removes the tracker ({@link Player}).
     *
     * @param tracker The tracker
     */
    public void removeTracker(Player tracker) {
        checkNotNull(tracker, "tracker");
        final LanternPlayer player = (LanternPlayer) tracker;
        if (this.trackers.remove(player)) {
            this.addedTrackers.remove(player);
            final MessagePlayOutAdvancements message = createAdvancementsMessage(Locale.ENGLISH, player.getAdvancementsProgress(), REMOVE);
            if (message != null) {
                player.getConnection().send(message);
            }
        }
    }

    void removeRawTracker(Player tracker) {
        final LanternPlayer player = (LanternPlayer) tracker;
        this.trackers.remove(player);
    }

    /**
     * Gets the root {@link Advancement} if present.
     *
     * @return The root advancement
     */
    public Optional<Advancement> getRootAdvancement() {
        return Optional.ofNullable(this.rootAdvancement);
    }

    /**
     * Gets the background texture of this tree.
     *
     * @return The background texture
     */
    public String getBackground() {
        return this.background;
    }

    /**
     * Adds the {@link Advancement} to this tree at the specified x and y coordinates.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @param advancement The advancement
     */
    public void addAdvancement(double x, double y, Advancement advancement) {
        checkNotNull(advancement, "advancement");
        checkArgument(!this.advancements.containsKey(advancement), "The advancement %s is already present in this tree", advancement.getId());
        if (x < 0) {
            final double x1 = Math.abs(x);
            if (x1 > this.xOffset) {
                this.refresh = true;
                this.xOffset = x1;
            }
        }
        if (y < 0) {
            final double y1 = Math.abs(y);
            if (y1 > this.yOffset) {
                this.refresh = true;
                this.yOffset = y1;
            }
        }
        this.advancements.put(advancement, new Vector2d(x, y));
        this.addedAdvancements.add(advancement);
    }

    /**
     * Removes the specified {@link Advancement}.
     *
     * @param advancement The advancement
     */
    public void removeAdvancement(Advancement advancement) {
        checkNotNull(advancement, "advancement");
        final Vector2d position = this.advancements.remove(advancement);
        if (position != null) {
            this.removedAdvancements.add(advancement);
            if (position.getX() < 0 || position.getY() < 0) {
                double xOffset = 0;
                double yOffset = 0;
                for (Vector2d pos : this.advancements.values()) {
                    final double x = pos.getX();
                    final double y = pos.getY();
                    if (x < 0) {
                        xOffset = Math.max(Math.abs(x), this.xOffset);
                    }
                    if (y < 0) {
                        yOffset = Math.max(Math.abs(y), this.yOffset);
                    }
                }
                if (xOffset != this.xOffset || yOffset != this.yOffset) {
                    this.refresh = true;
                    this.xOffset = xOffset;
                    this.yOffset = yOffset;
                }
            }
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
        this.addedTrackers.clear();
        this.refresh = false;
    }

    boolean isRefreshRequired() {
        return this.refresh;
    }

    final static class GlobalAdvancementsData {

        private final List<String> removed;
        private final List<MessagePlayOutAdvancements.AdvStruct> addedStructs;

        private GlobalAdvancementsData(@Nullable List<String> removed, @Nullable List<MessagePlayOutAdvancements.AdvStruct> addedStructs) {
            this.removed = removed == null ? Collections.emptyList() : removed;
            this.addedStructs = addedStructs == null ? Collections.emptyList() : addedStructs;
        }
    }

    static final int UPDATE = 0;
    static final int INITIALIZE = 1;
    static final int REFRESH = 2;
    static final int REMOVE = 3;

    @Nullable
    GlobalAdvancementsData createGlobalData(Locale locale, int state) {
        final String rootId;
        if (this.rootAdvancement != null) {
            rootId = formatId0(this.rootAdvancement.getId());
        } else {
            rootId = formatId0(ROOT_ADVANCEMENT);
        }

        List<String> removed = null;
        if (state != INITIALIZE && !this.removedAdvancements.isEmpty()) {
            removed = this.removedAdvancements.stream().map(a -> formatId0(a.getId())).collect(Collectors.toList());
        }
        if (state == REFRESH || state == REMOVE) {
            final List<String> removed1 = this.advancements.keySet().stream().map(a -> formatId0(a.getId())).collect(Collectors.toList());
            if (removed == null) {
                removed = removed1;
            } else {
                removed.addAll(removed1);
            }
            if (state == REMOVE) {
                return removed.isEmpty() ? null : new GlobalAdvancementsData(removed, null);
            }
        }
        List<MessagePlayOutAdvancements.AdvStruct> addedStructs = null;

        final Collection<Advancement> advancements;
        if (state == UPDATE) {
            advancements = this.addedAdvancements;
        } else {
            advancements = this.advancements.keySet();
        }

        if (state != UPDATE || (this.rootAdvancement != null && advancements.contains(this.rootAdvancement))) {
            addedStructs = new ArrayList<>();
            addedStructs.add(createStruct(rootId, null, createDisplay(
                    new LocalizedText(getTitle(), Locale.ENGLISH),
                    new LocalizedText(getDescription(), Locale.ENGLISH),
                    getIcon(), getFrameType(), this.background,
                    this.rootPosition.getX() + this.xOffset,
                    this.rootPosition.getY() + this.yOffset,
                    doesShowToast()), Collections.singletonList(Collections.singletonList(AdvancementCriterion.DUMMY))));
        }

        if (!advancements.isEmpty()) {
            if (addedStructs == null) {
                addedStructs = new ArrayList<>();
            }
            for (Advancement advancement : advancements) {
                if (advancement == this.rootAdvancement) {
                    continue;
                }
                final String id = formatId0(advancement.getId());

                final String parentId;
                final Optional<Advancement> optParent = advancement.getParent();
                if (optParent.isPresent()) {
                    final Advancement parent = optParent.get();
                    if (this.advancements.containsKey(parent)) {
                        parentId = formatId0(parent.getId());
                    } else {
                        parentId = rootId;
                    }
                } else {
                    parentId = rootId;
                }
                final List<List<String>> criteria;
                if (!advancement.getCriteria().isEmpty()) {
                    criteria = new ArrayList<>();
                    for (List<AdvancementCriterion> list : advancement.getCriteria()) {
                        criteria.add(list.stream().map(c -> c.id).collect(Collectors.toList()));
                    }
                } else {
                    criteria = Collections.singletonList(Collections.singletonList(AdvancementCriterion.DUMMY));
                }

                final Vector2d pos = this.advancements.get(advancement);
                addedStructs.add(createStruct(id, parentId, createDisplay(
                        new LocalizedText(advancement.getTitle(), locale),
                        new LocalizedText(advancement.getDescription(), locale),
                        advancement.getIcon(), advancement.getFrameType(), null,
                        pos.getX() + this.xOffset, pos.getY() + this.yOffset,
                        advancement.doesShowToast()), criteria));
            }
        }

        return removed == null && addedStructs == null ? null : new GlobalAdvancementsData(removed, addedStructs);
    }

    @Nullable
    private MessagePlayOutAdvancements createAdvancementsMessage(Locale locale, AdvancementsProgress progress, int state) {
        return createAdvancementsMessage(createGlobalData(locale, state), progress, state);
    }

    @Nullable
    MessagePlayOutAdvancements createAdvancementsMessage(@Nullable GlobalAdvancementsData advancementsData,
            AdvancementsProgress progress, int state) {
        if (state == REMOVE) {
            return advancementsData == null || advancementsData.removed.isEmpty() ? null : new MessagePlayOutAdvancements(
                    false, Collections.emptyList(), advancementsData.removed, Collections.emptyMap());
        }

        Map<String, Object2LongMap<String>> progressMap = null;
        if (this.rootAdvancement == null && (state == INITIALIZE || state == REFRESH)) {
            progressMap = new HashMap<>();
            final String rootId = formatId0(ROOT_ADVANCEMENT);
            progressMap.put(rootId, Object2LongMaps.singleton(AdvancementCriterion.DUMMY, System.currentTimeMillis()));
        }

        for (Advancement advancement : this.advancements.keySet()) {
            final AdvancementProgress progress1 = progress.getOrNull(advancement);
            if (progress1 != null && (state == INITIALIZE || state == REFRESH || progress1.isDirty())) {
                if (progressMap == null) {
                    progressMap = new HashMap<>();
                }
                final Object2LongMap<String> entries;
                if (!advancement.getCriteria().isEmpty()) {
                    entries = new Object2LongOpenHashMap<>();
                    for (AdvancementCriterion criterion : progress1.getDirtyCriteria()) {
                        entries.put(criterion.id, progress1.getOrInvalid(criterion));
                    }
                } else {
                    entries = Object2LongMaps.singleton(AdvancementCriterion.DUMMY, progress1.getAchieveTime());
                }
                progressMap.put(formatId0(advancement.getId()), entries);
            }
        }
        return advancementsData == null && progressMap == null ? null : new MessagePlayOutAdvancements(false,
                advancementsData == null ? Collections.emptyList() : advancementsData.addedStructs,
                advancementsData == null ? Collections.emptyList() : advancementsData.removed,
                progressMap == null ? Collections.emptyMap() : progressMap);
    }

    private MessagePlayOutAdvancements.AdvStruct.Display createDisplay(LocalizedText title, LocalizedText description, ItemStackSnapshot icon,
            FrameType frameType, @Nullable String background, double x, double y, boolean showToast) {
        return new MessagePlayOutAdvancements.AdvStruct.Display(title, description, icon, frameType, background, x, y, showToast);
    }

    private MessagePlayOutAdvancements.AdvStruct createStruct(String id, @Nullable String parentId,
            @Nullable MessagePlayOutAdvancements.AdvStruct.Display display, List<List<String>> criteria) {
        final List<String> criteria1 = new ArrayList<>();
        criteria.forEach(criteria1::addAll);
        return new MessagePlayOutAdvancements.AdvStruct(id, parentId, display, criteria1, criteria);
    }
}
