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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fop.configuration.Configuration;
import org.apache.fop.configuration.ConfigurationException;

public class DynamicConfiguration implements Configuration {

    private static final Log LOGGER = LogFactory.getLog(DynamicConfiguration.class);

    // Workaround for NullConfiguration not being public.
    // TODO! revisit once DynamicConfiguration is more complete.
    //private static final Configuration NULL_CONFIGURATION = new DynamicConfiguration("NULL");

    // LinkedHashMap to preserve ordering. Preserved only to help with debugging.
    private final Map<String, List<DynamicConfiguration>> children = new LinkedHashMap<>();

    // Used for debugging.
    // Not final, this can be modified to append an array index.
    private String xpath;
    private Map<String, String> attributes = new LinkedHashMap<>();
    private String value;

    public DynamicConfiguration(String xpath) {
        this.xpath = xpath;
    }

    private Configuration getChild0(String key) {
        // return children.get(key);
        if (!children.containsKey(key)) {
            return null;
        }
        else {
            // Returning the first is consistent with DefaultConfiguration.
            return children.get(key).get(0);
        }
    }

    @Override
    public Configuration getChild(String key) {
        Configuration child = getChild0(key);
        if (child == null) {
            child = new NullConfiguration(xpath + "/" + key);
        }
        if (LOGGER.isDebugEnabled()) {
            // debug("getChild(\"{}\"): {} -> {}", key, xpath + "/" + key, child);
            String msg = new StringBuilder()
                    .append("getChild(\"")
                    .append(key)
                    .append("\"): ")
                    .append(xpath)
                    .append('/')
                    .append(key)
                    .append(" -> ")
                    .append(child)
                    .toString();
            LOGGER.debug(msg);
        }
        return child;
    }

    @Override
    public Configuration getChild(String key, boolean required) {
        Configuration child = getChild0(key);
        if (child == null && required) {
            child = new NullConfiguration(xpath + "/" + key);
        }
        if (LOGGER.isDebugEnabled()) {
            // debug("getChild(\"{}\", {}): {} -> {}", key, required, xpath + "/" + key, child);
            String msg = new StringBuilder()
                    .append("getChild(\"")
                    .append(key)
                    .append(", ")
                    .append(required)
                    .append("\"): ")
                    .append(xpath)
                    .append('/')
                    .append(key)
                    .append(" -> ")
                    .append(child)
                    .toString();
            LOGGER.debug(msg);
        }
        return child;
    }

    @Override
    public Configuration[] getChildren(String key) {
        List<DynamicConfiguration> childrenList = children.get(key);
        Configuration[] children;
        if (childrenList == null) {
            children = new Configuration[0];
        }
        else {
            if (childrenList.size() == 1) {
                // if size > 1 then addChild() has already called ensureIndexed().
                ensureIndexed(childrenList.get(0));
            }
            children = childrenList.toArray(new Configuration[0]);
        }
        if (LOGGER.isDebugEnabled()) {
            // debug("getChildren(\"{}\"): {} -> [{}]", key, xpath + "/" + key, children.length);
            String msg = new StringBuilder()
                    .append("getChildren(\"")
                    .append(key)
                    .append("\"): ")
                    .append(xpath)
                    .append('/')
                    .append(key)
                    .append(" -> [")
                    .append(children.length)
                    .append(']')
                    .toString();
            LOGGER.debug(msg);
        }
        return children;
    }

    @Override
    public String[] getAttributeNames() {
        return attributes.keySet().stream().toArray(String[]::new);
    }

    private <T> T getAttribute0(String key, Class<T> type) {
        return type.cast(attributes.get(key));
    }

    private <T> T getAttribute(String methodName, String key, Class<T> type) {
        T attribute = getAttribute0(key, type);
        if (LOGGER.isDebugEnabled()) {
            // debug("{}(\"{}\"): {} -> {}", methodName, key, xpath + "/@" + key, attribute);
            String msg = new StringBuilder()
                    .append(methodName)
                    .append("(\"")
                    .append(key)
                    .append("\"): ")
                    .append(xpath)
                    .append("/@")
                    .append(key)
                    .append(" -> ")
                    .append(attribute)
                    .toString();
            LOGGER.debug(msg);
        }
        return attribute;
    }

    private <T> T getAttribute(String methodName, String key, T defaultValue, Class<T> type) {
        T attribute = getAttribute0(key, type);
        if (attribute == null) {
            attribute = defaultValue;
        }
        if (LOGGER.isDebugEnabled()) {
            // debug("{}(\"{}\", {}): {} -> {}", methodName, key, defaultValue, xpath + "/@" + key, attribute);
            String msg = new StringBuilder()
                    .append(methodName)
                    .append("(\"")
                    .append(key)
                    .append(", ")
                    .append(defaultValue)
                    .append("\"): ")
                    .append(xpath)
                    .append("/@")
                    .append(key)
                    .append(" -> ")
                    .append(attribute)
                    .toString();
            LOGGER.debug(msg);
        }
        return attribute;
    }

    @Override
    public String getAttribute(String key) throws ConfigurationException {
        return getAttribute("getAttribute", key, String.class);
    }

    @Override
    public String getAttribute(String key, String defaultValue) {
        return getAttribute("getAttribute", key, defaultValue, String.class);
    }

