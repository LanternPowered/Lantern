/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.item;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.Optional;

import javax.annotation.Nullable;

public final class ItemInteractionResult {

    private static final ItemInteractionResult PASS = builder().type(Type.PASS).build();
    private static final ItemInteractionResult SUCCESS = builder().type(Type.SUCCESS).build();

    /**
     * Gets a {@link ItemInteractionResult} of the type {@link Type#PASS}, with no
     * modifications done to the result item.
     *
     * @return The item interaction result
     */
    public static ItemInteractionResult pass() {
        return PASS;
    }

    /**
     * Gets a {@link ItemInteractionResult} of the type {@link Type#SUCCESS}, with no
     * modifications done to the result item.
     *
     * @return The item interaction result
     */
    public static ItemInteractionResult success() {
        return SUCCESS;
    }

    /**
     * Gets a new {@link Builder}.
     *
     * @return The builder
     */
    public static Builder builder() {
        return new Builder();
    }

    @Nullable private final ItemStackSnapshot resultItem;
    private final Type type;

    private ItemInteractionResult(Type type, @Nullable ItemStackSnapshot resultItem) {
        this.resultItem = resultItem;
        this.type = type;
    }

    public Optional<ItemStackSnapshot> getResultItem() {
        return Optional.ofNullable(this.resultItem);
    }

    public Type getType() {
        return this.type;
    }

    public enum Type {
        SUCCESS,
        FAIL,
        PASS,
    }

    /**
     * A builder to create {@link ItemInteractionResult}s.
     */
    public static final class Builder {

        private Type type;
        @Nullable private ItemStackSnapshot resultItem;

        private Builder() {
        }

        /**
         * Sets the {@link Type} of the result.
         *
         * @param type The type
         * @return This builder for chaining
         */
        public Builder type(Type type) {
            this.type = checkNotNull(type, "type");
            return this;
        }

        /**
         * Sets the result {@link ItemStackSnapshot}.
         *
         * @param resultItem The result item
         * @return This builder for chaining
         */
        public Builder resultItem(ItemStackSnapshot resultItem) {
            this.resultItem = checkNotNull(resultItem, "resultItem");
            return this;
        }

        /**
         * Builds the {@link ItemInteractionResult}.
         *
         * @return The interaction result
         */
        public ItemInteractionResult build() {
            checkArgument(this.type != null, "The type must be set");
            return new ItemInteractionResult(this.type, this.resultItem);
        }
    }
}
