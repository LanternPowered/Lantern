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
package org.lanternpowered.launch.dependencies;

import static java.util.Objects.requireNonNull;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public final class DependenciesParser extends Exception {

    public static Dependencies read(final Reader reader) throws IOException, ParseException {
        final JSONParser parser = new JSONParser();
        final JSONObject object = (JSONObject) parser.parse(reader);
        final List<Repository> repositories = new ArrayList<>();
        JSONArray array = (JSONArray) object.get("repositories");
        if (array != null) {
            for (Object obj : array) {
                final String url;
                String name = null;
                if (obj instanceof JSONObject) {
                    final JSONObject jsonObject = (JSONObject) obj;
                    url = (String) jsonObject.get("url");
                    requireNonNull(url, "url");
                    name = (String) jsonObject.get("name");
                } else {
                    url = (String) obj;
                }
                repositories.add(new Repository(new URL(url), name));
            }
        }
        array = (JSONArray) object.get("dependencies");
        final List<Dependency> dependencies = new ArrayList<>();
        requireNonNull(array, "dependencies");
        for (Object obj : array) {
            if (obj instanceof JSONObject) {
                final JSONObject jsonObject = (JSONObject) obj;
                final String group = requireNonNull((String) jsonObject.get("group"), "group");
                final String name = requireNonNull((String) jsonObject.get("name"), "name");
                final String version = requireNonNull((String) jsonObject.get("version"), "version");
                dependencies.add(new Dependency(group, name, version));
            } else {
                final String string = (String) obj;
                final String[] parts = string.split(":");
                if (parts.length != 3) throw new IllegalStateException("Invalid string format: <group>:<name>:<version>");
                dependencies.add(new Dependency(parts[0], parts[1], parts[2]));
            }
        }
        return new Dependencies(repositories, dependencies);
    }
}
