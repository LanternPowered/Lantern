package org.lanternpowered.server.block.type;

import org.lanternpowered.server.block.LanternBlockType;

public final class BlockGrass extends LanternBlockType {

    public BlockGrass(String identifier) {
        super(identifier, MaterialType.SOLID);
        this.setFullBlock(true);
    }
}
