/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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
package org.lanternpowered.server.text;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Locale;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextRepresentation;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.TextMessageException;
import org.spongepowered.api.util.annotation.NonnullByDefault;

@NonnullByDefault
public class PlainTextRepresentation implements TextRepresentation {

    @Override
    public String to(Text text) {
        return this.to(text, Locale.ENGLISH);
    }

    @Override
    public String to(Text text, Locale locale) {
        return LegacyTextRepresentation.to(checkNotNull(text, "text"), checkNotNull(locale, "locale"), new StringBuilder(), null).toString();
    }

    @Override
    public Text from(String input) throws TextMessageException {
        return Texts.of(input);
    }

    @Override
    public Text fromUnchecked(String input) {
        return Texts.of(input);
    }

}
