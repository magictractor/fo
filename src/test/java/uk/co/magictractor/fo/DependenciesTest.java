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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

/**
 *
 */
public class DependenciesTest {

    /**
     * This library uses Jakarta Commons Logging, as provided by Apache FOP.
     * This means that consumers deal with logging exactly as they if using
     * Apache FOP directly.
     */
    @Test
    public void testCommonsLogging() throws ClassNotFoundException {
        assertThat(Class.forName("org.apache.commons.logging.impl.Jdk14Logger"))
                .isNotNull();
    }

    /**
     * Jakarta Commons Logging is being used (see above). It can bridge to
     * SLF4J, but that is kept off the classpath to prevent SLF4J Loggers being
     * used accidentally.
     */
    @Test
    public void testSlf4j() {
        assertThatThrownBy(() -> Class.forName("org.slf4j.Logger"))
                .isExactlyInstanceOf(ClassNotFoundException.class);
    }

}
