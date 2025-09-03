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
package uk.co.magictractor.fo.entityset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.co.magictractor.fo.unescape.FoHtml4Unescaper;
import uk.co.magictractor.fo.unescape.Unescaper;

/**
 * Parser for entity declaration files, typically copied from W3C.
 *
 * @see https://www.w3.org/TR/xml-entity-names
 */
public class EntitySetBuilder {

    private static final Log LOG = LogFactory.getLog(EntitySetBuilder.class);

    private HashMap<String, String> map;

    private int expectedSize;
    private float loadFactor = 0.75f;

    /**
     * <p>
     * Sets the expected size of the {@code Map()} that will be returned by
     * {@code build()}. This should be precise in order to prevent any resizing
     * of the Map {@code Map()} while values are added and to avoid the
     * {@code Map()} taking up more memory than necessary.
     * </p>
     * <p>
     * Typically, code using {@code EntityDeclarationsReader} will be set up to
     * read the required files, run once and then {@code withExpectedSize()}
     * added to the reader with the value in the warning produced by the
     * {@code build()} method.
     * </p>
     */
    public EntitySetBuilder withExpectedSize(int expectedSize) {
        checkMapIsNull();
        this.expectedSize = expectedSize;
        return this;
    }

    /**
     * Not sure this will be useful, but will be worth testing to see if lookups
     * are faster with a load factor smaller than the default 0.75. That would
     * reduce the number of hash collisions, but would increase the memory
     * required.
     */
    public EntitySetBuilder withLoadFactor(float loadFactor) {
        checkMapIsNull();
        this.loadFactor = loadFactor;
        return this;
    }

    private void checkMapIsNull() {
        if (map != null) {
            throw new IllegalStateException("The map has already been initialised. Expected size and load factor must be set up before adding any values.");
        }
    }

    public EntitySetBuilder withEntity(Entity entity) {
        return withEntity(entity.getName(), entity.getValue());
    }

    public EntitySetBuilder withEntity(String name, String value) {
        if (map == null) {
            initMap();
        }
        putEntity(name, value);
        return this;
    }

    // No. Use reflection to call initMap()
    //
    //
    //    // Other may be empty
    //    public void withEntries(Map<String, String> other) {
    //        if (map == null) {
    //            initMap();
    //        }
    //        for (Map.Entry<String, String> entry : other.entrySet()) {
    //            addEntry(entry.getKey(), entry.getValue());
    //        }
    //    }

    private void initMap() {
        if (expectedSize == 0) {
            // No warning here, the warning will be produced in build() where the size is known.
            map = new HashMap<>(256, loadFactor);
        }
        else {
            // See Guava's Maps.newHashMapWithExpectedSize(int size) and
            // https://stackoverflow.com/questions/10901752/what-is-the-significance-of-load-factor-in-hashmap
            int initialCapacity = Math.round(0.5f + expectedSize / loadFactor);
            map = new HashMap<>(initialCapacity, loadFactor);
        }
    }

    public EntitySetBuilder withEntityDeclarationsResource(String resourceName) {
        return this.withResource(resourceName, EntitySetBuilder::parseEntityDeclaration);
    }

    public EntitySetBuilder withEntityDeclarationsResource(String resourceName, BiConsumer<EntitySetBuilder, Entity> entityConsumer) {
        return this.withResource(resourceName, EntitySetBuilder::parseEntityDeclaration, entityConsumer);
    }

    public EntitySetBuilder withResource(String resourceName, Function<String, List<Entity>> parseFunction) {
        // Use private putEntity() so that there's only check for map initialisation.
        return withResource(resourceName, parseFunction, (b, e) -> b.putEntity(e.getName(), e.getValue()));
    }

