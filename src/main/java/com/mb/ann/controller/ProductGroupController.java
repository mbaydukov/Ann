package com.mb.ann.controller;

import com.mb.ann.entity.ProductGroup;
import com.mb.ann.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ProductGroupController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired private ProductService productService;

    @RequestMapping("/")
    public String welcome(){
        return "/index.html";
    }

    @RequestMapping("/product-group/parse")
    @ResponseBody
    public void parse(@RequestParam Long productGroupId){
        ProductGroup productGroup = productService.getProductGroup(productGroupId);
        productService.parse(productGroup);
    }

    @RequestMapping("/product-group/add")
    @ResponseBody
    public void addUrl(@RequestBody ProductGroup productGroup){
        productService.addProductGroup(productGroup);
    }

    @RequestMapping("/product-group/remove")
    public void removeUrl(@RequestParam Long productGroupId){
        productService.removeProductGroup(productGroupId);
    }

    @ResponseBody
    @RequestMapping("/product-group/list")
    public List<ProductGroup> list(){
        return productService.getProductGroups();
    }
}
