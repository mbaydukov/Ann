package com.mb.ann.utils;

import com.mb.ann.entity.Product;
import com.mb.ann.entity.ProductGroup;

import java.util.List;

public interface Parser {

    public List<Product> parse(ProductGroup productGroup) throws Exception;

}
