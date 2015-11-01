/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.network.session;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;
import com.google.common.collect.Sets;

/**
 * A list of all the sessions which provides a convenient {@link #pulse()}
 * method to pulse every session in one operation.
 */
public class SessionRegistry {

    private final Set<Session> sessions = Sets.newConcurrentHashSet();

    /**
     * Pulses all the sessions.
     */
    public void pulse() {
        for (Session session : this.sessions) {
            session.pulse();
        }
    }

    /**
     * Adds a new session to the registry.
     * 
     * @param session the session to add
     */
    public void add(Session session) {
        checkNotNull(session, "session");
        this.sessions.add(session);
    }

    /**
     * Removes a session from the registry.
     * 
     * @param session the session to remove
     */
    public void remove(Session session) {
        checkNotNull(session, "session");
        this.sessions.remove(session);
    }
}
