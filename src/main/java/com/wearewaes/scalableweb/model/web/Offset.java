package com.wearewaes.scalableweb.model.web;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Offset {
    private long offset;
    private long length;
}
