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
package org.lanternpowered.server.block.entity.vanilla

import org.lanternpowered.server.block.entity.LanternBlockEntity
import org.lanternpowered.server.network.block.BlockEntityProtocolTypes
import org.lanternpowered.server.permission.ProxySubject
import org.spongepowered.api.block.entity.Sign
import org.spongepowered.api.data.Keys
import org.spongepowered.api.service.permission.SubjectReference
import org.spongepowered.api.text.Text
import org.spongepowered.api.util.Tristate

class LanternSign : LanternBlockEntity(), ProxySubject, Sign {

    override val subjectCollectionIdentifier: String get() = "sign" // PermissionService.SUBJECTS_SIGN
    override var internalSubject: SubjectReference? = null

    init {
        protocolType = BlockEntityProtocolTypes.SIGN
        initializeSubject()
        keyRegistry {
            register(Keys.SIGN_LINES, mutableListOf(Text.of(), Text.of(), Text.of(), Text.of()))
        }
    }

    override fun getName() = this.identifier
    override fun getIdentifier() = "Sign[x=${location.blockX},y=${location.blockY},z=${location.blockZ}]"
    override fun getPermissionDefault(permission: String) = Tristate.TRUE
}
