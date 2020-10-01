package com.wearewaes.scalableweb.controller;

import com.wearewaes.scalableweb.model.web.EncodedPayloadRequest;
import com.wearewaes.scalableweb.model.web.OffsetsResponse;
import com.wearewaes.scalableweb.model.web.Side;
import com.wearewaes.scalableweb.service.DataRecordServiceImpl;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * REST controller that handles requests for saving left and right side of decoded data
 * and calculating offsets.
 */
@Slf4j
@Api(value = "The Controller is aimed to calculate offsets for Base64 encoded Strings")
@RestController
@RequestMapping(value = "v1/diff", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class DataOffsetController {

    private final DataRecordServiceImpl service;

    public DataOffsetController(DataRecordServiceImpl service) {
        this.service = service;
    }

    /**
     * Endpoint for saving encoded binary data.
     *
     * @param id      data pair (aka record) ID.
     * @param side    may be left or right.
     * @param request contains payload with encoded binary data.
     */
    @ApiOperation("Endpoint for saving encoded binary data")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Created", response = Void.class),
            @ApiResponse(code = 400, message = "Bad Request", response = String.class)
    })
    @PostMapping(value = "{id}/{side}")
    @ResponseStatus(HttpStatus.CREATED)
    public void save(
            @ApiParam("Record ID") @PathVariable long id,
            @ApiParam("Record side (left/right)") @PathVariable Side side,
            @ApiParam("JSON payload") @Valid @RequestBody EncodedPayloadRequest request) {
        log.info("Received data for id: [{}], for side: [{}] and payload: [{}]", id, side, request);

        service.save(id, side, request.getPayload());
    }

    /**
     * Endpoint that compares saved encoded binary data.
     *
     * @param id data pair (aka record) ID.
     * @return {@link OffsetsResponse} with statis message and actual diffs if available.
     */
    @ApiOperation("Endpoint for comparing saved encoded binary data")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = OffsetsResponse.class),
            @ApiResponse(code = 400, message = "Bad Request", response = String.class),
            @ApiResponse(code = 404, message = "Bad Request", response = String.class)
    })
    @GetMapping("{id}")
    public OffsetsResponse calculateDifference(@ApiParam("Record ID") @NotNull @PathVariable long id) {
        log.info("Received request to compare payloads for ID: [{}]", id);

        return service.getOffsetsByRecordId(id);
    }
}
