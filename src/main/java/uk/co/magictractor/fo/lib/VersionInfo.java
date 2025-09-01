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

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import com.google.common.base.MoreObjects;
import com.google.common.collect.MoreCollectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// TODO! rework this. Some configuration might be desirable, for example to ignore IDE paths.
// Perhaps VersionInfoFactory and ExternalLibraries has a static (and settable) VersionInfoFactory?
//
// TODO! would be nice to also lookup based on the class (where Export Packages is defined)
public class VersionInfo {

    private static final Log LOG = LogFactory.getLog(VersionInfo.class);

    private static final List<VersionInfo> ALL_INSTANCES = new ArrayList<>();

    static {
        try {
            init();
        }
        catch (Exception e) {
            LOG.error("Failed to initialise VersionInfo instances", e);
        }
    }

    private static void init() throws Exception {
        Enumeration<URL> urlEnum = ExternalLibrary.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
        while (urlEnum.hasMoreElements()) {
            // Allow individual jars to fail and continue with the others.
            // There can be non-standard paths, such as java agents added by IDEs.
            try {
                ALL_INSTANCES.add(new VersionInfo(urlEnum.nextElement()));
            }
            catch (Exception e) {
                LOG.error("Failed to initialise VersionInfo instance for " + urlEnum, e);
            }
        }
    }

    public static List<VersionInfo> all() {
        return Collections.unmodifiableList(ALL_INSTANCES);
    }

    public static VersionInfo forArtifact(String groupId, String artifactId) {
        return forArtifactOptional(groupId, artifactId)
                .orElseThrow(() -> new IllegalArgumentException("VersionInfo not found"));
    }

    public static Optional<VersionInfo> forArtifactOptional(String groupId, String artifactId) {
        return ALL_INSTANCES.stream()
                .filter(info -> artifactId.equals(info.artifactId))
                .filter(info -> groupId.equals(info.groupId))
                .collect(MoreCollectors.toOptional());
    }

    public static VersionInfo forClassOrPackage(String classOrPackageName) {
        return forClassOrPackageOptional(classOrPackageName)
                .orElseThrow(() -> new IllegalArgumentException("VersionInfo not found for class or package name \"" + classOrPackageName + "\""));
    }

    public static Optional<VersionInfo> forClassOrPackageOptional(String classOrPackageName) {
        return Optional.empty();
    }

    public static VersionInfo forImplementationTitle(String implementationTitle) {
        return forImplementationTitleOptional(implementationTitle)
                .orElseThrow(() -> new IllegalArgumentException("VersionInfo not found for implementation title \"" + implementationTitle + "\""));
    }

    public static Optional<VersionInfo> forImplementationTitleOptional(String implementationTitle) {
        for (VersionInfo candidate : ALL_INSTANCES) {
            if (implementationTitle.equals(candidate.implementationTitle)) {
                return Optional.of(candidate);
            }
        }
        return Optional.empty();
    }

    // These read from MANIFEST.MF
    private String implementationTitle;
    private String implementationVersion;
    private String implementationVendor;
    private String exportPackage;

    // These inferred from the jar name and path.
    private String groupId;
    private String artifactId;
    private String version;

    // Can get the version number from
    // 1) the manifest
    // 2) the gradle/maven path (directory containing the jar)
    // 3) the jar file name (after last hyphen)
    // Also get Export-Package?
    // Export-Package: io.micrometer.common;
    private VersionInfo(URL manifestUrl) throws Exception {
        URLConnection manifestConnection = manifestUrl.openConnection();
        parseUrl(manifestConnection);
        readManifest(manifestConnection);
    }

