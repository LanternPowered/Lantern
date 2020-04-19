/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.util.graph;

import java.util.List;

/**
 * An exception for when a graph contains an unexpected cycle.
 */
public class CyclicGraphException extends RuntimeException {

    private static final long serialVersionUID = -8398890567263627095L;

    private final List<DirectedGraph.DataNode<?>[]> cycles;

    public CyclicGraphException(List<DirectedGraph.DataNode<?>[]> cycles, String msg) {
        super(msg);
        this.cycles = cycles;
    }

    /**
     * Gets a list of all cycles found in the graph.
     */
    public List<DirectedGraph.DataNode<?>[]> getCycles() {
        return this.cycles;
    }
}
