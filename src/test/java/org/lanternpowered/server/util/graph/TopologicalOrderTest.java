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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;
import org.lanternpowered.server.util.graph.DirectedGraph.DataNode;

import java.util.List;

public class TopologicalOrderTest {

    @Test
    public void testEmptyGraph() {
        final DirectedGraph<Integer> graph = new DirectedGraph<>();
        final List<Integer> order = TopologicalOrder.createOrderedLoad(graph);
        assertNotNull(order);
        assertTrue(order.isEmpty());
    }

    @Test
    public void testSingleGraph() {
        //
        //          1
        //
        final DirectedGraph<Integer> graph = new DirectedGraph<>();
        graph.add(1);

        final List<Integer> order = TopologicalOrder.createOrderedLoad(graph);
        assertNotNull(order);
        assertEquals(1, order.size());
        assertEquals(Integer.valueOf(1), order.get(0));
    }

    @Test
    public void testSimpleGraph() {
        //
        //          1 - 2 - 3 - 4
        //
        final DirectedGraph<Integer> graph = new DirectedGraph<>();
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(3, 4);

        final List<Integer> order = TopologicalOrder.createOrderedLoad(graph);
        assertNotNull(order);
        assertEquals(4, order.size());
        assertEquals(Integer.valueOf(4), order.get(0));
        assertEquals(Integer.valueOf(3), order.get(1));
        assertEquals(Integer.valueOf(2), order.get(2));
        assertEquals(Integer.valueOf(1), order.get(3));
    }

    @Test
    public void testSelfCycle() {
        //
        //          1 - .
        //           \ /
        //            .
        //
        final DirectedGraph<Integer> graph = new DirectedGraph<>();
        graph.addEdge(1, 1);
        try {
            TopologicalOrder.createOrderedLoad(graph);
            Assert.fail();
        } catch (CyclicGraphException e) {
            final List<DataNode<?>[]> cycles = e.getCycles();
            assertEquals(1, cycles.size());
            final DataNode<?>[] cycle1 = cycles.get(0);
            assertEquals(1, cycle1.length);
            assertEquals(1, cycle1[0].getData());
        }
    }

    @Test
    public void testSimpleCycle() {
        //
        //          1 - 2
        //           \ /
        //            3
        //
        final DirectedGraph<Integer> graph = new DirectedGraph<>();
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(3, 1);
        try {
            TopologicalOrder.createOrderedLoad(graph);
            Assert.fail();
        } catch (CyclicGraphException e) {
            final List<DataNode<?>[]> cycles = e.getCycles();
            assertEquals(1, cycles.size());
            final DataNode<?>[] cycle1 = cycles.get(0);
            assertEquals(3, cycle1.length);
            assertEquals(3, cycle1[0].getData());
            assertEquals(2, cycle1[1].getData());
            assertEquals(1, cycle1[2].getData());
        }
    }

    @Test
    public void testSimpleCycle2() {
        //
        //          1 - 2
        //          |\  |
        //          | \ |
        //          4 - 3
        //
        final DirectedGraph<Integer> graph = new DirectedGraph<>();
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(3, 1);
        graph.addEdge(3, 4);
        graph.addEdge(4, 1);
        try {
            TopologicalOrder.createOrderedLoad(graph);
            Assert.fail();
        } catch (CyclicGraphException e) {
            // The cycle detected outputs the largest cycle for any strongly
            // connected group
            final List<DataNode<?>[]> cycles = e.getCycles();
            assertEquals(1, cycles.size());
            final DataNode<?>[] cycle1 = cycles.get(0);
            assertEquals(4, cycle1.length);
            assertEquals(4, cycle1[0].getData());
            assertEquals(3, cycle1[1].getData());
            assertEquals(2, cycle1[2].getData());
            assertEquals(1, cycle1[3].getData());
        }
    }

    @Test
    public void testMulti() {
        //
        //          1 - 2 - 4 - 5
        //           \ /     \ /
        //            3       6
        //
        final DirectedGraph<Integer> graph = new DirectedGraph<>();
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(3, 1);
        graph.addEdge(2, 4);
        graph.addEdge(4, 5);
        graph.addEdge(5, 6);
        graph.addEdge(6, 4);
        try {
            TopologicalOrder.createOrderedLoad(graph);
            Assert.fail();
        } catch (CyclicGraphException e) {
            final List<DataNode<?>[]> cycles = e.getCycles();
            assertEquals(2, cycles.size());
            final DataNode<?>[] cycle1 = cycles.get(0);
            assertEquals(3, cycle1.length);
            assertEquals(6, cycle1[0].getData());
            assertEquals(5, cycle1[1].getData());
            assertEquals(4, cycle1[2].getData());
            final DataNode<?>[] cycle2 = cycles.get(1);
            assertEquals(3, cycle2.length);
            assertEquals(3, cycle2[0].getData());
            assertEquals(2, cycle2[1].getData());
            assertEquals(1, cycle2[2].getData());
        }
    }

}
