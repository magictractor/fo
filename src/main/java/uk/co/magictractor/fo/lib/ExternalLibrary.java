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

import java.util.Optional;

/**
 * <p>
 * Checks whether an optional third party is in the classpath by attempting to
 * load a class provided by that library.
 * </p>
 */
public class ExternalLibrary {

    private final String thirdPartyLibraryClassName;
    private String groupId;
    private String artifactId;

    private transient Boolean isPresent;
    // VersionInfo is likely to fail in some environments, so thirdPartyLibraryClassName is required
    // to check whether optional libraries are present and VersionInfo will be used to get the version if available.
    private transient Optional<VersionInfo> versionInfo;

    public ExternalLibrary(String thirdPartyLibraryClassName) {
        this.thirdPartyLibraryClassName = thirdPartyLibraryClassName;
    }

    public ExternalLibrary withGroupIdAndArtifactId(String groupId, String artifactId) {
        if (groupId == null || artifactId == null) {
            throw new IllegalArgumentException();
        }
        if (this.groupId != null) {
            throw new IllegalStateException("withGroupIdAndArtifactId() has already been called");
        }

        this.groupId = groupId;
        this.artifactId = artifactId;

        return this;
    }

    public VersionInfo getInfo() {
        Optional<VersionInfo> info = getInfoOptional();
        if (!info.isPresent()) {
            if (VersionInfo.all().isEmpty()) {
                throw new IllegalStateException("No Version info is available, investigation needed");
            }
            if (groupId == null) {
                throw new IllegalStateException("Version info could not be found, use withGroupIdAndArtifactId() so that version information can be determined");
            }
            // Perhaps there's a type in the groupId or artifactId.
            // Perhaps the version information could not be inferred for this lib, but was for others.
            throw new IllegalStateException("Version info could not be found, investigation needed");
        }

        return info.get();
    }

    public Optional<VersionInfo> getInfoOptional() {
        if (versionInfo == null) {
            if (groupId == null) {
                versionInfo = Optional.empty();
            }
            else {
                versionInfo = VersionInfo.forArtifactOptional(groupId, artifactId);
            }
        }
        return versionInfo;
    }

    public boolean isPresent() {
        if (isPresent == null) {
            isPresent = determineIsPresent();
        }
        return isPresent;
    }

    private boolean determineIsPresent() {
        try {
            Class.forName(thirdPartyLibraryClassName);
        }
        catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    public boolean isAbsent() {
        return !isPresent();
    }

}
