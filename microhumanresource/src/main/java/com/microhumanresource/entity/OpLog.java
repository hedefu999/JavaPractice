package com.microhumanresource.entity;

import lombok.Data;

import java.util.Date;
@Data
public class OpLog {
    private Integer id;

    private Date addDate;

    private String operate;

    private Integer hrid;
}
