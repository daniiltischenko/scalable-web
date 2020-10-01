package com.wearewaes.scalableweb.service;

import com.wearewaes.scalableweb.exception.BadRequestException;
import com.wearewaes.scalableweb.exception.NotFoundException;
import com.wearewaes.scalableweb.model.entity.DataRecord;
import com.wearewaes.scalableweb.model.web.Offset;
import com.wearewaes.scalableweb.model.web.OffsetsResponse;
import com.wearewaes.scalableweb.model.web.Side;
import com.wearewaes.scalableweb.persistence.DataRecordRepository;
import com.wearewaes.scalableweb.util.Base64Decoder;
import com.wearewaes.scalableweb.util.JsonSchemaValidator;
import com.wearewaes.scalableweb.util.OffsetCalculator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.wearewaes.scalableweb.model.web.OffsetsResponse.Message.*;

@Slf4j
@Service
public class DataRecordServiceImpl implements DataRecordService {

    private final DataRecordRepository repository;

    public DataRecordServiceImpl(DataRecordRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(long id, Side side, String encodedBase64String) {
        JsonSchemaValidator.validateJson(Base64Decoder.decode(encodedBase64String));

        DataRecord dataRecord = repository.findById(id)
                .orElse(new DataRecord(id));

        if (side == Side.LEFT) {
            dataRecord.setEncodedBase64StringLeft(encodedBase64String);
        } else if (side == Side.RIGHT) {
            dataRecord.setEncodedBase64StringRight(encodedBase64String);
        }

        log.info("Saving data record to database: [{}]", dataRecord);
        repository.save(dataRecord);
    }

    @Override
    public OffsetsResponse getOffsetsByRecordId(long id) {
        DataRecord entry = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Please provide correct ID"));

        log.debug("entry found: [{}]", entry);

        String left = entry.getEncodedBase64StringLeft();
        String right = entry.getEncodedBase64StringRight();

        validateLeftEncodedBinaryData(left);

        validateRightEncodedBinaryData(right);

        String leftDecoded = Base64Decoder.decode(left);
        String rightDecoded = Base64Decoder.decode(right);

        if (leftDecoded.equals(rightDecoded)) {
            log.info("Payloads are equal");

            return OffsetsResponse.builder()
                    .message(EQUAL)
                    .offsets(Collections.emptyList())
                    .build();
        } else if (leftDecoded.length() != rightDecoded.length()) {
            log.info("Payloads are not of equal length");

            return OffsetsResponse.builder()
                    .message(NOT_EQUAL_LENGTH)
                    .offsets(Collections.emptyList())
                    .build();
        }
        log.info("Payloads are of equal length, but payloads are different. Computing offsets.");

        return OffsetsResponse.builder()
                .message(EQUAL_LENGTH_BUT_DIFFERENT_PAYLOAD)
                .offsets(calculateOffsets(leftDecoded, rightDecoded))
                .build();
    }

    private List<Offset> calculateOffsets(String leftDecoded, String rightDecoded) {
        Map<Integer, Integer> offsetsToLengthMap = OffsetCalculator.calculate(leftDecoded, rightDecoded);
        log.debug("Calculated offsets: [{}]", offsetsToLengthMap);

        return offsetsToLengthMap.entrySet().stream()
                .map(this::buildOffsets)
                .collect(Collectors.toList());
    }

    private Offset buildOffsets(Map.Entry<Integer, Integer> offsetEntry) {
        return Offset.builder()
                .offset(offsetEntry.getKey())
                .length(offsetEntry.getValue())
                .build();
    }

    private void validateRightEncodedBinaryData(String right) {
        if (right == null || right.isEmpty()) {
            throw new BadRequestException("Right payload has not been provided yet");
        }
        log.debug("Data is valid: [{}]", right);
    }

    private void validateLeftEncodedBinaryData(String left) {
        if (left == null || left.isEmpty()) {
            throw new BadRequestException("Left payload has not been provided yet");
        }
        log.debug("Data is valid: [{}]", left);
    }
}
