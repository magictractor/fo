/**
 * Copyright 2024 Ken Dobson
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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

/**
 *
 */
public class VersionTest {

    /**
     * <p>
     * FOP version 2.9 {@code org.apache.fop.Version.getVersion()} only returns
     * "SVN". The code looks like it should return something meaningful, but the
     * Implementation Version is missing from the manifest. Fixed in 2.10.
     * <p>
     * <p>
     * Version 2.8 returns "2.8" from this method and includes
     * {@code Implementation-Version: 2.8} in MANIFEST.MF.
     * </p>
     * <p>
     * Version 2.7 also works as expected.
     * </p>
     */
    // https://stackoverflow.com/questions/921667/how-do-i-add-an-implementation-version-value-to-a-jar-manifest-using-maven
    @Test
    public void testFopVersion_direct() {
        // Quick regression test. Actual value is tested below.
        assertThat(org.apache.fop.Version.getVersion()).isNotEqualTo("SVN");
    }

    @Test
    public void testFopVersion_dependency() throws IOException {
        Path gradle = Paths.get(System.getProperty("user.dir"), "fo.gradle");

        if (!Files.exists(gradle)) {
            throw new IllegalStateException("Failed to find gradle build script");
        }

        assertThat(Version.fopVersion()).isEqualTo(findDependencyVersion(gradle, "org.apache.xmlgraphics:fop-core:"));
    }


    private String findDependencyVersion(Path gradle, String groupIdAndArtifact) throws IOException {
        if (!groupIdAndArtifact.endsWith(":")) {
            throw new IllegalArgumentException("groupIdAndArtifact should end with a colon");
        }

        String line;
        String fopDependencyLine = null;
        try (BufferedReader configReader = new BufferedReader(new InputStreamReader(Files.newInputStream(gradle)))) {
            while ((line = configReader.readLine()) != null) {
                if (line.contains(groupIdAndArtifact) && !line.trim().startsWith("//")) {
                    if (fopDependencyLine == null) {
                        fopDependencyLine = line;
                    }
                    else {
                        throw new IllegalStateException("Multiple lines contain " + groupIdAndArtifact);
                    }
                }
            }
        }

        int startIndex = fopDependencyLine.indexOf(groupIdAndArtifact) + groupIdAndArtifact.length();
        int endIndex = fopDependencyLine.indexOf("\"", startIndex);

        return fopDependencyLine.substring(startIndex, endIndex);
    }

}
