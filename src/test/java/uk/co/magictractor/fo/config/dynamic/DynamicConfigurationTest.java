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
package uk.co.magictractor.fo.config.dynamic;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import com.google.common.collect.Sets;

import org.apache.fop.configuration.Configuration;
import org.apache.fop.configuration.ConfigurationException;
import org.apache.fop.configuration.DefaultConfiguration;
import org.apache.fop.configuration.DefaultConfigurationBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class DynamicConfigurationTest {

    private static DefaultConfiguration xmlConfiguration;
    private static DynamicConfiguration dynamicConfiguration;

    @BeforeAll
    public static void setUp() throws IOException, ConfigurationException {
        try (InputStream confStream = DynamicConfigurationTest.class.getResourceAsStream("test_fop.xconf")) {
            xmlConfiguration = new DefaultConfigurationBuilder().build(confStream);
        }

        dynamicConfiguration = new FopDynamicConfiguration();
        DynamicConfiguration attrs = dynamicConfiguration.addChild("attrs");
        attrs.setAttribute("string", "hiya");
        attrs.setAttribute("boolean", true);
        attrs.setAttribute("int", 11);
        attrs.setAttribute("float", 1.23f);
        dynamicConfiguration.addChild("empty");
        dynamicConfiguration.addChild("space").setValue("    ");
        dynamicConfiguration.addChild("string").setValue("hello");
        dynamicConfiguration.addChild("boolean").setValue(true);
        dynamicConfiguration.addChild("yes").setValue("yes");
        dynamicConfiguration.addChild("int").setValue(5);
        dynamicConfiguration.addChild("float").setValue(1.61803398875f);
        dynamicConfiguration.addChild("li").setValue(1);
        dynamicConfiguration.addChild("li").setValue(2);
        dynamicConfiguration.addChild("li").setValue(3);
    }

    @Test
    public void testGetChild_noSuchChild() throws ConfigurationException {
        String className = checkSameValue(conf -> conf.getChild("nosuchchild").getClass().getSimpleName());
        // Different classes, but both called NullConfiguration.
        assertThat(className).isEqualTo("NullConfiguration");
    }

    @Test
    public void testGetChild_required_noSuchChild() throws ConfigurationException {
        String className = checkSameValue(conf -> conf.getChild("nosuchchild", true).getClass().getSimpleName());
        // Different classes, but both called NullConfiguration.
        assertThat(className).isEqualTo("NullConfiguration");
    }

    @Test
    public void testGetChild_required_hasChild() throws ConfigurationException {
        // Map to value because children are not equal.
        String value = checkSameValue(conf -> conf.getChild("int", true).getValue());
        assertThat(value).isEqualTo("5");
    }

    @Test
    public void testGetChild_notRequired_noSuchChild() throws ConfigurationException {
        checkSameValue(conf -> conf.getChild("nosuchchild", false));
    }

    @Test
    public void testGetChildren_noSuchChild() throws ConfigurationException {
        Configuration[] children = checkSameValue(conf -> conf.getChildren("nosuchchild"));
        assertThat(children).isEmpty();
    }

    @Test
    public void testGetChildren_single() throws ConfigurationException {
        int childrenCount = checkSameValue(conf -> conf.getChildren("int").length);
        assertThat(childrenCount).isEqualTo(1);
    }

    @Test
    public void testGetChildren_multiple() throws ConfigurationException {
        int childrenCount = checkSameValue(conf -> conf.getChildren("li").length);
        assertThat(childrenCount).isEqualTo(3);
    }

    @Test
    public void testGetAttributeNames_none() throws ConfigurationException {
        String[] attributeNames = checkSameValue(conf -> conf.getChild("empty").getAttributeNames());
        assertThat(attributeNames).isEmpty();
    }

    @Test
    public void testGetAttributeNames_multiple() throws ConfigurationException {
        // Mapped to Set because default configuration does not preserve order.
        Set<String> attributeNames = checkSameValue(conf -> Sets.newHashSet(conf.getChild("attrs").getAttributeNames()));
        assertThat(attributeNames).containsExactlyInAnyOrder("string", "boolean", "float", "int");
    }

    @Test
    public void testGetAttributeNames_ordering() throws ConfigurationException {
        // Default configuration does not preserve order, but dynamic configuration does.
        String[] attributeNames = dynamicConfiguration.getChild("attrs").getAttributeNames();
        assertThat(attributeNames).containsExactly("string", "boolean", "int", "float");
    }

    @Test
    public void testGetAttribute_noSuchAttr() throws ConfigurationException {
        String attributeValue = checkSameValue(conf -> conf.getAttribute("nosuchattr"));
        assertThat(attributeValue).isEqualTo(null);
    }

    @Test
    public void testGetAttribute_string() throws ConfigurationException {
        String attributeValue = checkSameValue(conf -> conf.getChild("attrs").getAttribute("string"));
        assertThat(attributeValue).isEqualTo("hiya");
    }

    @Test
    public void testGetAttribute_int() throws ConfigurationException {
        String attributeValue = checkSameValue(conf -> conf.getChild("attrs").getAttribute("int"));
        assertThat(attributeValue).isEqualTo("11");
    }

    @Test
    public void testGetAttributeAsBoolean_withDefault_nosuchattr() throws ConfigurationException {
        boolean attributeValue = checkSameValue(conf -> conf.getChild("attrs").getAttributeAsBoolean("nosuchattr", true));
        assertThat(attributeValue).isTrue();
    }

    @Test
    public void testGetAttributeAsBoolean_withDefault_empty() throws ConfigurationException {
        boolean attributeValue = checkSameValue(conf -> conf.getChild("empty").getAttributeAsBoolean("nosuchattr", true));
        assertThat(attributeValue).isTrue();
    }

    @Test
    public void testGetAttributeAsBoolean_withDefault_boolean() throws ConfigurationException {
        boolean attributeValue = checkSameValue(conf -> conf.getChild("attrs").getAttributeAsBoolean("boolean", false));
        assertThat(attributeValue).isTrue();
    }

    @Test
    public void testGetAttributeAsBoolean_withDefault_string() throws ConfigurationException {
        boolean attributeValue = checkSameValue(conf -> conf.getChild("attrs").getAttributeAsBoolean("string", true));
        assertThat(attributeValue).isFalse();
    }

    @Test
    public void testGetAttributeAsFloat_noSuchAttr() throws ConfigurationException {
        checkException(conf -> conf.getAttributeAsFloat("nosuchattr"));
    }

    @Test
    public void testGetAttributeAsFloat_float() throws ConfigurationException {
        float attributeValue = checkSameValue(conf -> conf.getChild("attrs").getAttributeAsFloat("float"));
        assertThat(attributeValue).isEqualTo(1.23f);
    }

    @Test
    public void testGetAttributeAsFloat_string() throws ConfigurationException {
        checkException(conf -> conf.getChild("attrs").getAttributeAsFloat("string"));
    }

    @Test
    public void testGetAttributeAsFloat_withDefault_string() throws ConfigurationException {
        boolean attributeValue = checkSameValue(conf -> conf.getChild("attrs").getAttributeAsBoolean("string", true));
        assertThat(attributeValue).isFalse();
    }

    // Ah, default gives horrible blob of text. Revisit later.
    //    @Test
    //    public void testGetValue_noValueSet() throws ConfigurationException {
    //        checkSameValue(conf -> conf.getValue());
    //    }

    @Test
    public void testGetValue_noSuchChild() throws ConfigurationException {
        checkException(conf -> conf.getChild("noSuchChild").getValue());
    }

    @Test
    public void testGetValue_empty() throws ConfigurationException {
        checkException(conf -> conf.getChild("empty").getValue());
    }

    @Test
    public void testGetValue_whitespace() throws ConfigurationException {
        checkSameValue(conf -> conf.getChild("space").getValue());
    }

    @Test
    public void testGetValue_string() throws ConfigurationException {
        checkSameValue(conf -> conf.getChild("string").getValue());
    }

    @Test
    public void testGetValue_int() throws ConfigurationException {
        checkSameValue(conf -> conf.getChild("int").getValue());
    }

    @Test
    public void testGetValue_withDefault_noSuchChild() throws ConfigurationException {
        String value = checkSameValue(conf -> conf.getChild("noSuchChild").getValue("default"));
        assertThat(value).isEqualTo("default");
    }

    @Test
    public void testGetValueAsBoolean_noSuchChild() throws ConfigurationException {
        checkSameValue(conf -> conf.getChild("noSuchChild").getValueAsBoolean());
    }

    @Test
    public void testGetValueAsBoolean_empty() throws ConfigurationException {
        checkSameValue(conf -> conf.getChild("empty").getValueAsBoolean());
    }

    @Test
    public void testGetValueAsBoolean_whitespace() throws ConfigurationException {
        checkSameValue(conf -> conf.getChild("space").getValueAsBoolean());
    }

    @Test
    public void testGetValueAsBoolean_string() throws ConfigurationException {
        checkSameValue(conf -> conf.getChild("string").getValueAsBoolean());
    }

    @Test
    public void testGetValueAsBoolean_boolean() throws ConfigurationException {
        boolean value = checkSameValue(conf -> conf.getChild("boolean").getValueAsBoolean());
        assertThat(value).isTrue();
    }

    @Test
    public void testGetValueAsBoolean_yes() throws ConfigurationException {
        boolean value = checkSameValue(conf -> conf.getChild("yes").getValueAsBoolean());
        // "yes" is true for attributes, but not values (FOP behaviour).
        assertThat(value).isFalse();
    }

    @Test
    public void testGetValueAsBoolean_int() throws ConfigurationException {
        checkSameValue(conf -> conf.getChild("int").getValueAsBoolean());
    }

    @Test
    public void testGetValueAsBoolean_withDefault_noSuchChild() throws ConfigurationException {
        boolean value = checkSameValue(conf -> conf.getChild("noSuchChild").getValueAsBoolean(true));
        assertThat(value).isTrue();
    }

    @Test
    public void testGetValueAsInteger_noSuchChild() throws ConfigurationException {
        int value = checkSameValue(conf -> conf.getChild("noSuchChild").getValueAsInteger());
        // FOP's NullConfiguration returns 0.
        assertThat(value).isEqualTo(0);
    }

    @Test
    public void testGetValueAsInteger_int() throws ConfigurationException {
        int value = checkSameValue(conf -> conf.getChild("int").getValueAsInteger());
        assertThat(value).isEqualTo(5);
    }

    @Test
    public void testGetValueAsInteger_float() throws ConfigurationException {
        // The float does not get rounded
        checkException(conf -> conf.getChild("float").getValueAsInteger());
    }

    @Test
    public void testGetValueAsInteger_withDefault_noSuchChild() throws ConfigurationException {
        int value = checkSameValue(conf -> conf.getChild("noSuchChild").getValueAsInteger(7));
        assertThat(value).isEqualTo(7);
    }

    @Test
    public void testGetValueAsFloat_noSuchChild() throws ConfigurationException {
        float value = checkSameValue(conf -> conf.getChild("noSuchChild").getValueAsFloat());
        // FOP's NullConfiguration returns 0f.
        assertThat(value).isEqualTo(0f);
    }

    @Test
    public void testGetValueAsFloat_int() throws ConfigurationException {
        float value = checkSameValue(conf -> conf.getChild("int").getValueAsFloat());
        assertThat(value).isEqualTo(5f);
    }

    @Test
    public void testGetValueAsFloat_float() throws ConfigurationException {
        float value = checkSameValue(conf -> conf.getChild("float").getValueAsFloat());
        assertThat(value).isEqualTo(1.61803398875f);
    }

    @Test
    public void testGetValueAsFloat_withDefault_noSuchChild() throws ConfigurationException {
        float value = checkSameValue(conf -> conf.getChild("noSuchChild").getValueAsFloat(1.412f));
        assertThat(value).isEqualTo(1.412f);
    }

    @Test
    public void testToString_attributesAndChildren() {
        DynamicConfiguration conf = new DynamicConfiguration("/conf");
        conf.setAttribute("attrOne", 1);
        conf.setAttribute("attrTwo", 2);
        conf.addChild("childOne");
        conf.addChild("childTwo");
        conf.addChild("childThree");
        String toString = conf.toString();
        assertThat(toString).isEqualTo("DynamicConfiguration{xpath=/conf, attributes.count=2, children.count=3}");
    }

    @Test
    public void testToString_indexAddedWithSecondElement() {
        DynamicConfiguration conf = new DynamicConfiguration("/conf");
        DynamicConfiguration li1 = conf.addChild("li");
        assertThat(li1.toString()).isEqualTo("DynamicConfiguration{xpath=/conf/li}");
        DynamicConfiguration li2 = conf.addChild("li");
        assertThat(li1.toString()).isEqualTo("DynamicConfiguration{xpath=/conf/li[1]}");
        assertThat(li2.toString()).isEqualTo("DynamicConfiguration{xpath=/conf/li[2]}");
    }

    @Test
    public void testToString_indexAddedWithGetChildren() {
        DynamicConfiguration conf = new DynamicConfiguration("/conf");
        DynamicConfiguration li1 = conf.addChild("li");
        assertThat(li1.toString()).isEqualTo("DynamicConfiguration{xpath=/conf/li}");
        conf.getChildren("li");
        assertThat(li1.toString()).isEqualTo("DynamicConfiguration{xpath=/conf/li[1]}");
        DynamicConfiguration li2 = conf.addChild("li");
        assertThat(li1.toString()).isEqualTo("DynamicConfiguration{xpath=/conf/li[1]}");
        assertThat(li2.toString()).isEqualTo("DynamicConfiguration{xpath=/conf/li[2]}");
    }

    @Test
    public void testToString_noAttributesAndNoChildren() {
        DynamicConfiguration conf = new DynamicConfiguration("/conf");
        String toString = conf.toString();
        assertThat(toString).isEqualTo("DynamicConfiguration{xpath=/conf}");
    }

    @Test
    public void testToString_attributesAndNoChildren() {
        DynamicConfiguration conf = new DynamicConfiguration("/conf");
        conf.setAttribute("attrOne", 1);
        conf.setAttribute("attrTwo", 2);
        conf.setAttribute("attrThree", 3);
        String toString = conf.toString();
        assertThat(toString).isEqualTo("DynamicConfiguration{xpath=/conf, attributes.count=3}");
    }

    private <R> R checkSameValue(ConfigurationFunction<R> getValueFunction) throws ConfigurationException {
        R xmlConfigurationValue;
        try {
            xmlConfigurationValue = getValueFunction.apply(xmlConfiguration);
        }
        catch (ConfigurationException e) {
            throw new AssertionError("Default configuration throws an exception, the test should use checkException()", e);
        }
        R dynamicConfigurationValue = getValueFunction.apply(dynamicConfiguration);

        assertThat(dynamicConfigurationValue)
                .as("Dynamic configuration value does not match the default configuration value")
                .isEqualTo(xmlConfigurationValue);

        return dynamicConfigurationValue;
    }

    private <R> void checkException(ConfigurationFunction<R> getValueFunction) {
        // Usually a ConfigurationException, but not always.
        Assertions.assertThatThrownBy(() -> getValueFunction.apply(xmlConfiguration),
            "Default configuration does not throw an exception, the test should use checkSameValue()");
        Assertions.assertThatThrownBy(() -> getValueFunction.apply(dynamicConfiguration),
            "Default configuration throws an exception, by dyanmic configuration does not");
        //    R dynamicConfigurationValue = getValueFunction.apply(dynamicConfiguration);

        // assertThat(dynamicConfigurationValue).isEqualTo(xmlConfigurationValue);
    }

    @FunctionalInterface
    private interface ConfigurationFunction<R> {
        R apply(Configuration conf) throws ConfigurationException;
    }

}
