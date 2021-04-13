package com.microhumanresource.entity;

import lombok.Data;

import java.util.Date;
@Data
public class MailSendLog {
    private String msgId;
    private Integer empId;
    //0 消息投递中   1 投递成功   2投递失败
    private Integer status;
    private String routeKey;
    private String exchange;
    private Integer count;
    private Date tryTime;
    private Date createTime;
    private Date updateTime;
}
