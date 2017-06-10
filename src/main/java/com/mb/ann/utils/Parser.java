package com.mb.ann.utils;

import com.mb.ann.entity.Product;

import java.util.List;

public interface Parser {

    public List<Product> parse(String url) throws Exception;

}
