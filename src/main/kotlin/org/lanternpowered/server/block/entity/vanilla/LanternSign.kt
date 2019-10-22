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
    }

    override fun getName() = this.identifier
    override fun getIdentifier() = "Sign[x=${location.blockX},y=${location.blockY},z=${location.blockZ}]"
    override fun getPermissionDefault(permission: String) = Tristate.TRUE

    public override fun registerKeys() {
        super.registerKeys()

        keyRegistry {
            register(Keys.SIGN_LINES, mutableListOf(Text.of(), Text.of(), Text.of(), Text.of()))
        }
    }
}
