package com.wearewaes.scalableweb;

import com.wearewaes.scalableweb.exception.BadRequestException;
import com.wearewaes.scalableweb.util.JsonSchemaValidator;
import org.junit.Test;

public class JsonSchemaValidatorTest {

    @Test
    public void validJson() {
        JsonSchemaValidator.validateJson("{\"name\":\"qwerty\"}");
    }

    @Test
    public void emptyJson() {
        JsonSchemaValidator.validateJson("{}");
    }

    @Test(expected = BadRequestException.class)
    public void invalidJson() {
        JsonSchemaValidator.validateJson("{some_data}");
    }

    @Test(expected = BadRequestException.class)
    public void invalidJsonNoBrackets() {
        JsonSchemaValidator.validateJson("\"name\":\"qwerty\"");
    }
}
