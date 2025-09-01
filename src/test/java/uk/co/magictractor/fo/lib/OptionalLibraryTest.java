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

import org.junit.jupiter.api.Test;

public class OptionalLibraryTest {

    @Test
    public void testIsPresent_java() {
        boolean isPresent = new ExternalLibrary("java.lang.Object").isPresent();
        assertThat(isPresent).isTrue();
    }

    @Test
    public void testIsPresent_guava() {
        boolean isPresent = new ExternalLibrary("com.google.common.base.MoreObjects").isPresent();
        assertThat(isPresent).isTrue();
    }

    @Test
    public void testIsPresent_hibernate() {
        boolean isPresent = new ExternalLibrary("org.hibernate.Session").isPresent();
        assertThat(isPresent).isFalse();
    }

    @Test
    public void testIsAbsent_fop() {
        boolean isAbsent = new ExternalLibrary("org.apache.fop.apps.FOUserAgent").isAbsent();
        assertThat(isAbsent).isFalse();
    }

    @Test
    public void testIsAbsent_spring() {
        boolean isAbsent = new ExternalLibrary("org.springframework.ui.Model").isAbsent();
        assertThat(isAbsent).isTrue();
    }

}
