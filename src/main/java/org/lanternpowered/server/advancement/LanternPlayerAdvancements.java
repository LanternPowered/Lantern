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

import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector2d;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import org.lanternpowered.server.advancement.criteria.LanternScoreCriterion;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.network.objects.LocalizedText;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutAdvancements;
import org.spongepowered.api.advancement.Advancement;
import org.spongepowered.api.advancement.DisplayInfo;
import org.spongepowered.api.advancement.TreeLayoutElement;
import org.spongepowered.api.advancement.criteria.AdvancementCriterion;
import org.spongepowered.api.advancement.criteria.AndCriterion;
import org.spongepowered.api.util.Tuple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

public class LanternPlayerAdvancements {

    private final LanternPlayer player;
    private final Map<Advancement, LanternAdvancementProgress> progress = new HashMap<>();

    // All the "dirty" progress that should be updated for the client
    final Set<LanternAdvancementProgress> dirtyProgress = new HashSet<>();

    public LanternPlayerAdvancements(LanternPlayer player) {
        this.player = player;
    }

    public LanternPlayer getPlayer() {
        return this.player;
    }

    /**
     * Gets the {@link LanternAdvancementProgress} for the specified {@link Advancement}.
     *
     * @param advancement The advancement
     * @return The advancement progress
     */
    public LanternAdvancementProgress get(Advancement advancement) {
        checkNotNull(advancement, "advancement");
        return this.progress.computeIfAbsent(advancement, advancement1 -> new LanternAdvancementProgress(
                this, (LanternAdvancement) advancement1));
    }

    @Nullable
    public MessagePlayOutAdvancements createUpdateMessage(boolean init) {
        if (!init && this.dirtyProgress.isEmpty()) {
            return null;
        }
        final List<MessagePlayOutAdvancements.AdvStruct> added = new ArrayList<>();
        final Map<String, Object2LongMap<String>> progressMap = new HashMap<>();
        final List<String> removed;
        if (init) {
            removed = Collections.emptyList();
            for (LanternAdvancementProgress progress : this.dirtyProgress) {
                // Update the visibility
                update(progress, null, null, null);
                // Reset the dirty state of the progress
                progress.resetDirtyState();
            }
            for (LanternAdvancementProgress progress : this.progress.values()) {
                if (progress.visible) {
                    // Add the advancement
                    added.add(createAdvancement(progress.getAdvancement()));
                    // Fill the progress map
                    final Object2LongMap<String> progressMap1 = new Object2LongOpenHashMap<>();
                    progress.fillProgress(progressMap1);
                    progressMap.put(progress.getAdvancement().getId(), progressMap1);
                }
            }
        } else {
            removed = new ArrayList<>();
            for (LanternAdvancementProgress progress : this.dirtyProgress) {
                update(progress, added, removed, progressMap);
            }
        }
        // Clear the dirty progress
        this.dirtyProgress.clear();
        return new MessagePlayOutAdvancements(init, added, removed, progressMap);
    }

    private void update(LanternAdvancementProgress progress,
            @Nullable List<MessagePlayOutAdvancements.AdvStruct> added,
            @Nullable List<String> removed,
            @Nullable Map<String, Object2LongMap<String>> progressMap) {
        final Advancement advancement = progress.getAdvancement();
        boolean updateProgress = true;
        boolean updateParentAndChildren = false;
        if (progress.dirtyVisibility) {
            final boolean current = progress.visible;
            final boolean visible = shouldBeVisible(advancement);
            if (current != visible) {
                progress.visible = visible;
                progress.dirtyVisibility = false;
                if (visible && added != null) {
                    added.add(createAdvancement(advancement));
                    // The progress is now visible, send the complete data
                    if (progressMap != null) {
                        final Object2LongMap<String> progressMap1 = new Object2LongOpenHashMap<>();
                        progress.fillProgress(progressMap1);
                        progressMap.put(advancement.getId(), progressMap1);
                        // The progress is already updated, prevent from doing it again
                        updateProgress = false;
                    }
                } else if (removed != null) {
                    removed.add(advancement.getId());
                }
                updateParentAndChildren = true;
            }
        }
        if (progress.dirtyProgress) {
            if (updateProgress && progressMap != null) {
                final Object2LongMap<String> progressMap1 = new Object2LongOpenHashMap<>();
                progress.fillDirtyProgress(progressMap1);
                progressMap.put(advancement.getId(), progressMap1);
            }
            // Reset dirty state, even if nothing changed
            progress.resetDirtyState();
        }
        if (updateParentAndChildren) {
            for (Advancement child : advancement.getChildren()) {
                update(get(child), added, removed, progressMap);
            }
            advancement.getParent().ifPresent(parent ->
                    update(get(parent), added, removed, progressMap));
        }
    }

