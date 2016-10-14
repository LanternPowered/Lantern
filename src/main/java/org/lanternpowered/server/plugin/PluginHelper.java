/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
