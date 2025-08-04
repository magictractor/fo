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

import com.google.common.base.MoreObjects;

import org.apache.fop.configuration.Configuration;
import org.apache.fop.configuration.ConfigurationException;

// Mostly a copy of Fop's NullConfiguration, but xpath is used, so getLocation() and toString() differ.
public final class NullConfiguration implements Configuration {

    private final String xpath;

    public NullConfiguration(String xpath) {
        this.xpath = xpath;
    }

    @Override
    public Configuration getChild(String key) {
        return new NullConfiguration(xpath + "/" + key);
    }

    @Override
    public Configuration getChild(String key, boolean required) {
        return new NullConfiguration(xpath + "/" + key);
    }

    @Override
    public Configuration[] getChildren(String key) {
        return new Configuration[0];
    }

    @Override
    public String[] getAttributeNames() {
        return new String[0];
    }

    @Override
    public String getAttribute(String key) throws ConfigurationException {
        return "";
    }

    @Override
    public String getAttribute(String key, String defaultValue) {
        return defaultValue;
    }

    @Override
    public boolean getAttributeAsBoolean(String key, boolean defaultValue) {
        return defaultValue;
    }

    @Override
    public float getAttributeAsFloat(String key) throws ConfigurationException {
        return 0;
    }

    @Override
    public float getAttributeAsFloat(String key, float defaultValue) {
        return defaultValue;
    }

    @Override
    public int getAttributeAsInteger(String key, int defaultValue) {
        return defaultValue;
    }

    @Override
    public String getValue() throws ConfigurationException {
        throw new ConfigurationException("missing value");
    }

    @Override
    public String getValue(String defaultValue) {
        return defaultValue;
    }

    @Override
    public boolean getValueAsBoolean() throws ConfigurationException {
        return false;
    }

    @Override
    public boolean getValueAsBoolean(boolean defaultValue) {
        return defaultValue;
    }

    @Override
    public int getValueAsInteger() throws ConfigurationException {
        return 0;
    }

    @Override
    public int getValueAsInteger(int defaultValue) {
        return defaultValue;
    }

    @Override
    public float getValueAsFloat() throws ConfigurationException {
        return 0;
    }

    @Override
    public float getValueAsFloat(float defaultValue) {
        return defaultValue;
    }

    @Override
    public String getLocation() {
        return xpath;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("xpath", xpath)
                .toString();
    }

}
