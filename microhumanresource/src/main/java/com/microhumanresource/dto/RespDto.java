package com.microhumanresource.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
@AllArgsConstructor
public class RespDto {
    private Integer status;
    private String msg;
    private Object obj;

    public static RespDto build() {
        return new RespDto();
    }

    public static RespDto ok(String msg) {
        return new RespDto(200, msg, null);
    }

    public static RespDto ok(String msg, Object obj) {
        return new RespDto(200, msg, obj);
    }

    public static RespDto error(String msg) {
        return new RespDto(500, msg, null);
    }

    public static RespDto error(String msg, Object obj) {
        return new RespDto(500, msg, obj);
    }

}
