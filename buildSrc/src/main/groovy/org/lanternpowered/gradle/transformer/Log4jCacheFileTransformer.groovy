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
package org.lanternpowered.gradle.transformer

import org.codehaus.plexus.util.IOUtil
import org.gradle.api.file.FileTreeElement
import org.apache.tools.zip.ZipEntry
import org.apache.tools.zip.ZipOutputStream
import org.apache.logging.log4j.core.config.plugins.processor.PluginCache
import com.github.jengelman.gradle.plugins.shadow.transformers.Transformer
import com.github.jengelman.gradle.plugins.shadow.relocation.Relocator

import static org.apache.logging.log4j.core.config.plugins.processor.PluginProcessor.PLUGIN_CACHE_FILE

import org.lanternpowered.gradle.io.DelegateOutputStream

/**
 * This is a modification of the maven plugin:
 *     https://github.com/edwgiz/maven-shaded-log4j-transformer
 * but modified to work with gradle and the shadow plugin.
 */
class Log4jCacheFileTransformer implements Transformer {

    private final List<File> tempFiles = []

    @Override
    boolean canTransformResource(FileTreeElement element) {
        return PLUGIN_CACHE_FILE.equals(element.relativePath.pathString)
    }

    @Override
    void transform(String path, InputStream is, List<Relocator> relocators) {
        def tempFile = File.createTempFile('Log4j2Plugins', '.dat')
        def fos = new FileOutputStream(tempFile)
        try {
            IOUtil.copy(is, fos)
        } finally {
            fos.close()
            is.close()
        }
        this.tempFiles.add(tempFile)
    }

    @Override
    boolean hasTransformedResource() {
        return this.tempFiles.size() > 1
    }

    @Override
    void modifyOutputStream(ZipOutputStream os) {
        try {
            def aggregator = new PluginCache()
            aggregator.loadCacheFiles(Collections.enumeration(this.tempFiles.collect { it.toURI().toURL() }))
            os.putNextEntry(new ZipEntry(PLUGIN_CACHE_FILE))
            aggregator.writeCache(new DelegateOutputStream(os) {
                @Override
                public void close() throws IOException {
                    // Make sure that the write method
                    // doesn't close the stream
                }
            })
        } finally {
            this.tempFiles.each { it.delete() }
        }
    }
}
