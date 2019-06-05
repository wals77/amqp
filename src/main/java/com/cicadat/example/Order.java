package com.cicadat.example;

import java.util.Date;

public class Order {

    private String id;    //订单ID

    private Integer status; //订单状态 0支付中 1支付成功 2 支付失败

    private Date createTime;    //创建时间

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
