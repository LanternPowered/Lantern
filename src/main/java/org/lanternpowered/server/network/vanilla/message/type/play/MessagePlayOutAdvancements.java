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
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import org.lanternpowered.server.advancement.AdvancementFrameType;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.objects.LocalizedText;
import org.spongepowered.api.item.ItemType;

import java.util.Arrays;
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
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("clear", this.clear)
                .add("addedAdvStructs", Arrays.toString(this.addedAdvStructs.toArray(new AdvStruct[0])))
                .add("removedAdvs", Arrays.toString(this.removedAdvs.toArray(new String[0])))
                .toString();
    }

    public static final class AdvStruct {

        private final String id;
        @Nullable private final String parentId;
        @Nullable private final Display display;
        private final List<String> criteria;
        private final List<List<String>> requirements;

        public AdvStruct(String id, @Nullable String parentId, @Nullable Display display,
                List<String> criteria, List<List<String>> requirements) {
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

        public List<String> getCriteria() {
            return this.criteria;
        }

        public List<List<String>> getRequirements() {
            return this.requirements;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .omitNullValues()
                    .add("id", this.id)
                    .add("parentId", this.parentId)
                    .add("display", this.display)
                    .add("criteria", Arrays.toString(this.criteria.toArray(new String[0])))
                    .toString();
        }

        public static final class Display {

            private final LocalizedText title;
            private final ItemType icon;
            private final AdvancementFrameType frameType;
            @Nullable private final String background;
            private final int x;
            private final int y;

            public Display(LocalizedText title, ItemType icon, AdvancementFrameType frameType,
                    @Nullable String background, int x, int y) {
                this.icon = icon;
                this.title = title;
                this.frameType = frameType;
                this.background = background;
                this.x = x;
                this.y = y;
            }

            public LocalizedText getTitle() {
                return this.title;
            }

            public ItemType getIcon() {
                return this.icon;
            }

            public AdvancementFrameType getFrameType() {
                return this.frameType;
            }

            public Optional<String> getBackground() {
                return Optional.ofNullable(this.background);
            }

            public int getX() {
                return this.x;
            }

            public int getY() {
                return this.y;
            }

            @Override
            public String toString() {
                return MoreObjects.toStringHelper(this)
                        .omitNullValues()
                        .add("icon", this.icon.getId())
                        .add("title", this.title.getText())
                        .add("frameType", this.frameType.getId())
                        .add("background", this.background)
                        .add("x", this.x)
                        .add("y", this.y)
                        .toString();
            }
        }
    }
}
