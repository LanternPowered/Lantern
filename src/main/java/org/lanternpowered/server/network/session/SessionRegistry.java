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
