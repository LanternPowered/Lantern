package org.lanternpowered.server.network.message.caching;

import org.lanternpowered.server.network.message.caching.CachingHashGenerator.Equal;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(TYPE)
@Retention(RUNTIME)
@SuppressWarnings("rawtypes")
public @interface Caching {

    /**
     * Gets the caching hash generator class. The default generator is
     * the {@link Equal} one.
     * 
     * @return the caching has generator class
     */
    public Class<? extends CachingHashGenerator> value() default Equal.class;
}
