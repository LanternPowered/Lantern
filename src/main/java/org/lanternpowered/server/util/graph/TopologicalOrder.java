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

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.lanternpowered.server.util.graph.DirectedGraph.DataNode;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class TopologicalOrder {

    /**
     * Performs a topological sort over the directed graph, fir the purpose of
     * determining load order between a set of components where an edge is
     * representing a load-after dependency. For example an edge from node A to
     * node B signifies that A depends on B and that B must load before A, the
     * resulting topological order would therefore be {@code [B, A]}.
     *
     * @throws CyclicGraphException if the graph contains a cycle.
     */
    public static <T> List<T> createOrderedLoad(DirectedGraph<T> graph) {
        final List<T> orderedList = new ArrayList<>();
        while (graph.getNodeCount() != 0) {
            DirectedGraph.DataNode<T> next = null;
            for (DirectedGraph.DataNode<T> node : graph.getNodes()) {
                if (node.getEdgeCount() == 0) {
                    next = node;
                    break;
                }
            }
            if (next == null) {
                // We have a cycle
                // Find all cycles for reporting purposes
                final TarjanCycleDetector detector = new TarjanCycleDetector(graph);
                final List<DataNode<?>[]> cycles = detector.getCycles();
                final StringBuilder msg = new StringBuilder();
                msg.append("Graph is cyclic! Cycles:\n");
                for (DataNode<?>[] cycle : cycles) {
                    msg.append("[");
                    for (DataNode<?> node : cycle) {
                        msg.append(node.getData().toString()).append(" ");
                    }
                    msg.append("]\n");
                }
                throw new CyclicGraphException(cycles, msg.toString());
            }
            orderedList.add(next.getData());
            graph.remove(next.getData());
        }
        return orderedList;
    }

    /**
     * Uses Tarjan's strongly connected components algorithm to find all cycles
     * in a graph.
     */
    private static class TarjanCycleDetector {

        private DirectedGraph<?> graph;
        private int index = 0;
        private Deque<DataNode<?>> stack = new ArrayDeque<>();
        private Object2IntOpenHashMap<DataNode<?>> node_indices = new Object2IntOpenHashMap<>();
        private Object2IntOpenHashMap<DataNode<?>> lowlinks = new Object2IntOpenHashMap<>();
        private List<DataNode<?>[]> result = null;

        public TarjanCycleDetector(DirectedGraph<?> graph) {
            this.graph = graph;
        }

        public List<DataNode<?>[]> getCycles() {
            if (this.result != null) {
                return this.result;
            }
            this.result = new ArrayList<>();
            for (DataNode<?> node : this.graph.getNodes()) {
                if (!this.node_indices.containsKey(node)) {
                    strongconnect(node);
                }
            }
            return this.result;
        }

        private void strongconnect(DataNode<?> node) {
            this.node_indices.put(node, this.index);
            this.lowlinks.put(node, this.index);
            this.index++;
            this.stack.push(node);

            for (DataNode<?> adj : node.getAdjacent()) {
                if (!this.node_indices.containsKey(adj)) {
                    strongconnect(adj);
                    final int lowlink = Math.min(this.lowlinks.getInt(node), this.lowlinks.getInt(adj));
                    this.lowlinks.put(node, lowlink);
                } else if (this.stack.contains(adj)) {
                    final int lowlink = Math.min(this.lowlinks.getInt(node), this.node_indices.getInt(adj));
                    this.lowlinks.put(node, lowlink);
                }
            }

            if (this.lowlinks.getInt(node) == this.node_indices.getInt(node)) {
                final List<DataNode<?>> cycle = new ArrayList<>();
                DataNode<?> w;
                do {
                    w = this.stack.pop();
                    cycle.add(w);
                } while (w != node);
                this.result.add(cycle.toArray(new DataNode<?>[cycle.size()]));
            }
        }
    }
}
