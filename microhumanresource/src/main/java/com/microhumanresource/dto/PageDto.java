package com.microhumanresource.dto;

import lombok.Data;

import java.util.List;
@Data
public class PageDto {
    private Long total;
    private List<?> data;
}
