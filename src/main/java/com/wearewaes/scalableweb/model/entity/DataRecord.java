package com.wearewaes.scalableweb.model.entity;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity for "records" table. Contains left and right Base64 encoded strings.
 */
@RequiredArgsConstructor
@Data
@Table(name = "records")
@Entity
public class DataRecord {

    @NonNull
    @Id
    private Long id;

    @Column(name = "encoded_left")
    private String encodedBase64StringLeft;

    @Column(name = "encoded_right")
    private String encodedBase64StringRight;

    public DataRecord() {

    }
}
