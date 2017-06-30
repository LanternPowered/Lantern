package org.lanternpowered.server.data.property.item;

import org.lanternpowered.server.data.property.common.AbstractItemStackPropertyStore;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.data.property.item.BurningFuelProperty;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;
import java.util.OptionalInt;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class BurningFuelPropertyStore extends AbstractItemStackPropertyStore<BurningFuelProperty> {

    @Override
    protected Optional<BurningFuelProperty> getFor(ItemStack itemStack) {
        final OptionalInt fuelTime = Lantern.getRegistry().getFuelRegistry()
                .getResult(itemStack.createSnapshot());
        return fuelTime.isPresent() ? Optional.of(new BurningFuelProperty(fuelTime.getAsInt())) : Optional.empty();
    }
}
