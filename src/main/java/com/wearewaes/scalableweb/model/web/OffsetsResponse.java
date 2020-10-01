package com.wearewaes.scalableweb.model.web;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OffsetsResponse {
    private Message message;
    private List<Offset> offsets;

    public enum Message {
        EQUAL("Left and right sides are equal"),
        NOT_EQUAL_LENGTH("Left and right sides are not of equal length"),
        EQUAL_LENGTH_BUT_DIFFERENT_PAYLOAD("Left and right sides of equal length but payload is different");

        private final String value;

        Message(String value) {
            this.value = value;
        }

        /**
         * Required for deserialization.
         */
        @JsonValue
        public String getValue() {
            return value;
        }
    }
}
