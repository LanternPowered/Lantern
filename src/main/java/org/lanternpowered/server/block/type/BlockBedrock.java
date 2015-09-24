package org.lanternpowered.server.block.type;

import org.lanternpowered.server.block.LanternBlockType;

public class BlockBedrock extends LanternBlockType {

    public BlockBedrock(String identifier) {
        super(identifier, MaterialType.SOLID);
        this.setFullBlock(true);
    }
}
