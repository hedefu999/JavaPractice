package com.microhumanresource.entity;

import lombok.Data;

import java.util.Date;
@Data
public class EmployeeRemove {
    private Integer id;

    private Integer eid;

    private Integer afterdepid;

    private Integer afterjobid;

    private Date removedate;

    private String reason;

    private String remark;
}
