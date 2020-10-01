package com.wearewaes.scalableweb.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wearewaes.scalableweb.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Utility class, JSON schema validator.
 * No need to create Spring Bean.
 */
@Slf4j
public final class JsonSchemaValidator {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private JsonSchemaValidator() {
        throw new AssertionError("Should not be instantiated");
    }

    /**
     * Checks if provided string is a correct JSON.
     *
     * @param json to validate.
     */
    public static void validateJson(String json) {
        try {
            log.debug("Validating decoded JSON: [{}]", json);
            objectMapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Encoded payload contains invalid JSON");
        }
    }
}
