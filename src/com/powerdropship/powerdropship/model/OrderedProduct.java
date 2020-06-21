package com.powerdropship.powerdropship.model;

import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
public class OrderedProduct {

    private String vendorSku;
    private String productUpc;
    private String productName;
    private String productOrderQuantity;

    public String getVendorSku() {
        return vendorSku;
    }

    public void setVendorSku(String vendorSku) {
        this.vendorSku = vendorSku;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductUpc() {
        return productUpc;
    }

    public void setProductUpc(String productUpc) {
        this.productUpc = productUpc;
    }

    public String getProductOrderQuantity() {
        return productOrderQuantity;
    }

    public void setProductOrderQuantity(String productOrderQuantity) {
        this.productOrderQuantity = productOrderQuantity;
    }
}
