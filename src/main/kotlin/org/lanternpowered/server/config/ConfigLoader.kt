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
package org.lanternpowered.server.config

import org.lanternpowered.api.world.difficulty.Difficulties

class ConfigLoader {

    /**
     * Loads a config from the tree node.
     */
    fun <T : ConfigObject> load(node: TreeNode, factory: Factory<T>): T {
        TODO()
    }
}

fun test() {
    val node = TreeNode()
    val loader = ConfigLoader()
    val config = WorldConfigObject.from(node)
    config.chunks.test = true
    config.difficulty = Difficulties.HARD.get()
/*
    val config = ConfigLoader()
    val value = config.get { WorldConfigSpec.chunks.test }
    config.set { WorldConfigSpec.chunks.test } to value*/
}
