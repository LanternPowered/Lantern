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
package org.lanternpowered.server.config

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path

/**
 * Constructs a new loader for the given [URL].
 */
fun loaderOf(url: URL): Loader = URLLoader(url)

/**
 * Constructs a new loader for the given file [Path].
 */
fun loaderOf(path: Path): MutableWatchableLoader = FileLoader(path)

/**
 * Constructs a new loader from the given source.
 */
fun loaderOf(source: Source): Loader = SimpleLoader(source)

/**
 * Constructs a new loader from the given source and sink.
 */
fun loaderOf(source: Source, sink: Sink): MutableLoader = SimpleMutableLoader(source, sink)

/**
 * Represents a source of data.
 */
typealias Source = () -> BufferedReader

/**
 * Represents a sink which will be used
 * to save a configuration file.
 */
typealias Sink = () -> BufferedWriter

/**
 * Represents a loader.
 */
interface Loader {

    /**
     * The source provider to read the contents.
     */
    val source: Source
}

/**
 * Represents a loader that can also write content.
 */
interface MutableLoader : Loader {

    /**
     * The sink provider to write the contents.
     */
    val sink: Sink
}

/**
 * Represents a loader that can be watched for changes.
 */
interface WatchableLoader : Loader

/**
 * Represents a loader that can be watched for changes.
 */
interface MutableWatchableLoader : WatchableLoader, MutableLoader

/**
 * A simple loader.
 */
internal class SimpleLoader(override val source: Source) : Loader

/**
 * A simple mutable loader.
 */
internal class SimpleMutableLoader(override val source: Source, override val sink: Sink) : MutableLoader

/**
 * A URL loader.
 */
internal class URLLoader(val url: URL) : Loader {
    override val source: Source = { BufferedReader(InputStreamReader(this.url.openStream(), Charsets.UTF_8)) }
}

/**
 * A file loader.
 */
internal class FileLoader(val path: Path) : MutableWatchableLoader {
    override val source: Source = { Files.newBufferedReader(this.path) }
    override val sink: Sink = { Files.newBufferedWriter(this.path) }
}
