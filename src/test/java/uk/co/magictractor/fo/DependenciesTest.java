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

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

/**
 *
 */
public class DependenciesTest {

    /**
     * This test will fail if a new transitive dependency on commons-logging is
     * added. An exclusion should be added as done for
     * {@code org.apache.xmlgraphics:fop}.
     */
    @Test
    public void testCommonsLogging() {
        assertThatThrownBy(() -> Class.forName("org.apache.commons.logging.impl.Jdk14Logger"))
                .isExactlyInstanceOf(ClassNotFoundException.class);
    }

}
