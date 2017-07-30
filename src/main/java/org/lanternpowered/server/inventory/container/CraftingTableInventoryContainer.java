package org.lanternpowered.server.inventory.container;

import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.inventory.LanternContainer;
import org.lanternpowered.server.inventory.block.ICraftingTableInventory;
import org.lanternpowered.server.inventory.entity.LanternPlayerInventory;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutOpenWindow;
import org.spongepowered.api.text.Text;

public class CraftingTableInventoryContainer extends LanternContainer {

    public CraftingTableInventoryContainer(LanternPlayerInventory playerInventory, ICraftingTableInventory openInventory) {
        super(playerInventory, openInventory);
    }

    @Override
    protected void openInventoryFor(LanternPlayer viewer) {
        viewer.getConnection().send(new MessagePlayOutOpenWindow(this.windowId, MessagePlayOutOpenWindow.WindowType.CRAFTING_TABLE,
                Text.of(this.openInventory.getName()), this.openInventory.getSlots().size(), 0));
    }
}