    /**
     * Gets whether the {@link Advancement} should be visible.
     *
     * @param advancement The advancement
     * @return Whether it should be visible
     */
    private boolean shouldBeVisible(Advancement advancement) {
        if (shouldBeVisible0(advancement)) {
            return true;
        }
        Optional<DisplayInfo> display = advancement.getDisplayInfo();
        if (!display.isPresent() || display.get().isHidden()) {
            return false;
        }
        int i = 2;
        while (i-- > 0 && (advancement = advancement.getParent().orElse(null)) != null) {
            display = advancement.getDisplayInfo();
            if (!display.isPresent()) {
                return false;
            }
            if (get(advancement).achieved()) {
                return true;
            }
            if (display.get().isHidden()) {
                return false;
            }
        }
        return false;
    }

    private boolean shouldBeVisible0(Advancement advancement) {
        if (get(advancement).achieved()) {
            return true;
        }
        for (Advancement child : advancement.getChildren()) {
            if (shouldBeVisible0(child)) {
                return true;
            }
        }
        return false;
    }

    static Tuple<List<AdvancementCriterion>, String[][]> createCriteria(AdvancementCriterion criterion) {
        final List<AdvancementCriterion> criteria = new ArrayList<>();
        if (criterion instanceof AndCriterion) {
            final List<String[]> names = new ArrayList<>();
            for (AdvancementCriterion child : ((AndCriterion) criterion).getCriteria()) {
                if (child instanceof LanternScoreCriterion) {
                    for (String id : ((LanternScoreCriterion) child).getIds()) {
                        names.add(new String[] { id });
                    }
                } else {
                    names.add(new String[] { child.getName() });
                }
                criteria.add(child);
            }
            return new Tuple<>(criteria, names.toArray(new String[names.size()][]));
        } else {
            criteria.add(criterion);
            return new Tuple<>(criteria, new String[][] {{ criterion.getName() }});
        }
    }

    private MessagePlayOutAdvancements.AdvStruct createAdvancement(Advancement advancement) {
        return createAdvancement(this.player.getLocale(), advancement);
    }

    @SuppressWarnings("ConstantConditions")
    private static MessagePlayOutAdvancements.AdvStruct createAdvancement(
            Locale locale, Advancement advancement) {
        final String parentId = advancement.getParent().map(Advancement::getId).orElse(null);
        final String background = parentId == null ? advancement.getTree().get().getBackgroundPath() : null;
        final DisplayInfo displayInfo = advancement.getDisplayInfo().orElse(null);
        final TreeLayoutElement layoutElement = ((LanternAdvancement) advancement).getLayoutElement();
        final String[][] criteriaRequirements = ((LanternAdvancement) advancement).clientCriteria.getSecond();
        final Set<String> criteria = new HashSet<>();
        for (String[] array : criteriaRequirements) {
            Collections.addAll(criteria, array);
        }
        return new MessagePlayOutAdvancements.AdvStruct(advancement.getId(), parentId,
                displayInfo == null ? null : createDisplay(locale, displayInfo, layoutElement, background),
                criteria, criteriaRequirements);
    }

    private static MessagePlayOutAdvancements.AdvStruct.Display createDisplay(
            Locale locale, DisplayInfo displayInfo, TreeLayoutElement layoutElement, @Nullable String background) {
        final Vector2d position = layoutElement.getPosition();
        return new MessagePlayOutAdvancements.AdvStruct.Display(
                new LocalizedText(displayInfo.getTitle(), locale),
                new LocalizedText(displayInfo.getDescription(), locale),
                displayInfo.getIcon(), displayInfo.getType(),
                background, position.getX(), position.getY(),
                displayInfo.doesShowToast(),
                displayInfo.isHidden());
    }
}
