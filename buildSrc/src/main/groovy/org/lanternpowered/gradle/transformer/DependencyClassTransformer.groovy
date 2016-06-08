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
package org.lanternpowered.gradle.transformer

import com.github.jengelman.gradle.plugins.shadow.relocation.Relocator
import com.github.jengelman.gradle.plugins.shadow.transformers.Transformer
import org.apache.tools.zip.ZipEntry
import org.apache.tools.zip.ZipOutputStream
import org.codehaus.plexus.util.IOUtil
import org.gradle.api.file.FileTreeElement
import org.gradle.api.tasks.util.PatternSet
import org.lanternpowered.gradle.asm.ClassDependencyCollector

/**
 * TODO: A better class name
 */
class DependencyClassTransformer implements Transformer {

    /**
     * The files that should be added and checked for dependencies.
     */
    PatternSet filesToInclude = new PatternSet()

    /**
     * The paths of file/packages which may be added as dependencies
     * by the included files.
     */
    PatternSet pathsToCheck = new PatternSet()

    private final Set<String> includedFiles = new HashSet<>()
    private final Map<String, File> processedFiles = new HashMap<>()

    @Override
    boolean canTransformResource(FileTreeElement element) {
        if (this.filesToInclude.asSpec.isSatisfiedBy(element)) {
            this.includedFiles.add(element.relativePath.pathString)
            return true
        }
        if (this.pathsToCheck.asSpec.isSatisfiedBy(element)) {
            return true
        }
        return false
    }

    @Override
    void transform(String path, InputStream is, List<Relocator> relocators) {
        File tempFile = File.createTempFile(path, '.class')
        OutputStream fos = new FileOutputStream(tempFile)
        try {
            IOUtil.copy(is, fos)
        } finally {
            fos.close()
            is.close()
        }
        this.processedFiles.put(path, tempFile)
    }

    @Override
    boolean hasTransformedResource() {
        return true
    }

    @Override
    void modifyOutputStream(ZipOutputStream os) {
        try {
            Set<String> processedPaths = new HashSet<>()
            for (String path : this.includedFiles) {
                if (this.processedFiles.containsKey(path) && processedPaths.add(path)) {
                    this.modifyOutputStream0(processedPaths, os, path)
                }
            }
        } finally {
            this.processedFiles.each { it.getValue().delete() }
        }
    }

    private void modifyOutputStream0(Set<String> processedPaths, ZipOutputStream os, String path) {
        File file = this.processedFiles.get(path);
        if (file == null) {
            return;
        }
        InputStream is = new FileInputStream(file)
        try {
            os.putNextEntry(new ZipEntry(path))
            try {
                IOUtil.copy(is, os)
            } finally {
                is.close()
            }

            is = new FileInputStream(file)
            ClassDependencyCollector.collect(is).each {
                if (this.processedFiles.containsKey(path) && processedPaths.add(it)) {
                    this.modifyOutputStream0(processedPaths, os, it)
                }
            }
        } finally {
            is.close()
        }
    }
}
