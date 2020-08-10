package com.icodingedusales.entity;

import java.io.Serializable;

/**
 * (Product)实体类
 *
 * @author makejava
 * @since 2020-05-19 22:12:18
 */
public class Product implements Serializable {
    private static final long serialVersionUID = -68155441762153289L;
    
    private Integer id;
    
    private String name;
    /**
    * 价格
    */
    private Double price;
    /**
    * 库存
    */
    private Integer stock;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

}