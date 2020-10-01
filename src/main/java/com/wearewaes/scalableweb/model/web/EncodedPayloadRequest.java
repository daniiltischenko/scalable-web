package com.wearewaes.scalableweb.model.web;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class EncodedPayloadRequest {
    @NotEmpty(message = "Please provide encoded binary data for payload attribute")
    private String payload;
}
