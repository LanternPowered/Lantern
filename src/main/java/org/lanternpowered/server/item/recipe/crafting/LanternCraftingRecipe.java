package org.lanternpowered.server.item.recipe.crafting;

import static com.google.common.base.Preconditions.checkState;

import org.lanternpowered.server.item.recipe.LanternRecipe;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.crafting.CraftingResult;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

@SuppressWarnings("ConstantConditions")
abstract class LanternCraftingRecipe extends LanternRecipe implements ICraftingRecipe {

    @Nullable private final String group;

    LanternCraftingRecipe(String pluginId, String name,
            ItemStackSnapshot exemplaryResult, @Nullable String group) {
        super(pluginId, name, exemplaryResult);
        this.group = group;
    }

    @Override
    public Optional<String> getGroup() {
        return Optional.ofNullable(this.group);
    }

    @Override
    public boolean isValid(CraftingMatrix craftingMatrix, World world) {
        return match(craftingMatrix, false, false) != null;
    }

    @Override
    public ItemStackSnapshot getResult(CraftingMatrix craftingMatrix) {
        final Result result = match(craftingMatrix, true, false);
        checkState(result != null, "isValid is false");
        return result.resultItem.createSnapshot();
    }

    @Override
    public List<ItemStackSnapshot> getRemainingItems(CraftingMatrix craftingMatrix) {
        final Result result = match(craftingMatrix, false, true);
        checkState(result != null, "isValid is false");
        return result.remainingItems;
    }

    @Override
    public Optional<CraftingResult> getResult(CraftingMatrix craftingMatrix, @Nullable World world) {
        final Result result = match(craftingMatrix, false, true);
        return result == null ? Optional.empty() : Optional.of(
                new CraftingResult(result.resultItem.createSnapshot(), result.remainingItems));
    }

    static final class Result {

        @Nullable private final ItemStack resultItem;
        @Nullable private final List<ItemStackSnapshot> remainingItems;

        Result(@Nullable ItemStack resultItem, @Nullable List<ItemStackSnapshot> remainingItems) {
            this.remainingItems = remainingItems;
            this.resultItem = resultItem;
        }
    }

    @Nullable
    abstract Result match(CraftingMatrix craftingMatrix, boolean resultItem, boolean remainingItems);
}
