package com.mb.ann.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name="PRODUCT_GROUP")
public class ProductGroup {

    public enum ParseStatus {
        PENDING, SUCCESS, FAIL
    }

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private Long storeId;
    private String url;
    private String description;
    private Timestamp updateTime;
    @Enumerated(EnumType.STRING)
    private ParseStatus status;
    private String exception;

    public ProductGroup(){}

    public ProductGroup(Long storeId, String url) {
        this.storeId = storeId;
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public ParseStatus getStatus() {
        return status;
    }

    public void setStatus(ParseStatus status) {
        this.status = status;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }
}
