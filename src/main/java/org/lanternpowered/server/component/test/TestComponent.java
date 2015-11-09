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
package org.lanternpowered.server.component.test;

import org.lanternpowered.server.component.AttachableTo;
import org.lanternpowered.server.component.Component;
import org.lanternpowered.server.component.ComponentHolder;
import org.lanternpowered.server.component.OnAttach;
import org.lanternpowered.server.component.OnDetach;
import org.lanternpowered.server.component.Require;
import org.lanternpowered.server.inject.Inject;

@AttachableTo(ComponentHolder.class)
public class TestComponent implements Component {

    @Inject public ComponentHolder holder;
    @Inject @Require public ExtendedOtherComponent other;
    public AnotherTestComponent another;

    @OnAttach
    public void onAttach() {
        System.out.println("onAttach");
    }

    @OnDetach
    public void onDetach() {
    }

    @Inject
    private void setOtherComponent(@Require AnotherTestComponent another) {
        System.out.println("Set other test component: " + another);
        this.another = another;
    }
}