    private void parseUrl(URLConnection manifestConnection) throws IOException, URISyntaxException {
        if (!manifestConnection.getURL().getProtocol().equals("jar")) {
            // throw new IllegalArgumentException("Code needs modification to handle protocol " + manifestUrl.getProtocol());
            LOG.warn("Code needs modification to handle manifest URL protocol " + manifestConnection.getURL().getProtocol() + ": in " + manifestConnection.getURL());
            return;
        }

        URL jarUrl = ((JarURLConnection) manifestConnection).getJarFileURL();
        if (!jarUrl.getProtocol().equals("file")) {
            // throw new IllegalArgumentException("Code needs modification to handle protocol " + manifestUrl.getProtocol());
            LOG.warn("Code needs modification to handle jar URL protocol " + jarUrl.getProtocol());
            return;
        }

        Path jarPath = Paths.get(jarUrl.toURI());
        System.out.println(jarPath);

        String jarName = jarPath.getFileName().toString();
        if (!jarName.endsWith(".jar")) {
            throw new IllegalStateException();
        }
        // Cannot split file name into version and artifactId yet
        // because version may contain a hyphen.
        String jarBaseName = jarName.substring(0, jarName.length() - 4);

        Path dir = jarPath.getParent();
        if (!jarBaseName.endsWith(dir.getFileName().toString())) {
            // Tolerate one step between.
            // Gradle repositories have a randomised jar between the version folder and the jar.
            // Maven repositories do not.
            // C:\Users\Ken\.gradle\caches\modules-2\files-2.1\xml-apis\xml-apis-ext\1.3.04\41a8b86b358e87f3f13cf46069721719105aff66\xml-apis-ext-1.3.04.jar
            String skippedDir = dir.getFileName().toString();
            dir = dir.getParent();
            if (!jarBaseName.endsWith(dir.getFileName().toString())) {
                throw new IllegalStateException(
                    "Version mismatch, jar file base name is " + jarBaseName
                            + ", but ancestor directory names are " + skippedDir + " and " + dir.getFileName());
            }
        }

        version = dir.getFileName().toString();
        if (implementationVersion != null && !version.equals(implementationVersion)) {
            throw new IllegalStateException("Version mismatch");
        }

        dir = dir.getParent();
        artifactId = dir.getFileName().toString();
        if (!jarBaseName.equals(artifactId + "-" + version)) {
            throw new IllegalStateException("Jar file name does not match directory names");
        }

        dir = dir.getParent();
        groupId = dir.getFileName().toString();
    }

    private void readManifest(URLConnection manifestConnection) throws IOException {
        try (InputStream manifestStream = manifestConnection.getInputStream()) {
            readManifest(manifestStream);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void readManifest(InputStream manifestStream) throws IOException {
        Manifest manifest = new Manifest(manifestStream);
        Attributes manifestAttributes = manifest.getMainAttributes();
        implementationTitle = manifestAttributes.getValue("Implementation-Title");
        implementationVersion = manifestAttributes.getValue("Implementation-Version");
        implementationVendor = manifestAttributes.getValue("Implementation-Vendor");
        exportPackage = manifestAttributes.getValue("Export-Package");
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("artifact", groupId + ":" + artifactId + ":" + version)
                .add("implementationTitle", implementationTitle)
                .add("implementationVendor", implementationVendor)
                .omitNullValues()
                .toString();
    }

    // Apache Test does have Implementation-Version: 1.14.0
    // thirdPartyClass.getResourceAsStream("/META-INF/MANIFEST.MF") can return the manifest for
    // a different lib.
    //
    //    Specification-Title: Apache Commons Text
    //    Specification-Version: 1.14
    //    Specification-Vendor: The Apache Software Foundation
    //    Implementation-Title: Apache Commons Text
    //    Implementation-Version: 1.14.0
    //    Implementation-Vendor: The Apache Software Foundation
    public static void main(String[] args) throws Exception {
        // TODO! also findByTitle. and Optional variants.
        //VersionInfo spring = VersionInfo.forClassOrPackage("org.springframework.web.util.HtmlUtils");

        //Properties properties = System.getProperties();
        //properties.list(System.out);

        // No env variables for Gradle or Maven.
        for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }

        for (VersionInfo info : ALL_INSTANCES) {
            System.out.println();
            System.out.println(info);
            System.out.println(info.exportPackage);
        }
    }

}
