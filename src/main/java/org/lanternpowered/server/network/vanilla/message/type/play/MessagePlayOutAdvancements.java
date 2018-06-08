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
package org.lanternpowered.server.network.vanilla.message.type.play;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Iterables;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import org.lanternpowered.server.network.message.Message;
import org.spongepowered.api.advancement.AdvancementType;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

public final class MessagePlayOutAdvancements implements Message {

    private final boolean clear;
    private final List<AdvStruct> addedAdvStructs;
    private final List<String> removedAdvs;
    private final Map<String, Object2LongMap<String>> progress;

    public MessagePlayOutAdvancements(boolean clear, List<AdvStruct> addedAdvStructs, List<String> removedAdvs,
            Map<String, Object2LongMap<String>> progress) {
        this.addedAdvStructs = addedAdvStructs;
        this.removedAdvs = removedAdvs;
        this.progress = progress;
        this.clear = clear;
    }

    public boolean getClear() {
        return this.clear;
    }

    public List<AdvStruct> getAddedAdvStructs() {
        return this.addedAdvStructs;
    }

    public List<String> getRemovedAdvs() {
        return this.removedAdvs;
    }

    public Map<String, Object2LongMap<String>> getProgress() {
        return this.progress;
    }

    @Override
    public String toString() {
        final MoreObjects.ToStringHelper progress = MoreObjects.toStringHelper("");
        this.progress.forEach((key, value) -> {
            final MoreObjects.ToStringHelper progressEntry = MoreObjects.toStringHelper("");
            value.object2LongEntrySet().forEach(e2 -> progressEntry.add(e2.getKey(), e2.getLongValue()));
            progress.add(key, progressEntry.toString());
        });
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("clear", this.clear)
                .add("addedAdvStructs", Iterables.toString(this.addedAdvStructs))
                .add("removedAdvs", Iterables.toString(this.removedAdvs))
                .add("progress", progress.toString())
                .toString();
    }

    public static final class AdvStruct {

        private final String id;
        @Nullable private final String parentId;
        @Nullable private final Display display;
        private final Collection<String> criteria;
        private final String[][] requirements;

        public AdvStruct(String id, @Nullable String parentId, @Nullable Display display,
                Collection<String> criteria, String[][] requirements) {
            this.requirements = requirements;
            this.parentId = parentId;
            this.criteria = criteria;
            this.display = display;
            this.id = id;
        }

        public String getId() {
            return this.id;
        }

        public Optional<String> getParentId() {
            return Optional.ofNullable(this.parentId);
        }

        public Optional<Display> getDisplay() {
            return Optional.ofNullable(this.display);
        }

        public Collection<String> getCriteria() {
            return this.criteria;
        }

        public String[][] getRequirements() {
            return this.requirements;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .omitNullValues()
                    .add("id", this.id)
                    .add("parentId", this.parentId)
                    .add("display", this.display)
                    .add("criteria", Iterables.toString(this.criteria))
                    .toString();
        }

        public static final class Display {

            private final Text title;
            private final Text description;
            private final ItemStackSnapshot icon;
            private final AdvancementType frameType;
            @Nullable private final String background;
            private final double x;
            private final double y;
            private final boolean showToast;
            private final boolean hidden;

            public Display(Text title, Text description, ItemStackSnapshot icon, AdvancementType frameType,
                    @Nullable String background, double x, double y, boolean showToast, boolean hidden) {
                this.description = description;
                this.background = background;
                this.frameType = frameType;
                this.showToast = showToast;
                this.hidden = hidden;
                this.title = title;
                this.icon = icon;
                this.x = x;
                this.y = y;
            }

            public boolean isHidden() {
                return this.hidden;
            }

            public Text getDescription() {
                return this.description;
            }

            public Text getTitle() {
                return this.title;
            }

            public ItemStackSnapshot getIcon() {
                return this.icon;
            }

            public AdvancementType getType() {
                return this.frameType;
            }

            public Optional<String> getBackground() {
                return Optional.ofNullable(this.background);
            }

            public double getX() {
                return this.x;
            }

            public double getY() {
                return this.y;
            }

            public boolean doesShowToast() {
                return this.showToast;
            }

            @Override
            public String toString() {
                return MoreObjects.toStringHelper(this)
                        .omitNullValues()
                        .add("icon", this.icon)
                        .add("title", this.title)
                        .add("description", this.description)
                        .add("type", this.frameType.getId())
                        .add("background", this.background)
                        .add("showToast", this.showToast)
                        .add("x", this.x)
                        .add("y", this.y)
                        .toString();
            }
        }
    }
}
