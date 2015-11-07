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
package org.lanternpowered.server.component;

import java.util.Collection;
import java.util.Optional;

public interface ComponentHolder {

    /**
     * Attempts to add the component of the specified type to holder if it's not present and
     * returns it. This method may return a {@link IllegalArgumentException}
     * if the component type isn't applicable for this component holder.
     * 
     * @param type the component type
     * @return the component
     */
    <T extends Component> T addComponent(Class<T> type);

    /**
     * Gets the component of the specified type from the holder if it's present.
     * 
     * @param type the component type
     * @return the component
     */
    <T extends Component> Optional<T> getComponent(Class<T> type);

    /**
     * Gets all components of the specified type (or a child implementation).
     *
     * @param type the component type
     * @return the components
     */
    <T extends Component> Collection<T> getAllComponents(Class<T> type);

    /**
     * Gets the component of the specified type (not a child implementation)
     * from the holder if it is present.
     *
     * @param type the component type
     * @return the component
     */
    <T extends Component> Optional<T> getExactComponent(Class<T> type);

    /**
     * Attempts to remove the component of the specified type from the holder.
     * 
     * @param type the component type
     * @return the component
     */
    <T extends Component> Optional<T> removeComponent(Class<T> type);
}
