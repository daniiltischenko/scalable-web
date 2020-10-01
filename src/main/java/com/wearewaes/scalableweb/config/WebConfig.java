package com.wearewaes.scalableweb.config;

import com.wearewaes.scalableweb.model.web.Side;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * In Java, it's considered good practice to define enum values with uppercase letters, as they are constants.
 * However, we want to support lowercase letters in the request URL for {@link Side} enum.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToEnumConverter());
    }

    /**
     * Custom converter for {@link Side} enum.
     */
    private static class StringToEnumConverter implements Converter<String, Side> {
        @Override
        public Side convert(String source) {
            return Side.valueOf(source.toUpperCase());
        }
    }
}