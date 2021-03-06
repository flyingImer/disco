/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License").
 *   You may not use this file except in compliance with the License.
 *   A copy of the License is located at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   or in the "license" file accompanying this file. This file is distributed
 *   on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *   express or implied. See the License for the specific language governing
 *   permissions and limitations under the License.
 */

package software.amazon.disco.agent.event;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for protocol events; These events here typically occur around service events and
 * provide the mechanisms that make them work.
 */
public abstract class AbstractProtocolEvent extends AbstractEvent implements ProtocolEvent {

    public AbstractProtocolEvent(String origin) {
        super(origin);
        withData(DataKey.HEADER_MAP.name(), new HashMap<String, String>());
    }

    /**
     * Data keys
     */
    enum DataKey {
        /**
         * The map used to store header data.
         */
        HEADER_MAP
    }

    /**
     * Get the internal header map.
     *
     * @return the internal header map.
     */
    protected Map<String, String> getHeaderMap() {
        @SuppressWarnings("unchecked")
        Map<String, String> headerMap = (Map<String, String>) getData(DataKey.HEADER_MAP.name());
        return headerMap;
    }

    /**
     * Populate the contents of the inputMap into the header map.
     *
     * @param inputMap the input map to add onto the header map.
     * @return the 'this' for method chaining
     */
    public AbstractProtocolEvent withHeaderMap(Map<String, String> inputMap) {
        getHeaderMap().putAll(inputMap);
        return this;
    }

    /**
     * Stores the key-value pair into the header map.
     *
     * @param key   the key used to associate a header mapping.
     * @param value The header value associated with the key in the header.
     * @return "This" for method chaining.
     */
    public AbstractProtocolEvent withHeaderData(String key, String value) {
        if (data == null) {
            // When it's null, we don't populate the map.
            // Retrieval will return null, so the listener will know it's unable to retrieve the data.
            return this;
        }
        Map<String, String> headerMap = getHeaderMap();
        headerMap.put(key, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHeaderData(String key) {
        Map<String, String> headerMap = getHeaderMap();
        return headerMap.get(key);
    }

}
