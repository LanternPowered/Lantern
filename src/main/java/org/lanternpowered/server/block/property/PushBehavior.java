package org.lanternpowered.server.block.property;

/**
 * Represents the block behavior that occurs
 * when a block is pushed by a piston.
 */
public enum PushBehavior {
    /**
     * The block can't be pushed, nothing
     * will happen.
     */
    BLOCK,
    /**
     * The block can be pushed, the block
     * will be moved one block forward.
     */
    PUSH,
    /**
     * The block will be replaced ('destroyed')
     * when the block is pushed.
     */
    REPLACE,
    ;
}
