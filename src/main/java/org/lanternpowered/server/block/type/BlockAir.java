package org.lanternpowered.server.block.type;

import org.lanternpowered.server.block.LanternBlockType;

public class BlockAir extends LanternBlockType {

    public BlockAir(String identifier) {
        super(identifier, MaterialType.GAS);
    }
}
