package com.mb.ann.service;

import com.mb.ann.entity.Product;
import com.mb.ann.entity.ProductQueue;
import com.mb.ann.entity.Store;
import com.mb.ann.repository.ProductQueueRepository;
import com.mb.ann.repository.ProductRepository;
import com.mb.ann.repository.StoreRepository;
import com.mb.ann.utils.Parser;
import groovy.lang.GroovyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired private GroovyFactoryService groovyFactoryService;
    @Autowired private ProductRepository productRepository;
    @Autowired private StoreRepository storeRepository;
    @Autowired private ProductQueueRepository productQueueRepository;

    public void addUrlToQueue(Long storeId, String url){
        productQueueRepository.save(new ProductQueue(storeId, url));
    }

    public void removeUrlFromQueue(Long productQueueId){
        productQueueRepository.delete(productQueueId);
    }

    public List<Product> parse(Long storeId, String url) throws Exception {
        Store store = storeRepository.findOne(storeId);
        List<Product> products = ((Parser)getParser(store)).parse(url);
        products.forEach(p -> p.setStoreId(store.getId()));
        productRepository.save(products);
        return products;
    }

    private GroovyObject getParser(Store store){
        return groovyFactoryService.get(store.getName());
    }
}
