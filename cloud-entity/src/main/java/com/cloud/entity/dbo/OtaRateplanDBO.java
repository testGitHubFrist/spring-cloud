package com.cloud.entity.dbo;

import java.util.Date;

/**
 * 表名：ota_rateplan
 * 生成时间：2018-06-07 11:11:52
 */
public class OtaRateplanDBO {
    /**
     * id:
     */
    private Integer id;

    /**
     * supplier_code:直连供应商code
     */
    private Integer supplierCode;

    /**
     * group_key:分组键
     */
    private String groupKey;

    /**
     * elong_hotel_id:
     */
    private String elongHotelId;

    /**
     * elong_rateplan_id:
     */
    private Integer elongRateplanId;

    /**
     * supplier_hotel_id:
     */
    private String supplierHotelId;

    /**
     * supplier_rateplan_id:
     */
    private String supplierRateplanId;

    /**
     * enable:1=可用，0=不可用
     */
    private Integer enable;

    /**
     * operator:
     */
    private String operator;

    /**
     * operate_date:
     */
    private Date operateDate;

    /**
     * _timestamp:
     */
    private Date timestamp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(Integer supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public String getElongHotelId() {
        return elongHotelId;
    }

    public void setElongHotelId(String elongHotelId) {
        this.elongHotelId = elongHotelId;
    }

    public Integer getElongRateplanId() {
        return elongRateplanId;
    }

    public void setElongRateplanId(Integer elongRateplanId) {
        this.elongRateplanId = elongRateplanId;
    }

    public String getSupplierHotelId() {
        return supplierHotelId;
    }

    public void setSupplierHotelId(String supplierHotelId) {
        this.supplierHotelId = supplierHotelId;
    }

    public String getSupplierRateplanId() {
        return supplierRateplanId;
    }

    public void setSupplierRateplanId(String supplierRateplanId) {
        this.supplierRateplanId = supplierRateplanId;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Date getOperateDate() {
        return operateDate;
    }

    public void setOperateDate(Date operateDate) {
        this.operateDate = operateDate;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}