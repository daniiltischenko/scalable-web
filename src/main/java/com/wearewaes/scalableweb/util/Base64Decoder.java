package com.wearewaes.scalableweb.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Base64;

/**
 * Utility class for decoding Base64 strings.
 * No need to create Spring Bean.
 */
@Slf4j
public final class Base64Decoder {

    private Base64Decoder() {
        throw new AssertionError("Should not be instantiated");
    }

    public static String decode(String payload) {
        log.debug("Decoding JSON from Base64 string: [{}]", payload);
        Base64.Decoder decoder = Base64.getDecoder();
        return new String(decoder.decode(payload));
    }
}
