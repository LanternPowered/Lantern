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
package org.lanternpowered.server.inventory.vanilla.block;

import org.lanternpowered.server.block.entity.IBlockEntityInventory;
import org.lanternpowered.server.inventory.AbstractChildrenInventory;
import org.lanternpowered.server.inventory.client.ClientContainer;
import org.lanternpowered.server.inventory.client.ContainerProperties;
import org.lanternpowered.server.inventory.type.slot.LanternFuelSlot;
import org.lanternpowered.server.inventory.type.slot.LanternInputSlot;
import org.lanternpowered.server.inventory.type.slot.LanternOutputSlot;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.item.inventory.query.QueryOperation;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;

import java.util.Optional;

public class FurnaceInventory extends AbstractChildrenInventory implements IBlockEntityInventory {

    private static final class Holder {

        private static final QueryOperation<?> INPUT_SLOT_OPERATION =
                QueryOperationTypes.INVENTORY_TYPE.of(LanternInputSlot.class);
        private static final QueryOperation<?> FUEL_SLOT_OPERATION =
                QueryOperationTypes.INVENTORY_TYPE.of(LanternFuelSlot.class);
        private static final QueryOperation<?> OUTPUT_SLOT_OPERATION =
                QueryOperationTypes.INVENTORY_TYPE.of(LanternOutputSlot.class);
    }

    private LanternInputSlot inputSlot;
    private LanternFuelSlot fuelSlot;
    private LanternOutputSlot outputSlot;

    private boolean useCachedProgress;

    private double smeltProgress;
    private double fuelProgress;

    public LanternInputSlot getInputSlot() {
        return this.inputSlot;
    }

    public LanternFuelSlot getFuelSlot() {
        return this.fuelSlot;
    }

    public LanternOutputSlot getOutputSlot() {
        return this.outputSlot;
    }

    public void enableCachedProgress() {
        this.useCachedProgress = true;
    }

    public void resetCachedProgress() {
        this.smeltProgress = -1;
        this.fuelProgress = -1;
    }

    @Override
    protected void init() {
        super.init();

        resetCachedProgress();
        this.inputSlot = (LanternInputSlot) query(Holder.INPUT_SLOT_OPERATION).first();
        this.fuelSlot = (LanternFuelSlot) query(Holder.FUEL_SLOT_OPERATION).first();
        this.outputSlot = (LanternOutputSlot) query(Holder.OUTPUT_SLOT_OPERATION).first();
    }

    @Override
    protected void initClientContainer(ClientContainer clientContainer) {
        super.initClientContainer(clientContainer);
        // Provide the smelting progress
        clientContainer.bindPropertySupplier(ContainerProperties.SMELT_PROGRESS, () -> {
            final Optional<DataHolder> dataHolder = getCarrierAs(DataHolder.class);
            if (!dataHolder.isPresent()) {
                return 0.0;
            }
            double smeltProgress = this.smeltProgress;
            if (!this.useCachedProgress || smeltProgress < 0) {
                smeltProgress = this.smeltProgress =
                        dataHolder.get().get(Keys.PASSED_COOK_TIME).get().doubleValue() /
                                dataHolder.get().get(Keys.MAX_COOK_TIME).get().doubleValue();
            }
            return smeltProgress;
        });
        // Provide the fuel progress
        clientContainer.bindPropertySupplier(ContainerProperties.FUEL_PROGRESS, () -> {
            final Optional<DataHolder> dataHolder = getCarrierAs(DataHolder.class);
            if (!dataHolder.isPresent()) {
                return 0.0;
            }
            double fuelProgress = this.fuelProgress;
            if (!this.useCachedProgress || fuelProgress < 0) {
                fuelProgress = this.fuelProgress = 1.0 -
                        dataHolder.get().get(Keys.PASSED_BURN_TIME).get().doubleValue() /
                                dataHolder.get().get(Keys.MAX_BURN_TIME).get();
            }
            return fuelProgress;
        });
    }
}