    /**
     * <p>
     * The {@code Function} parses an {@code Entity} from a line of text read
     * from the resource, it may return {@code null} to indicate no
     * {@code Entity}.
     * </p>
     * <p>
     * This is intended for simple cases like entity declarations where the
     * entity name and value can be extracted from a single line. However, the
     * {@code Function} could be on a stateful object to handle multiline
     * parsing; but in such cases it might be easier to parse externally and
     * call {@link EntitySetBuilder#withEntry()}. Use of an
     * {@code EntitySetBuilder} is still recommended to create an optimal
     * {@code Map} efficiently.
     * </p>
     */
    public EntitySetBuilder withResource(String resourceName, Function<String, List<Entity>> parseFunction, BiConsumer<EntitySetBuilder, Entity> entityConsumer) {
        if (map == null) {
            initMap();
        }

        int sizeBefore = map.size();
        try {
            read0(resourceName, parseFunction, entityConsumer);
        }
        catch (IOException e) {
            throw new UnsupportedOperationException(e);
        }

        if (map.size() == sizeBefore) {
            LOG.warn("No data read from " + resourceName);
        }

        return this;
    }

    private void read0(String resourceName, Function<String, List<Entity>> parseFunction, BiConsumer<EntitySetBuilder, Entity> entityConsumer) throws IOException {
        try (InputStream in = getClass().getResourceAsStream(resourceName)) {
            if (in == null) {
                throw new IllegalArgumentException("Resource not found: " + resourceName);
            }
            read0(in, parseFunction, entityConsumer);
        }
    }

    private void read0(InputStream reader, Function<String, List<Entity>> parseFunction, BiConsumer<EntitySetBuilder, Entity> entityConsumer) throws IOException {
        String line;
        try (BufferedReader lineReader = new BufferedReader(new InputStreamReader(reader))) {
            while ((line = lineReader.readLine()) != null) {
                List<Entity> entities = parseFunction.apply(line);
                if (entities != null) {
                    for (Entity entity : entities) {
                        // putEntity(entity.getName(), entity.getValue());
                        entityConsumer.accept(this, entity);
                    }
                }
            }
        }
    }

    public static List<Entity> parseEntityDeclaration(String line) {
        // TODO! something better than a new Unescaper instance every time.
        // Maybe a singleton FoXmlUnescaper?
        // Limit to the predefined XML entity set so that the Unescaper does not have a dependency on an EntitySetBuilder.
        Unescaper unescaper = new FoHtml4Unescaper(EntitySets.xmlPredefined());

        if (!line.startsWith("<!ENTITY ")) {
            return null;
        }

        int nameStartIndex = 9;
        int nameEndIndex = line.indexOf(' ', nameStartIndex + 1);
        int tabIndex = line.indexOf('\t', nameStartIndex + 1);
        String name;
        if (tabIndex != -1 && tabIndex < nameEndIndex) {
            name = line.substring(nameStartIndex, tabIndex);
            LOG.trace("Name is followed by a tab: " + name);
        }
        else {
            name = line.substring(nameStartIndex, nameEndIndex);
        }

        //return new Entity(line, line);
        int valueStartIndex = line.indexOf('\"');
        int valueEndIndex = line.indexOf('\"', valueStartIndex + 1);
        String escapedValue = line.substring(valueStartIndex + 1, valueEndIndex);
        if (escapedValue.charAt(0) == ' ') {
            escapedValue = escapedValue.substring(1);
            LOG.debug("Removed leading space from value of " + name);
        }

        String value = unescaper.unescape(escapedValue);
        if (value.length() > 1) {
            if (value.charAt(0) == '&') {
                // TODO! find an explanation for double escapes
                value = unescaper.unescape(value);
                LOG.debug("Double escape in " + escapedValue + " for entity " + name);
            }
            // Ah, this is OK in HTML 5, some entities are two chars.
            //            if (value.codePointCount(0, value.length()) != 1) {
            //                throw new IllegalStateException("Expected a single code point when unescaping "
            //                        + escapedValue + ", but codePointCount=" + value.codePointCount(0, value.length()));
            //            }
        }
        // temp
        //LOG.debug(name + " -> " + value + " [" + escapedValue + "]");

        return Collections.singletonList(new Entity(name, value));
    }

