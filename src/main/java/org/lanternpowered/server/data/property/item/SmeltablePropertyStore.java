package org.lanternpowered.server.data.property.item;

import org.lanternpowered.server.data.property.common.AbstractItemStackPropertyStore;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.data.property.item.SmeltableProperty;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class SmeltablePropertyStore extends AbstractItemStackPropertyStore<SmeltableProperty> {

    private static final Optional<SmeltableProperty> TRUE = Optional.of(new SmeltableProperty(true));
    private static final Optional<SmeltableProperty> FALSE = Optional.of(new SmeltableProperty(false));

    @Override
    protected Optional<SmeltableProperty> getFor(ItemStack itemStack) {
        return Lantern.getRegistry().getSmeltingRecipeRegistry()
                .findMatchingRecipe(itemStack.createSnapshot())
                .isPresent() ? TRUE : FALSE;
    }
}
