package com.mb.ann.service;

import com.mb.ann.entity.Product;
import com.mb.ann.entity.ProductGroup;
import com.mb.ann.entity.Store;
import com.mb.ann.repository.ProductGroupRepository;
import com.mb.ann.repository.ProductRepository;
import com.mb.ann.repository.StoreRepository;
import com.mb.ann.utils.Parser;
import groovy.lang.GroovyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ProductService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired private GroovyFactoryService groovyFactoryService;
    @Autowired private ProductRepository productRepository;
    @Autowired private StoreRepository storeRepository;
    @Autowired private ProductGroupRepository productGroupRepository;

    public void addProductGroup(ProductGroup productGroup){
        productGroup.setStoreId(1l);
        productGroup.setStatus(ProductGroup.ParseStatus.PENDING);
        productGroupRepository.save(productGroup);
    }

    public void removeProductGroup(Long productGroupId){
        productGroupRepository.delete(productGroupId);
    }

    public List<ProductGroup> getProductGroups(){
        return (List<ProductGroup>) productGroupRepository.findAll();
    }

    @Async
    public CompletableFuture<ProductGroup> parse(ProductGroup productGroup) {
        try {
            Store store = storeRepository.findOne(productGroup.getStoreId());
            List<Product> products = ((Parser) getParser(store)).parse(productGroup);
            if (products != null && !products.isEmpty()) {
                final Timestamp updateTime = new Timestamp(System.currentTimeMillis());
                products.forEach(p -> p.setUpdateTime(updateTime));
                productRepository.save(products);
            }
            productGroup.setStatus(ProductGroup.ParseStatus.SUCCESS);
            productGroup.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        } catch (Exception e){
            productGroup.setStatus(ProductGroup.ParseStatus.FAIL);
            productGroup.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            productGroup.setException(e.getMessage());
        }
        return CompletableFuture.completedFuture(productGroupRepository.save(productGroup));
    }

    private GroovyObject getParser(Store store){
        return groovyFactoryService.get(store.getParserClass());
    }

    public ProductGroup getProductGroup(long productGroupId){
        return productGroupRepository.findOne(productGroupId);
    }
}
