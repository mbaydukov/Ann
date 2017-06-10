package com.mb.ann.controller;

import com.mb.ann.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProductController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired private ProductService productService;

    @RequestMapping("/")
    public ResponseEntity parse(@RequestParam Long storeId, String url){
        try {
            productService.parse(storeId, url);
        } catch (Exception e){
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
        }
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    @RequestMapping("/product/add")
    public void addUrl(@RequestParam Long storeId, String url){
        productService.addUrlToQueue(storeId, url);
    }

    @RequestMapping("/product/remove")
    public void removeUrl(@RequestParam Long storeId, String url){
        productService.addUrlToQueue(storeId, url);
    }
}
