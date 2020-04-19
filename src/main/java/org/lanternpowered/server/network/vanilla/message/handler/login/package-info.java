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
/**
 * Login uses the following steps:
 *
 * C -> S: Handshake with Next State set to 2 (login)
 * C -> S: Login Start
 * S -> C: Encryption Request (if in offline mode, this will be skipped and moved directly to the login success)
 * Client auth
 * C -> S: Encryption Response
 * Server auth, both enable encryption
 * S -> C: Set Compression and Login Success
 *
 * More info at: http://wiki.vg/Protocol#Login
 */
@org.checkerframework.framework.qual.DefaultQualifier(org.checkerframework.checker.nullness.qual.NonNull.class)
package org.lanternpowered.server.network.vanilla.message.handler.login;
