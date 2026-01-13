package com.jipple.util;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class JsonUtils {
    
    protected static final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    /**
     * Converts a JSON generator block to a JSON string.
     * 
     * @param block A consumer that writes JSON using the provided JsonGenerator
     * @return The JSON string representation
     * @throws RuntimeException if an IOException occurs during JSON generation
     */
    public static String toJsonString(Consumer<JsonGenerator> block) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            JsonGenerator generator = mapper.createGenerator(baos, JsonEncoding.UTF8);
            block.accept(generator);
            generator.close();
            baos.close();
            return new String(baos.toByteArray(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate JSON string", e);
        }
    }
}

