package org.lanternpowered.server.item.recipe.crafting;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.MoreObjects;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.item.recipe.crafting.CraftingResult;

import javax.annotation.Nullable;

public final class ExtendedCraftingResult {

    private final CraftingResult result;
    private final CraftingMatrix matrix;
    @Nullable final int[][] itemQuantities;
    private final int maxTimes;

    public ExtendedCraftingResult(CraftingResult result, CraftingMatrix matrix, int maxTimes) {
        this(result, matrix, maxTimes, null);
    }

    ExtendedCraftingResult(CraftingResult result, CraftingMatrix matrix, int maxTimes, @Nullable int[][] itemQuantities) {
        this.itemQuantities = itemQuantities;
        this.maxTimes = maxTimes;
        this.matrix = matrix;
        this.result = result;
    }

    /**
     * Gets the {@link CraftingResult}.
     *
     * @return The crafting result
     */
    public CraftingResult getResult() {
        return this.result;
    }

    /**
     * Gets the result {@link CraftingMatrix}.
     *
     * @return The result matrix
     */
    public CraftingMatrix getResultMatrix(int times) {
        checkArgument(times <= this.maxTimes, "times cannot exceed getMaxTimes");
        final CraftingMatrix matrix = this.matrix.copy();
        for (int x = 0; x < matrix.width(); x++) {
            for (int y = 0; y < matrix.height(); y++) {
                final ItemStack itemStack = matrix.get(x, y);
                if (!itemStack.isEmpty()) {
                    itemStack.setQuantity(itemStack.getQuantity() -
                            (this.itemQuantities == null ? 1 : this.itemQuantities[x][y]) * times);
                }
            }
        }
        return matrix;
    }

    /**
     * Gets the amount of times that the {@link CraftingResult}
     * can be crafted from a {@link GridInventory}.
     *
     * @return The times
     */
    public int getMaxTimes() {
        return this.maxTimes;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("result", this.result)
                .add("maxTimes", this.maxTimes)
                .toString();
    }
}
