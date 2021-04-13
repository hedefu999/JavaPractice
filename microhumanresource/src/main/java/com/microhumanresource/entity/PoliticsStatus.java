package com.microhumanresource.entity;

import lombok.Data;

@Data
public class PoliticsStatus {
    private Integer id;

    private String name;
    public PoliticsStatus(){}
    public PoliticsStatus(String name) {
        this.name = name;
    }
}
