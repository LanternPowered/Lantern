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
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;

public final class AdvancementTreeBuilder extends StyleableBuilder<AdvancementTree, AdvancementTreeBuilder> {

    /**
     * The default background of the {@link AdvancementTree}.
     */
    public static final String DEFAULT_BACKGROUND = "minecraft:textures/gui/advancements/backgrounds/stone.png";

    @Nullable private Advancement rootAdvancement;
    private Vector2i position;
    private String background;

    AdvancementTreeBuilder() {
        reset();
    }

    /**
     * Sets the root {@link Advancement}.
     * <p>
     * If this isn't specified ({@code null}), then it is required to provide at least a title,
     * with optionally a icon and description.
     *
     * @param rootAdvancement The root advancement
     * @return This advancement tree builder, for chaining
     */
    public AdvancementTreeBuilder rootAdvancement(@Nullable Advancement rootAdvancement) {
        this.rootAdvancement = rootAdvancement;
        return this;
    }

    /**
     * Sets the position of the root advancement or icon.
     * <p>
     * Defaults to {@link Vector2i#ZERO}.
     *
     * @param position The position
     * @return This advancement tree builder, for chaining
     */
    public AdvancementTreeBuilder rootPosition(Vector2i position) {
        checkNotNull(this.position, "position");
        this.position = position;
        return this;
    }

    /**
     * Sets the background of {@link AdvancementTree}.
     *
     * @param background The background
     * @return This advancement tree builder, for chaining
     */
    public AdvancementTreeBuilder background(@Nullable String background) {
        this.background = background == null ? DEFAULT_BACKGROUND : background;
        return this;
    }

    /**
     * Builds the {@link AdvancementTree} with the specified plugin identifier and identifier.
     *
     * @param pluginId The plugin identifier
     * @param id The identifier
     * @return The advancement tree
     */
    public AdvancementTree build(String pluginId, String id) {
        final Text title;
        final Text description;
        final ItemStackSnapshot icon;
        final FrameType frameType;
        if (this.rootAdvancement == null) {
            //noinspection ConstantConditions
            checkArgument(this.title != null, "The title must be set");
            title = this.title;
            description = this.description;
            icon = this.icon;
            frameType = this.frameType;
        } else {
            title = this.rootAdvancement.getTitle();
            description = this.rootAdvancement.getDescription();
            icon = this.rootAdvancement.getIcon();
            frameType = this.rootAdvancement.getFrameType();
        }
        return new AdvancementTree(pluginId, id, title.toPlain(), title, description, icon, frameType,
                this.background, this.rootAdvancement, this.position);
    }

    @Override
    public AdvancementTreeBuilder from(AdvancementTree value) {
        super.from(value);
        this.rootAdvancement = value.getRootAdvancement().orElse(null);
        this.background = value.getBackground();
        return this;
    }

    @Override
    public AdvancementTreeBuilder reset() {
        super.reset();
        this.rootAdvancement = null;
        this.position = Vector2i.ZERO;
        this.background = DEFAULT_BACKGROUND;
        return this;
    }
}
