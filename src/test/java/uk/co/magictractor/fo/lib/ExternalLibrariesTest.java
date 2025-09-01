/**
 * Copyright 2025 Ken Dobson
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
package uk.co.magictractor.fo.lib;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ExternalLibrariesTest {

    private static List<String> GRADLE_CONFIG = new ArrayList<>();

    @BeforeAll
    public static void setUp() throws IOException {
        readGradleConfig();
    }

    /**
     * <p>
     * FOP version 2.9 {@code org.apache.fop.Version.getVersion()} only returns
     * "SVN". The code looks like it should return something meaningful, but the
     * Implementation Version is missing from the manifest. Fixed in 2.10.
     * <p>
     * <p>
     * Other version (2.7, 2.8, 2.10 and 2.11 checked) returns a correct value
     * from from this method which is read from
     * {@code Implementation-Version: 2.x} in MANIFEST.MF.
     * </p>
     */
    @Test
    public void testFopVersion_regression() {
        // Quick regression test. Actual value is tested below.
        assertThat(org.apache.fop.Version.getVersion()).isNotEqualTo("SVN");
    }

    @Test
    public void testVersion_fop() {
        checkVersionAgainstGradle(ExternalLibraries.FOP, "org.apache.xmlgraphics:fop-core:");
    }

    @Test
    public void testVersion_apache() {
        checkVersionAgainstGradle(ExternalLibraries.APACHE_TEXT, "org.apache.commons:commons-text:");
    }

    @Test
    public void testVersion_springWeb() {
        checkVersionAgainstGradle(ExternalLibraries.SPRING_WEB, "org.springframework:spring-web:");
    }

    @Test
    public void testVersion_jSoup() {
        checkVersionAgainstGradle(ExternalLibraries.JSOUP, "org.jsoup:jsoup:");
    }

    @Test
    public void testVersion_unbescape() {
        checkVersionAgainstGradle(ExternalLibraries.UNBESCAPE, "org.unbescape:unbescape:");
    }

    private void checkVersionAgainstGradle(ExternalLibrary lib, String groupIdAndArtifactId) {
        String libVersion = lib.getInfo().getVersion();
        String gradleVersion = findGradleVersion(groupIdAndArtifactId);

        assertThat(libVersion).isEqualTo(gradleVersion);
    }

    private String findGradleVersion(String groupIdAndArtifactId) {
        if (!groupIdAndArtifactId.endsWith(":")) {
            throw new IllegalArgumentException("groupIdAndArtifactId should end with a colon");
        }

        String line = null;
        for (String candidate : GRADLE_CONFIG) {
            if (candidate.contains(groupIdAndArtifactId)) {
                if (line == null) {
                    line = candidate;
                }
                else {
                    throw new IllegalStateException("Multiple lines contain " + groupIdAndArtifactId);
                }
            }
        }

        int startIndex = line.indexOf(groupIdAndArtifactId) + groupIdAndArtifactId.length();
        int endIndex = line.indexOf("\'", startIndex);

        return line.substring(startIndex, endIndex);
    }

    private static void readGradleConfig() throws IOException {
        Path gradle = Paths.get(System.getProperty("user.dir"), "fo.gradle");
        if (!Files.exists(gradle)) {
            throw new IllegalStateException("Failed to find gradle build script");
        }

        String line;
        try (BufferedReader configReader = new BufferedReader(new InputStreamReader(Files.newInputStream(gradle)))) {
            while ((line = configReader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("implementation ") || line.startsWith("compileOnly ")) {
                    line = line
                            .replace("implementation ", "")
                            .replace("compileOnly ", "")
                            .replace("(", "")
                            .replace(")", "")
                            .replace("{", "");
                    GRADLE_CONFIG.add(line);
                }
            }
        }

    }

}
