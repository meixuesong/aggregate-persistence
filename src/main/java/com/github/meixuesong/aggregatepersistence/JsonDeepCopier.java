/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */

package com.github.meixuesong.aggregatepersistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Use Json to clone(deep copy) object.
 *
 * @author meixuesong
 */
public class JsonDeepCopier implements DeepCopier {

    private ObjectMapper mapper;

    @Override
    public <T> T copy(T object) {
        try {
            String json = getMapper().writeValueAsString(object);

            return getMapper().readValue(json, (Class<T>) (object.getClass()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized ObjectMapper getMapper() {
        if (mapper == null) {
            createObjectMapper();
        }

        return mapper;
    }

    private void createObjectMapper() {
        if (this.mapper == null) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            this.mapper = mapper;
        }
    }
}
