package com.wearewaes.scalableweb.controller;

import com.wearewaes.scalableweb.model.web.EncodedPayloadRequest;
import com.wearewaes.scalableweb.model.web.Offset;
import com.wearewaes.scalableweb.model.web.OffsetsResponse;
import com.wearewaes.scalableweb.model.entity.DataRecord;
import com.wearewaes.scalableweb.persistence.DataRecordRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

/**
 * Provides integration test from API call to DB connection.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DataOffsetControllerTest {

    //Base64 for {"name":"qwerty"}
    private static final String NAME_QWERTY = "eyJuYW1lIjoicXdlcnR5In0=";
    //Base64 for {"name":"qwerdo"}
    private static final String NAME_QWERDO = "eyJuYW1lIjoicXdlcmRvIn0=";
    //Base64 for {"nmae":"qwerdo"}
    private static final String NMAE_QWERDO = "eyJubWFlIjoicXdlcmRvIn0=";
    //Base64 for {"age":"17"}
    private static final String AGE_17 = "eyJhZ2UiOiIxNyJ9";
    //Base64 for invalid_json
    private static final String INVALID_JSON = "aW52YWxpZF9qc29u";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DataRecordRepository repository;

    @Test
    public void leftSideSuccess() {
        // given
        // make sure DB is empty
        assertTableIsEmpty();

        ResponseEntity response = postLeft(NAME_QWERTY);

        //when

        Optional<DataRecord> dataRecord = repository.findById(1L);
        //then
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(dataRecord.isPresent());

        DataRecord expected = new DataRecord();
        expected.setId(1L);
        expected.setEncodedBase64StringLeft(NAME_QWERTY);
        Assertions.assertThat(dataRecord.get().equals(expected));

        //clear the DB
        clearDataBase();
    }

    @Test
    public void rightSideSuccess() {
        // given
        // make sure DB is empty
        assertTableIsEmpty();

        //when
        ResponseEntity<ResponseEntity> response = postRight(NAME_QWERDO);

        Optional<DataRecord> dataRecord = repository.findById(1L);

        //then
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(dataRecord.isPresent());

        DataRecord expected = new DataRecord();
        expected.setId(1L);
        expected.setEncodedBase64StringRight(NAME_QWERDO);
        Assertions.assertThat(dataRecord.get().equals(expected));

        //clear the DB
        clearDataBase();
    }

    @Test
    public void leftAndRightSuccess() {
        // given
        // make sure DB is empty
        assertTableIsEmpty();

        //when
        ResponseEntity<ResponseEntity> responseLeft = postLeft(NAME_QWERTY);

        ResponseEntity<ResponseEntity> responseRight = postRight(NAME_QWERDO);

        Optional<DataRecord> dataRecord = repository.findById(1L);

        //then
        Assertions.assertThat(responseLeft.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(responseRight.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(dataRecord.isPresent());

        DataRecord expected = new DataRecord();
        expected.setId(1L);
        expected.setEncodedBase64StringLeft(NAME_QWERTY);
        expected.setEncodedBase64StringRight(NAME_QWERDO);
        Assertions.assertThat(dataRecord.get().equals(expected));

        //clear the DB
        clearDataBase();
    }

    @Test
    public void equalPayloads() {
        // given
        // make sure DB is empty
        assertTableIsEmpty();

        postLeft(NAME_QWERTY);
        postRight(NAME_QWERTY);

        //when
        ResponseEntity<OffsetsResponse> response = restTemplate.getForEntity("/v1/diff/1", OffsetsResponse.class);

        //then
        Assertions.assertThat(response.getStatusCode().equals(HttpStatus.OK));
        Assertions.assertThat(response.getBody().getMessage().equals(OffsetsResponse.Message.EQUAL));

        clearDataBase();
    }

    @Test
    public void differentPayloadLength() {
        // given
        // make sure DB is empty
        assertTableIsEmpty();

        postLeft(NAME_QWERTY);
        postRight(AGE_17);

        //when
        ResponseEntity<OffsetsResponse> response = restTemplate.getForEntity("/v1/diff/1", OffsetsResponse.class);

        //then
        Assertions.assertThat(response.getStatusCode().equals(HttpStatus.OK));
        Assertions.assertThat(response.getBody().getMessage().equals(OffsetsResponse.Message.NOT_EQUAL_LENGTH));

        clearDataBase();
    }

    @Test
    public void successOneOffset() {
        // given
        // make sure DB is empty
        assertTableIsEmpty();

        postLeft(NAME_QWERTY);
        postRight(NAME_QWERDO);

        //when
        ResponseEntity<OffsetsResponse> response = restTemplate.getForEntity("/v1/diff/1", OffsetsResponse.class);

        //then
        Assertions.assertThat(response.getStatusCode().equals(HttpStatus.OK));

        Assertions.assertThat(response.getBody().getMessage().equals(OffsetsResponse.Message.EQUAL_LENGTH_BUT_DIFFERENT_PAYLOAD));
        Assertions.assertThat(response.getBody().getOffsets().size() == 1);

        Offset expectedOffset = Offset.builder().offset(16).length(2).build();
        Assertions.assertThat(response.getBody().getOffsets().get(0).equals(expectedOffset));

        clearDataBase();
    }

    @Test
    public void successTwoOffsets() {
        // given
        // make sure DB is empty
        assertTableIsEmpty();

        postLeft(NAME_QWERTY);
        postRight(NMAE_QWERDO);

        //when
        ResponseEntity<OffsetsResponse> response = restTemplate.getForEntity("/v1/diff/1", OffsetsResponse.class);

        //then
        Assertions.assertThat(response.getStatusCode().equals(HttpStatus.OK));

        Assertions.assertThat(response.getBody().getMessage().equals(OffsetsResponse.Message.EQUAL_LENGTH_BUT_DIFFERENT_PAYLOAD));
        Assertions.assertThat(response.getBody().getOffsets().size() == 2);

        Offset expectedOffset_01 = Offset.builder().offset(5).length(2).build();
        Assertions.assertThat(response.getBody().getOffsets().get(0).equals(expectedOffset_01));

        Offset expectedOffset_02 = Offset.builder().offset(16).length(2).build();
        Assertions.assertThat(response.getBody().getOffsets().get(1).equals(expectedOffset_02));

        clearDataBase();
    }

    @Test
    public void onlyLeftOffsetFail() {
        // given
        // make sure DB is empty
        assertTableIsEmpty();

        postLeft(NAME_QWERTY);

        //when
        ResponseEntity response = restTemplate.getForEntity("/v1/diff/1", Object.class);

        //then
        Assertions.assertThat(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));

        clearDataBase();
    }

    @Test
    public void onlyRightOffsetFail() {
        // given
        // make sure DB is empty
        assertTableIsEmpty();

        postRight(NAME_QWERTY);

        //when
        ResponseEntity response = restTemplate.getForEntity("/v1/diff/1", Object.class);

        //then
        Assertions.assertThat(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));

        clearDataBase();
    }

    @Test
    public void noSidesInDBFail() {
        // given
        // make sure DB is empty
        assertTableIsEmpty();

        //when
        ResponseEntity response = restTemplate.getForEntity("/v1/diff/1", Object.class);

        //then
        Assertions.assertThat(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
        assertTableIsEmpty();
    }

    @Test
    public void leftInvalidJson() {
        // given
        // make sure DB is empty
        assertTableIsEmpty();

        //when
        ResponseEntity response = postLeft(INVALID_JSON, Object.class);

        //then
        Assertions.assertThat(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
        assertTableIsEmpty();
    }

    @Test
    public void rightInvalidJson() {
        // given
        // make sure DB is empty
        assertTableIsEmpty();

        //when
        ResponseEntity response = postRight(INVALID_JSON, Object.class);

        //then
        Assertions.assertThat(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
        assertTableIsEmpty();
    }

    private ResponseEntity postLeft(String payload) {
        return postLeft(payload, ResponseEntity.class);
    }

    private <T> ResponseEntity<T> postLeft(String payload, Class<T> clazz) {
        EncodedPayloadRequest requestLeft = new EncodedPayloadRequest();
        requestLeft.setPayload(payload);
        return restTemplate.postForEntity("/v1/diff/1/left", requestLeft, clazz);
    }

    private ResponseEntity postRight(String payload) {
        return postRight(payload, ResponseEntity.class);
    }

    private <T> ResponseEntity<T> postRight(String payload, Class<T> clazz) {
        EncodedPayloadRequest requestRight = new EncodedPayloadRequest();
        requestRight.setPayload(payload);
        return restTemplate.postForEntity("/v1/diff/1/right", requestRight, clazz);
    }

    private void assertTableIsEmpty() {
        Assertions.assertThat(!repository.findAll().iterator().hasNext());
    }

    private void clearDataBase() {
        repository.deleteById(1L);
    }
}
