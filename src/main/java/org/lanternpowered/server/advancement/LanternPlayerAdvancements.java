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
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import org.lanternpowered.server.advancement.criteria.LanternScoreCriterion;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.registry.type.advancement.AdvancementRegistryModule;
import org.lanternpowered.server.game.registry.type.advancement.AdvancementTreeRegistryModule;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutAdvancements;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.advancement.Advancement;
import org.spongepowered.api.advancement.AdvancementTree;
import org.spongepowered.api.advancement.DisplayInfo;
import org.spongepowered.api.advancement.TreeLayoutElement;
import org.spongepowered.api.advancement.criteria.AdvancementCriterion;
import org.spongepowered.api.advancement.criteria.AndCriterion;
import org.spongepowered.api.util.Tuple;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

public class LanternPlayerAdvancements {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

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
     * Gets the {@link Path} of the save file.
     *
     * @return The path
     */
    private Path getSavePath() {
        return Lantern.getGame().getSavesDirectory()
                .resolve("advancements")
                .resolve(this.player.getUniqueId().toString().toLowerCase() + ".json");
    }

    public void init() {
        init0();

        // Load progress from the file
        final Path file = getSavePath();
        if (Files.exists(file)) {
            try (BufferedReader reader = Files.newBufferedReader(file)) {
                loadProgressFromJson(GSON.fromJson(reader, JsonObject.class));
            } catch (IOException e) {
                Lantern.getLogger().error("Failed to load the advancements progress for the player: " + this.player.getUniqueId(), e);
            }
        }
    }

    public void initClient() {
        this.player.getConnection().send(createUpdateMessage(true));
    }

