/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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
package org.lanternpowered.server.command.element;

import static org.lanternpowered.server.text.translation.TranslationHelper.t;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Color;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

public class ColorElement extends CommandElement {

    public static ColorElement of(@Nullable Text key) {
        return new ColorElement(key, null, false);
    }

    public static ColorElement of(@Nullable Text key, Color defaultColor, boolean preferHex) {
        return new ColorElement(key, defaultColor, preferHex);
    }

    @Nullable private final Color defaultColor;
    private final boolean preferHex;

    protected ColorElement(@Nullable Text key, @Nullable Color defaultColor, boolean preferHex) {
        super(key);
        this.defaultColor = defaultColor;
        this.preferHex = preferHex;
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        String rStr = args.next();
        // Check for hex format
        if (rStr.startsWith("0x") || rStr.startsWith("#")) {
            // Get the hex value without the prefix
            String value = rStr.substring(rStr.startsWith("0x") ? 2 : 1);
            int hex;
            try {
                hex = Integer.parseInt(value, 16);
            } catch (NumberFormatException e) {
                throw args.createError(t("Expected input %s to be hexadecimal, but it was not"));
            }
            return Color.ofRgb(hex);
        } else {
            String gStr;
            String bStr;
            if (rStr.contains(",")) {
                String[] split = rStr.split(",");
                if (split.length != 3) {
                    throw args.createError(t("Comma-separated color must have 3 elements, not %s", split.length));
                }
                rStr = split[0];
                gStr = split[1];
                bStr = split[2];
            } else {
                gStr = args.next();
                bStr = args.next();
            }
            int r = parseComponent(args, rStr, "r");
            int g = parseComponent(args, gStr, "g");
            int b = parseComponent(args, bStr, "b");
            return Color.of(new Vector3i(r, g, b));
        }
    }

    private static int parseComponent(CommandArgs args, String arg, String name) throws ArgumentParseException {
        try {
            int value = Integer.parseInt(arg);
            if (value < 0) {
                throw args.createError(t("Number %s for %s component is too small, it must be at least %s", value, name, 0));
            }
            if (value > 255) {
                throw args.createError(t("Number %s for %s component is too big, it must be at most %s", value, name, 255));
            }
            return value;
        } catch (NumberFormatException e) {
            throw args.createError(t("Expected input %s for %s component to be a number, but it was not", arg, name));
        }
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        Optional<String> arg = args.nextIfPresent();
        if (!arg.isPresent() || this.defaultColor == null) {
            return Collections.emptyList();
        }
        if (args.nextIfPresent().isPresent()) {
            if (args.nextIfPresent().isPresent()) {
                // Store the current state
                Object state = args.getState();
                if (args.nextIfPresent().isPresent()) {
                    // We finished the vector3d, reset before the last arg
                    args.setState(state);
                } else {
                    // The blue is being completed
                    return Collections.singletonList(Integer.toString(this.defaultColor.getBlue()));
                }
            } else {
                // The green is being completed
                return Collections.singletonList(Integer.toString(this.defaultColor.getGreen()));
            }
        } else {
            // The red/hex is being completed
            final String arg0 = arg.get();

            String hexPref = null;
            boolean completeHex = false;
            if ((this.preferHex && arg0.isEmpty()) || arg0.startsWith("0x")) {
                completeHex = true;
                hexPref = "0x";
            } else if (arg0.startsWith("#")) {
                completeHex = true;
                hexPref = "#";
            }

            if (completeHex) {
                return Collections.singletonList(hexPref + Integer.toHexString(this.defaultColor.getRgb()));
            } else {
                if (arg0.contains(",")) {
                    int partCount = arg0.split(",").length;
                    int index = arg0.lastIndexOf(',');
                    if (partCount > 3) {
                        return Collections.emptyList();
                    }
                    String begin = index == -1 ? "" : arg0.substring(0, index + 1);
                    int value = partCount == 0 ? this.defaultColor.getRed() : partCount == 1 ? this.defaultColor.getGreen() :
                                    this.defaultColor.getBlue();
                    return Collections.singletonList(begin + Integer.toString(value));
                }
                return Collections.singletonList(Integer.toString(this.defaultColor.getRed()));
            }
        }
        return Collections.emptyList();
    }
}
