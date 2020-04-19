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
package org.lanternpowered.server.plugin;

import static com.google.common.base.Preconditions.checkArgument;

import org.lanternpowered.server.util.graph.DirectedGraph;
import org.lanternpowered.server.util.graph.TopologicalOrder;

import java.util.List;
import java.util.Map;

final class PluginHelper {

    private PluginHelper() {
    }

    static List<PluginCandidate> sort(Iterable<PluginCandidate> candidates) {
        DirectedGraph<PluginCandidate> graph = new DirectedGraph<>();

        for (PluginCandidate candidate : candidates) {
            graph.add(candidate);
            for (PluginCandidate dependency : candidate.getDependencies()) {
                graph.addEdge(candidate, dependency);
            }
        }

        return TopologicalOrder.createOrderedLoad(graph);
    }

    static String formatRequirements(Map<String, String> requirements) {
        checkArgument(!requirements.isEmpty(), "Requirements cannot be empty");
        StringBuilder builder = new StringBuilder();

        boolean first = true;
        for (Map.Entry<String, String> entry : requirements.entrySet()) {
            if (first) {
                first = false;
            } else {
                builder.append(", ");
            }

            // Append plugin ID
            builder.append(entry.getKey());

            final String version = entry.getValue();
            if (version != null) {
                builder.append(" (Version ").append(version).append(')');
            }
        }

        return builder.toString();
    }

}
