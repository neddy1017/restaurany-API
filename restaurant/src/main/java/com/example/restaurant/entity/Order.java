package com.example.restaurant.entity;

import java.util.List;

public class Order {
    private String customerid;
    private List<Product> productid;

    public Order() {

    }

    public Order(String customerid,List<Product> productid) {
        this.customerid = customerid;
        this.productid = productid;
    }

    public String getId() {
        return customerid;
    }

    public void setId(String customerid) {
        this.customerid = customerid;
    }

    public List<Product> getproduct() {
        return productid;
    }

    public void setproduct(List<Product> productid) {
        this.productid = productid;
    }
    

}
