package org.lanternpowered.server.block.type;

import org.lanternpowered.server.block.LanternBlockType;
import org.spongepowered.api.data.property.block.MatterProperty.Matter;

public class BlockBedrock extends LanternBlockType {

    public BlockBedrock(String identifier) {
        super(identifier, Matter.SOLID);
    }
}