    //    String result = getAttribute(key);
    //    if (result == null || "".equals(result)) {
    //        return defaultValue;
    //    }
    // return"true".equalsIgnoreCase(result)||"yes".equalsIgnoreCase(result);
    @Override
    public boolean getAttributeAsBoolean(String key, boolean defaultValue) {
        boolean attributeBooleanValue = defaultValue;
        String attributeStringValue = attributes.get(key);
        if (attributeStringValue != null && !"".equals(attributeStringValue)) {
            attributeBooleanValue = "true".equalsIgnoreCase(attributeStringValue) || "yes".equalsIgnoreCase(attributeStringValue);
        }
        return attributeBooleanValue;
    }

    @Override
    public float getAttributeAsFloat(String key) {
        float floatValue = Float.parseFloat(attributes.get(key));
        return floatValue;
    }

    @Override
    public float getAttributeAsFloat(String key, float defaultValue) {
        return getAttribute("getAttributeAsFloat", key, defaultValue, Float.class);
    }

    @Override
    public int getAttributeAsInteger(String key, int defaultValue) {
        return getAttribute("getAttributeAsInteger", key, defaultValue, Integer.class);
    }

    private <T> T getValue0(Class<T> type) {
        return type.cast(value);
    }

    private <T> T getValue(String methodName, T defaultValue, Class<T> type) {
        T value = getValue0(type);
        if (value == null) {
            value = defaultValue;
        }
        if (LOGGER.isDebugEnabled()) {
            //  debug("{}({}): {} -> {}", methodName, defaultValue, xpath, value);
            String msg = new StringBuilder()
                    .append(methodName)
                    .append("(")
                    .append(defaultValue)
                    .append("): ")
                    .append(xpath)
                    .append(" -> ")
                    .append(value)
                    .toString();
            LOGGER.debug(msg);
        }
        return value;
    }

    @Override
    public String getValue() throws ConfigurationException {
        //return getValue("getValue", String.class);
        if (value == null || "".equals(value)) {
            throw new ConfigurationException("No value in " + xpath);
        }
        return value;
    }

    @Override
    public String getValue(String defaultValue) {
        return getValue("getValue", defaultValue, String.class);
    }

    @Override
    public boolean getValueAsBoolean() {
        Boolean booleanValue = "true".equals(value);
        if (LOGGER.isDebugEnabled()) {
            // debug("getValueAsBoolean(): {} -> {}", xpath, booleanValue);
            String msg = new StringBuilder()
                    .append("getValueAsBoolean(): ")
                    .append(xpath)
                    .append(" -> ")
                    .append(booleanValue)
                    .toString();
            LOGGER.debug(msg);
        }

        return booleanValue;
    }

    @Override
    public boolean getValueAsBoolean(boolean defaultValue) {
        return getValue("getValueAsBoolean", defaultValue, Boolean.class);
    }

    @Override
    public int getValueAsInteger() {
        int intValue = Integer.parseInt(value);
        return intValue;
    }

    @Override
    public int getValueAsInteger(int defaultValue) {
        return getValue("getValueAsInteger", defaultValue, Integer.class);
    }

    @Override
    public float getValueAsFloat() {
        float floatValue = Float.parseFloat(value);
        return floatValue;
    }

    @Override
    public float getValueAsFloat(float defaultValue) {
        return getValue("getValueAsFloat", defaultValue, Float.class);
    }

    @Override
    public String getLocation() {
        throw new UnsupportedOperationException();
    }

    public void setAttribute(String key, String value) {
        setAttribute0(key, value, true);
    }

    public void setAttribute(String key, boolean value) {
        setAttribute0(key, Boolean.toString(value), false);
    }

    public void setAttribute(String key, int value) {
        setAttribute0(key, Integer.toString(value), false);
    }

    public void setAttribute(String key, float value) {
        setAttribute0(key, Float.toString(value), false);
    }

    public void setAttribute0(String key, String value, boolean logQuotes) {
        attributes.put(key, value);
    }

    public void setValue(String value) {
        setValue0(value, true);
    }

    public void setValue(boolean value) {
        setValue0(Boolean.toString(value), false);
    }

    public void setValue(int value) {
        setValue0(Integer.toString(value), false);
    }

    public void setValue(float value) {
        setValue0(Float.toString(value), false);
    }

    public void setValue0(String value, boolean logQuotes) {
        this.value = value;
    }

    public DynamicConfiguration addChild(String key) {
        String childKey = xpath + "/" + key;
        List<DynamicConfiguration> childrenList = children.get(key);
        if (childrenList == null) {
            childrenList = new ArrayList<>();
            children.put(key, childrenList);
        }
        else {
            if (childrenList.size() == 1) {
                // if size > 1 then addChild() has already called ensureIndexed().
                ensureIndexed(childrenList.get(0));
            }
            childKey = childKey + "[" + (childrenList.size() + 1) + "]";
        }
        DynamicConfiguration child = new DynamicConfiguration(childKey);
        childrenList.add(child);

        return child;
    }

    private void ensureIndexed(DynamicConfiguration configuration) {
        if (!configuration.xpath.endsWith("]")) {
            configuration.xpath += "[1]";
        }
    }

    @Override
    public String toString() {
        ToStringHelper helper = MoreObjects.toStringHelper(this)
                .add("xpath", xpath);

        if (!attributes.isEmpty()) {
            helper.add("attributes.count", attributes.size());
        }

        if (!children.isEmpty()) {
            helper.add("children.count", children.size());
        }

        return helper.toString();
    }

}