    public void save() {
        final Path file = getSavePath();

        try {
            // Make sure the parent directory exists
            final Path parent = file.getParent();
            if (!Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            // Save the advancements
            try (BufferedWriter writer = Files.newBufferedWriter(file)) {
                GSON.toJson(saveProgressToJson(), writer);
            }
        } catch (IOException e) {
            Lantern.getLogger().error("Failed to save the advancements progress for the player: " + this.player.getUniqueId(), e);
        }
    }

    /**
     * Reloads the progress. This should be called when the
     * {@link Advancement}s are getting reloaded.
     */
    public void reload() {
        // Save the progress
        final Map<String, Map<String, Instant>> progressMap = saveProgress();
        // Initialize the new advancements
        init0();
        // Load the progress again
        loadProgress(progressMap);
        // Resend the advancements to the player
        initClient();
    }

    private void init0() {
        cleanup();

        // Load all the advancements into this progress tracker
        final AdvancementRegistryModule registryModule = AdvancementRegistryModule.get();
        for (Advancement advancement : registryModule.getAll()) {
            final LanternAdvancementProgress progress = get(advancement);
            // Update the visibility
            progress.dirtyVisibility = true;
            this.dirtyProgress.add(progress);
        }
    }

    public void pulse() {
        final MessagePlayOutAdvancements advancementsMessage = createUpdateMessage(false);
        if (advancementsMessage != null) {
            this.player.getConnection().send(advancementsMessage);
        }
    }

    public void cleanup() {
        // Clear all the current progress
        this.dirtyProgress.clear();
        this.progress.values().forEach(LanternAdvancementProgress::cleanup);
        this.progress.clear();
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

    public Collection<AdvancementTree> getUnlockedAdvancementTrees() {
        final ImmutableList.Builder<AdvancementTree> builder = ImmutableList.builder();
        for (AdvancementTree tree : AdvancementTreeRegistryModule.get().getAll()) {
            final Advancement advancement = tree.getRootAdvancement();
            final LanternAdvancementProgress progress = get(advancement);
            if ((!progress.dirtyVisibility && progress.visible) ||
                    (progress.dirtyVisibility && shouldBeVisible(advancement))) {
                builder.add(tree);
            }
        }
        return builder.build();
    }

    private void loadProgress(Map<String, Map<String, Instant>> progressMap) {
        for (Advancement advancement : AdvancementRegistryModule.get().getAll()) {
            final Map<String, Instant> entry = progressMap.get(advancement.getKey().toString());
            if (entry != null) {
                get(advancement).loadProgress(entry);
            }
        }
    }

    private Map<String, Map<String, Instant>> saveProgress() {
        final Map<String, Map<String, Instant>> progressMap = new HashMap<>();
        for (LanternAdvancementProgress entry : this.progress.values()) {
            final Map<String, Instant> entryProgress = entry.saveProgress();
            if (!entryProgress.isEmpty()) {
                progressMap.put(entry.getAdvancement().getKey().toString(), entryProgress);
            }
        }
        return progressMap;
    }

    private void loadProgressFromJson(JsonObject json) {
        for (Advancement advancement : AdvancementRegistryModule.get().getAll()) {
            final JsonObject entry = json.getAsJsonObject(advancement.getKey().toString());
            if (entry != null) {
                loadAdvancementProgressFromJson(get(advancement), entry);
            }
        }
    }

    private JsonObject saveProgressToJson() {
        final JsonObject json = new JsonObject();
        for (LanternAdvancementProgress entry : this.progress.values()) {
            final JsonObject entryJson = saveAdvancementProgressToJson(entry);
            if (entryJson != null) {
                json.add(entry.getAdvancement().getKey().toString(), entryJson);
            }
        }
        return json;
    }

    private void loadAdvancementProgressFromJson(LanternAdvancementProgress progress, JsonObject json) {
        if (!json.has("criteria")) {
            return;
        }
        final Map<String, Instant> progressMap = new HashMap<>();
        // Convert all the string instants
        for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("criteria").entrySet()) {
            try {
                progressMap.put(entry.getKey(), Instant.ofEpochMilli(DATE_TIME_FORMATTER.parse(entry.getValue().getAsString()).getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
                progressMap.put(entry.getKey(), Instant.now());
            }
        }
        progress.loadProgress(progressMap);
    }

    @Nullable
    private JsonObject saveAdvancementProgressToJson(LanternAdvancementProgress progress) {
        final Map<String, Instant> progressMap = progress.saveProgress();
        final boolean done = progress.achieved();
        if (progressMap.isEmpty() && !done) {
            return null;
        }
        final JsonObject json = new JsonObject();
        json.addProperty("done", done);
        if (!progressMap.isEmpty()) {
            final JsonObject progressJson = new JsonObject();
            for (Map.Entry<String, Instant> entry : progressMap.entrySet()) {
                progressJson.addProperty(entry.getKey(), DATE_TIME_FORMATTER.format(new Date(entry.getValue().toEpochMilli())));
            }
            json.add("criteria", progressJson);
        }
        return json;
    }

    @Nullable
    private MessagePlayOutAdvancements createUpdateMessage(boolean init) {
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
                update(progress, null, null, null, true);
            }
            for (LanternAdvancementProgress progress : this.progress.values()) {
                if (progress.visible) {
                    // Add the advancement
                    added.add(createAdvancement(progress.getAdvancement()));
                    final Object2LongMap<String> progressMap1 = progress.collectProgress();
                    if (!progressMap1.isEmpty()) {
                        // Fill the progress map
                        progressMap.put(progress.getAdvancement().getKey().toString(), progressMap1);
                    }
                }
            }
        } else {
            removed = new ArrayList<>();
            for (LanternAdvancementProgress progress : this.dirtyProgress) {
                update(progress, added, removed, progressMap, false);
            }
        }
        // Clear the dirty progress
        this.dirtyProgress.clear();
        return new MessagePlayOutAdvancements(init, added, removed, progressMap);
    }

    private void update(LanternAdvancementProgress progress,
            @Nullable List<MessagePlayOutAdvancements.AdvStruct> added,
            @Nullable List<String> removed,
            @Nullable Map<String, Object2LongMap<String>> progressMap,
            boolean force) {
        final Advancement advancement = progress.getAdvancement();
        boolean updateParentAndChildren = false;
        if (progress.dirtyVisibility || force) {
            final boolean visible = shouldBeVisible(advancement);
            progress.dirtyVisibility = false;
            if (progress.visible != visible) {
                progress.visible = visible;
                if (visible && added != null) {
                    added.add(createAdvancement(advancement));
                    // The progress is now visible, send the complete data
                    if (progressMap != null) {
                        final Object2LongMap<String> progressMap1 = progress.collectProgress();
                        if (!progressMap1.isEmpty()) {
                            progressMap.put(advancement.getKey().toString(), progressMap1);
                        }
                        // The progress is already updated, prevent from doing it again
                        progress.dirtyProgress = false;
                    }
                } else if (removed != null) {
                    removed.add(advancement.getKey().toString());
                }
                updateParentAndChildren = true;
            }
        }
        if (progress.visible && progress.dirtyProgress && progressMap != null) {
            progressMap.put(advancement.getKey().toString(), progress.collectProgress());
        }
        // Reset dirty state, even if nothing changed
        progress.dirtyProgress = false;
        if (updateParentAndChildren) {
            for (Advancement child : advancement.getChildren()) {
                update(get(child), added, removed, progressMap, true);
            }
            advancement.getParent().ifPresent(parent ->
                    update(get(parent), added, removed, progressMap, true));
        }
    }

    /**
     * Gets whether the {@link Advancement} should be visible.
     *
     * @param advancement The advancement
     * @return Whether it should be visible
     */
    private boolean shouldBeVisible(Advancement advancement) {
        if (!advancement.getTree().isPresent()) {
            return false;
        }
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
        final List<String[]> names = new ArrayList<>();
        if (criterion instanceof AndCriterion) {
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
        } else {
            if (criterion instanceof LanternScoreCriterion) {
                for (String id : ((LanternScoreCriterion) criterion).getIds()) {
                    names.add(new String[] { id });
                }
            } else {
                names.add(new String[] { criterion.getName() });
            }
            criteria.add(criterion);
        }
        return new Tuple<>(criteria, names.toArray(new String[names.size()][]));
    }

    private MessagePlayOutAdvancements.AdvStruct createAdvancement(Advancement advancement) {
        return createAdvancement(this.player.getLocale(), advancement);
    }

    @SuppressWarnings("ConstantConditions")
    private static MessagePlayOutAdvancements.AdvStruct createAdvancement(
            Locale locale, Advancement advancement) {
        final String parentId = advancement.getParent().map(Advancement::getKey).map(CatalogKey::toString).orElse(null);
        final String background = parentId == null ? advancement.getTree().get().getBackgroundPath() : null;
        final DisplayInfo displayInfo = advancement.getDisplayInfo().orElse(null);
        final TreeLayoutElement layoutElement = ((LanternAdvancement) advancement).getLayoutElement();
        final String[][] criteriaRequirements = ((LanternAdvancement) advancement).clientCriteria.getSecond();
        final Set<String> criteria = new HashSet<>();
        for (String[] array : criteriaRequirements) {
            Collections.addAll(criteria, array);
        }
        return new MessagePlayOutAdvancements.AdvStruct(advancement.getKey().toString(), parentId,
                displayInfo == null ? null : createDisplay(locale, displayInfo, layoutElement, background),
                criteria, criteriaRequirements);
    }

    private static MessagePlayOutAdvancements.AdvStruct.Display createDisplay(
            Locale locale, DisplayInfo displayInfo, TreeLayoutElement layoutElement, @Nullable String background) {
        final Vector2d position = layoutElement.getPosition();
        return new MessagePlayOutAdvancements.AdvStruct.Display(
                displayInfo.getTitle(),
                displayInfo.getDescription(),
                displayInfo.getIcon(), displayInfo.getType(),
                background, position.getX(), position.getY(),
                displayInfo.doesShowToast(),
                displayInfo.isHidden());
    }
}
