package com.webdevlab.tablicabackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TagUsageDTO {
    private String tag;
    private Long usage;
}
