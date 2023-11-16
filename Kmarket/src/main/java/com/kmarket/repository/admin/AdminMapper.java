package com.kmarket.repository.admin;

import com.kmarket.domain.Products;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper {
    int insertProduct(Products products);
}
