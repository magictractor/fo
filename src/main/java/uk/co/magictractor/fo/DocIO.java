/**
 * Copyright 2023 Ken Dobson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.magictractor.fo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.common.io.ByteStreams;

/**
 *
 */
public class DocIO {

    private final Path dir;
    private final String fileNameBase;

    public DocIO(String fileNameBase) {
        // TODO! something more robust (optional environment variable?)
        this(Paths.get(System.getProperty("user.dir"), "docs"), fileNameBase);
    }

    public DocIO(Path dir, String fileNameBase) {
        this.dir = dir;
        this.fileNameBase = fileNameBase;
        try {
            Files.createDirectories(dir);
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public String fileName(String fileNameExtension) {
        if (!fileNameExtension.startsWith(".")) {
            throw new IllegalArgumentException("File name extension must start with '.'");
        }
        return fileNameBase + fileNameExtension;
    }

    public Path resolve(String fileNameExtension) {
        return dir.resolve(fileName(fileNameExtension));
    }

    /**
     * Note that the returned stream is NOT buffered.
     */
    public OutputStream newOutputStream(String fileNameExtension) throws IOException {
        return Files.newOutputStream(resolve(fileNameExtension));
    }

    /**
     * Note that the returned stream is NOT buffered.
     */
    public InputStream newInputStream(String fileNameExtension) throws IOException {
        return Files.newInputStream(resolve(fileNameExtension));
    }

    public void transferTo(ByteArrayInputStream in, String fileNameExtension) throws IOException {
        try (OutputStream out = newOutputStream(fileNameExtension)) {
            ByteStreams.copy(in, out);
        }
    }

}
