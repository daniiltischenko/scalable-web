package com.wearewaes.scalableweb.persistence;

import com.wearewaes.scalableweb.model.entity.DataRecord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository class for {@link DataRecord} entities.
 */
@Repository
public interface DataRecordRepository extends CrudRepository<DataRecord, Long> {

}
