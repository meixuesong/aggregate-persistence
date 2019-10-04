package com.github.meixuesong.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class DeepCopierImpl implements DeepCopier {

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

    private ObjectMapper getMapper() {
        if (mapper == null) {
            createObjectMapper();
        }

        return mapper;
    }

    private synchronized void createObjectMapper() {
        if (this.mapper == null) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            this.mapper = mapper;
        }
    }
}
