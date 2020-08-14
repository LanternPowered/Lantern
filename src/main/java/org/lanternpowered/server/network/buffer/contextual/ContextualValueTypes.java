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
package org.lanternpowered.server.network.buffer.contextual;

import net.kyori.adventure.text.Component;
import org.lanternpowered.server.network.entity.parameter.ParameterList;
import org.lanternpowered.server.network.entity.parameter.ParameterListContextualValueType;
import org.lanternpowered.server.network.item.ItemStackValueType;
import org.lanternpowered.server.network.vanilla.advancement.NetworkAdvancement;
import org.lanternpowered.server.network.vanilla.advancement.NetworkAdvancementContextualValueType;
import org.lanternpowered.server.network.vanilla.recipe.NetworkRecipe;
import org.lanternpowered.server.network.vanilla.recipe.NetworkRecipeContextualValueType;
import org.lanternpowered.server.network.vanilla.trade.NetworkTradeOffer;
import org.lanternpowered.server.network.vanilla.trade.NetworkTradeOfferContextualValueType;
import org.spongepowered.api.item.inventory.ItemStack;

public final class ContextualValueTypes {

    /**
     * A serializer for {@link Text} objects,
     * NULL {@code null} values are NOT SUPPORTED.
     * <p>
     * Text -> JSON -> UTF-8 encoded string prefixed by the length as a var-int.
     */
    public static final ContextualValueType<Component> TEXT = new TextContextualValueType();

    /**
     * A serializer for legacy text objects,
     * NULL {@code null} values are NOT SUPPORTED.
     * <p>
     * Text -> Legacy -> UTF-8 encoded string prefixed by the length as a var-int.
     */
    public static final ContextualValueType<Component> LEGACY_TEXT = new TextContextualValueType(); // TODO

    /**
     * A serializer for {@link ItemStack} objects,
     * NULL {@code null} values are SUPPORTED.
     */
    public static final ContextualValueType<ItemStack> ITEM_STACK = new ItemStackValueType();

    /**
     * A serializer for {@link ParameterList} objects.
     */
    public static final ContextualValueType<ParameterList> PARAMETER_LIST = new ParameterListContextualValueType();

    /**
     * A serializer for {@link NetworkRecipe} objects.
     */
    public static final ContextualValueType<NetworkRecipe> RECIPE = new NetworkRecipeContextualValueType();

    /**
     * A serializer for {@link NetworkAdvancement} objects.
     */
    public static final ContextualValueType<NetworkAdvancement> ADVANCEMENT = new NetworkAdvancementContextualValueType();

    /**
     * A serializer for {@link NetworkTradeOffer} objects.
     */
    public static final ContextualValueType<NetworkTradeOffer> TRADE_OFFER = new NetworkTradeOfferContextualValueType();

    private ContextualValueTypes() {
    }
}
