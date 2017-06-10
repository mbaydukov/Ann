package com.mb.ann.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name="PRODUCT_QUEUE")
public class ProductQueue {

    public enum ParseStatus {
        PENDING, SUCCESS, FAIL
    }

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private Long storeId;
    private String url;
    private Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    @Enumerated(EnumType.STRING)
    private ParseStatus status;

    public ProductQueue(Long storeId, String url) {
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

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public ParseStatus getStatus() {
        return status;
    }

    public void setStatus(ParseStatus status) {
        this.status = status;
    }
}
