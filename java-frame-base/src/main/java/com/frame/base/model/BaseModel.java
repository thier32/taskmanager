package com.frame.base.model;

import com.frame.base.utils.DateUtils;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import java.time.LocalDateTime;

@MappedSuperclass
public class BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Long id;


    protected Long createdBy;

    protected String createdByUserName = "System";

    protected String updateByUserName = "System";

    protected Long updatedBy;

    protected LocalDateTime createdAt = DateUtils.localDateTimeGmt();;

    protected LocalDateTime updatedAt = DateUtils.localDateTimeGmt();;

    public BaseModel() {
        if (id == null) {
            createdAt = DateUtils.localDateTimeGmt();
        }
        updatedAt = DateUtils.localDateTimeGmt();;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreatedByUserName() {
        return createdByUserName;
    }

    public BaseModel setCreatedByUserName(String createdByUserName) {
        this.createdByUserName = createdByUserName;
        return this;
    }

    public String getUpdateByUserName() {
        return updateByUserName;
    }

    public BaseModel setUpdateByUserName(String updateByUserName) {
        this.updateByUserName = updateByUserName;
        return this;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public BaseModel setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }
}
