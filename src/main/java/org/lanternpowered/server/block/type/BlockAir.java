package org.lanternpowered.server.block.type;

import org.lanternpowered.server.block.LanternBlockType;
import org.spongepowered.api.data.property.block.MatterProperty.Matter;

public class BlockAir extends LanternBlockType {

    public BlockAir(String identifier) {
        super(identifier, Matter.GAS);
    }
}
