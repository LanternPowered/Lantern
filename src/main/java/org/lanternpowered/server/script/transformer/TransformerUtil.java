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
package org.lanternpowered.server.script.transformer;

import org.lanternpowered.api.asset.Asset;

public final class TransformerUtil {

    /**
     * Generates a class name based on a {@link Asset} (resource) path.
     *
     * <p>For example: 'minecraft:my/script/file.sc' -> 'scripts.minecraft.my.script.File'
     * Underscores in the file name will capitalize the next letter and remove the underscore,
     * for example: 'minecraft:my/script/some_useful_file.sc' -> 'scripts.minecraft.my.script.SomeUsefulFile'<p/>
     * <p>Note: All script class will be located in the subpackage 'scripts'<p/>
     *
     * @param asset The asset
     * @return The class name
     */
    public static String generateClassNameFromAssetPath(String asset) {
        final StringBuilder builder = new StringBuilder();
        builder.append("scripts.");

        int index = asset.indexOf(':');
        if (index == -1) {
            builder.append("minecraft");
        } else {
            builder.append(asset.substring(0, index));
            asset = asset.substring(index + 1);
        }
        builder.append('.');

        final String[] parts = asset.replaceAll("\\\\", "/").split("/");
        for (int i = 0; i < parts.length - 1; i++) {
            builder.append(parts[i]);
            builder.append('.');
        }

        // Get the name of the file
        String name = parts[parts.length - 1];

        // Strip the extension from the file name
        index = name.lastIndexOf('.');
        if (index != -1) {
            name = name.substring(0, index);
        }

        final char[] chars = name.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i == 0) {
                builder.append(Character.toUpperCase(chars[i]));
            } else if (chars[i] == '_' || chars[i] == '-') {
                if (i != chars.length - 1) {
                    if (Character.toUpperCase(chars[i + 1]) != chars[i + 1]) {
                        builder.append(Character.toUpperCase(chars[i + 1]));
                        i++;
                    }
                }
            } else if (chars[i] == '.') {
                builder.append('_');
            } else {
                builder.append(chars[i]);
            }
        }

        return builder.toString();
    }

    private TransformerUtil() {
    }
}
