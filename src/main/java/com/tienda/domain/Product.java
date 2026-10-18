package com.tienda.domain;
import java.math.BigDecimal;
public record Product(Integer id,String code,String name,BigDecimal price,String image) {}
