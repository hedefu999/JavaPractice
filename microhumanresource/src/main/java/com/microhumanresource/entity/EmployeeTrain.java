package com.microhumanresource.entity;

import lombok.Data;

import java.util.Date;
@Data
public class EmployeeTrain {
    private Integer id;

    private Integer eid;

    private Date traindate;

    private String traincontent;

    private String remark;
}
