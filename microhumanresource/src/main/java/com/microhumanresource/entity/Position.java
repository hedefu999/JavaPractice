package com.microhumanresource.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
@Data
public class Position {
    private Integer id;
    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "Asia/Shanghai")
    private Date createDate;

    private Boolean enabled;
    public Position(){}
    public Position(String name) {
        this.name = name;
    }
}