    // https://www.w3.org/TR/xml-entity-names/#chars_math-combining-tables
    // As detailed in A.4 Entities Defined to be a Combining Character DownBreve, tdot, TripleDot and DotDot are no longer prefixed by a space.

    private void putEntity(String name, String value) {
        if (name.charAt(0) == '&') {
            throw new IllegalArgumentException();
        }
        if (name.endsWith(";")) {
            throw new IllegalArgumentException();
        }
        if (map.containsKey(name)) {
            // TODO! maybe allow if duplicate (configurable)
            throw new IllegalArgumentException("Entity " + name + " has already been defined");
        }
        map.put(name, value);
    }

    public boolean hasEntity(String name) {
        return map != null && map.containsKey(name);
    }

    /**
     * <p>
     * Remove an Entity that has previously been read or explicitly added. An
     * {@code IllegalArgumentException} will be thrown if there is no matching
     * entity, or if the existing entity value does not match the given value.
     * {@link #removeEntity(String)} maybe be used if value verification is not
     * wanted.
     * </p>
     * <p>
     * This method should be used with caution because the {@code Map} could
     * exceed the expected size and then shrink back causing resizes. It might
     * be better to filter entities using a {@code BiConsumer} when reading
     * resources.
     * </p>
     */
    public EntitySetBuilder removeEntity(Entity entity) {
        return removeEntity(entity.getName(), entity.getValue());
    }

    /**
     * <p>
     * Remove an Entity that has previously been read or explicitly added. An
     * {@code IllegalArgumentException} will be thrown if there is no matching
     * entity, or if the existing entity value does not match the given value.
     * {@link #removeEntity(String)} maybe be used if value verification is not
     * wanted.
     * </p>
     * <p>
     * This method should be used with caution because the {@code Map} could
     * exceed the expected size and then shrink back causing resizes. It might
     * be better to filter entities using a {@code BiConsumer} when reading
     * resources.
     * </p>
     */
    public EntitySetBuilder removeEntity(String name, String value) {
        if (map != null) {
            String currentValue = map.get(name);
            if (!currentValue.equals(value)) {
                throw new IllegalArgumentException();
            }
            map.remove(name);
            return this;
        }
        throw new IllegalArgumentException();
    }

    /**
     * <p>
     * Remove an Entity that has previously been read or explicitly added. An
     * {@code IllegalArgumentException} will be thrown if there is no matching
     * entity. The value of the entity is not checked, other {@removeEntity()}
     * methods may be used if the value should be verified.
     * </p>
     * <p>
     * This method should be used with caution because the {@code Map} could
     * exceed the expected size and then shrink back causing resizes. It might
     * be better to filter entities using a {@code BiConsumer} when reading
     * resources.
     * </p>
     */
    public EntitySetBuilder removeEntity(String name) {
        if (map != null && map.containsKey(name)) {
            map.remove(name);
            return this;
        }
        throw new IllegalArgumentException();
    }

    public Map<String, String> build() {
        if (map == null) {
            throw new IllegalStateException("No methods that provide data to the builder have been called");
        }
        if (map.isEmpty()) {
            // Do nothing, warning has already been logged.
        }
        else if (expectedSize == 0) {
            LOG.warn("withExpectedSize() was not used, using it with size=" + map.size() + " for this data will prevent Map resizes when reading data");
        }
        else if (expectedSize != map.size()) {
            LOG.warn("withExpectedSize() was used with an incorrect size, " + expectedSize + " was given, but it should be " + map.size() + " for optimal performance");
        }
        Map<String, String> result = map;
        reset();

        return result;
    }

    private void reset() {
        map = null;
        expectedSize = 0;
        // load factor is retained
    }

}
