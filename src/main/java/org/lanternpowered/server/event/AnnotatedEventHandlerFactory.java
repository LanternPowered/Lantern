package org.lanternpowered.server.event;

import java.lang.reflect.Method;

public interface AnnotatedEventHandlerFactory {

    AnnotatedEventHandler create(Object handle, Method method) throws Exception;
}
