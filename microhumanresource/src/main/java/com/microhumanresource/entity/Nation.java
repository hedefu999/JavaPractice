package com.microhumanresource.entity;

import lombok.Data;

@Data
public class Nation {
    private Integer id;

    private String name;

    public Nation(String name) {
        this.name = name;
    }
}
