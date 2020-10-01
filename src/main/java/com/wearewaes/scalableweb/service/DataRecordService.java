package com.wearewaes.scalableweb.service;

import com.wearewaes.scalableweb.model.web.OffsetsResponse;
import com.wearewaes.scalableweb.model.web.Side;

/**
 * Provides interface for interacting with records from database.
 */
public interface DataRecordService {

    /**
     * Persists data record to database.
     *
     * @param id                  of the record.
     * @param side                left or right
     * @param encodedBase64String
     */
    void save(long id, Side side, String encodedBase64String);

    /**
     * Retrieves data from DB and calculates offsets.
     *
     * @param id
     * @return
     */
    OffsetsResponse getOffsetsByRecordId(long id);
}
