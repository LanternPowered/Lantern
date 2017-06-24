package org.lanternpowered.server.item.recipe.crafting;

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.crafting.CraftingGridInventory;

import javax.annotation.Nullable;

/**
 * A crafting matrix that allows {@link ItemStack}s to be reused in the crafting
 * systems, all the items are also lazily copied.
 */
public class CraftingMatrix {

    /**
     * Creates a {@link CraftingMatrix} for the given {@link CraftingGridInventory}.
     *
     * @param gridInventory The crafting grid inventory
     * @return The crafting matrix
     */
    public static CraftingMatrix of(CraftingGridInventory gridInventory) {
        checkNotNull(gridInventory, "gridInventory");
        return new CraftingMatrix(gridInventory);
    }

    @Nullable private ItemStack[][] matrix;
    private final CraftingGridInventory grid;

    private CraftingMatrix(CraftingGridInventory grid) {
        this.grid = grid;
    }

    /**
     * Gets the {@link ItemStack} at the given coordinates
     * or {@link ItemStack#empty()} if none is present.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @return The item stack
     */
    public ItemStack get(int x, int y) {
        if (this.matrix == null) {
            this.matrix = new ItemStack[this.grid.getColumns()][this.grid.getRows()];
        }
        ItemStack itemStack = this.matrix[x][y];
        if (itemStack == null) {
            itemStack = this.matrix[x][y] = this.grid.peek(x, y).orElse(ItemStack.empty());
        }
        return itemStack;
    }

    /**
     * Gets the width of the crafting matrix.
     *
     * @return The width
     */
    public int width() {
        return this.grid.getColumns();
    }

    /**
     * Gets the height of the crafting matrix.
     *
     * @return The height
     */
    public int height() {
        return this.grid.getRows();
    }

    @SuppressWarnings("ConstantConditions")
    static final CraftingMatrix EMPTY = new CraftingMatrix(null) {

        @Override
        public ItemStack get(int x, int y) {
            return ItemStack.empty();
        }

        @Override
        public int width() {
            return 1;
        }

        @Override
        public int height() {
            return 1;
        }
    };
}
