package com.microhumanresource.entity;

import com.microhumanresource.dto.Meta;
import lombok.Data;

import java.util.List;
@Data
public class Menu {
    private Integer id;

    private String url;

    private String path;

    private String component;

    private String name;

    private String iconCls;
    /**
     * 表中的部分字段组成一个实体的对象域，此时可使用mybatis的association
     */
    private Meta meta;

    private Integer parentId;

    private Boolean enabled;
    private List<Menu> children;
    private List<Role> roles;
}
