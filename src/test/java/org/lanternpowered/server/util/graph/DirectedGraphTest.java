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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.lanternpowered.server.util.graph.DirectedGraph.DataNode;

public class DirectedGraphTest {

    @Test
    public void testNodeCount() {
        final DirectedGraph<Integer> graph = new DirectedGraph<>();
        assertEquals(0, graph.getNodeCount());
        graph.add(1);
        graph.add(2);
        assertEquals(2, graph.getNodeCount());
        graph.add(3);
        // 2 is a duplicate and shouldn't create a new node.
        graph.add(2);
        assertEquals(3, graph.getNodeCount());
        graph.remove(3);
        assertEquals(2, graph.getNodeCount());
        graph.clear();
        assertEquals(0, graph.getNodeCount());
    }

    @Test
    public void testEdgeCount() {
        final DirectedGraph<Integer> graph = new DirectedGraph<>();
        graph.addEdge(1, 3);
        graph.addEdge(2, 3);
        assertEquals(2, graph.getEdgeCount());
        graph.addEdge(3, 4);
        graph.addEdge(2, 3);
        assertEquals(3, graph.getEdgeCount());
        graph.get(1).removeEdge(graph.get(3));
        assertEquals(2, graph.getEdgeCount());
        graph.remove(2);
        assertEquals(1, graph.getEdgeCount());
    }

    @Test
    public void testContains() {
        final DirectedGraph<Integer> graph = new DirectedGraph<>();
        graph.add(1);
        graph.add(2);
        assertTrue(graph.contains(1));
        assertFalse(graph.contains(3));
    }

    @Test
    public void testGet() {
        final DirectedGraph<Integer> graph = new DirectedGraph<>();
        // any sequence of add and get should return the same node instance
        final DataNode<Integer> a = graph.add(1);
        final DataNode<Integer> b = graph.get(1);
        final DataNode<Integer> c = graph.add(1);
        final DataNode<Integer> d = graph.get(1);
        assertEquals(a, b);
        assertEquals(b, c);
        assertEquals(c, d);
    }

    @Test
    public void testReverse() {
        final DirectedGraph<Integer> graph = new DirectedGraph<>();
        graph.addEdge(1, 2);
        graph.addEdge(2, 4);
        graph.addEdge(3, 1);
        graph.addEdge(3, 4);
        graph.addEdge(4, 5);

        final DirectedGraph<Integer> rev = graph.reverse();
        assertEquals(5, rev.getEdgeCount());
        assertTrue(rev.get(2).isAdjacent(rev.get(1)));
        assertTrue(rev.get(4).isAdjacent(rev.get(2)));
        assertTrue(rev.get(1).isAdjacent(rev.get(3)));
        assertTrue(rev.get(4).isAdjacent(rev.get(3)));
        assertTrue(rev.get(5).isAdjacent(rev.get(4)));
    }

}
