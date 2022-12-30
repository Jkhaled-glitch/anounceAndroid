package com.example.ann;

public class Product {
    private String productId;
    private String productName;
    private String productPrice;
    private String productLocal;
    private String productImage;
    private String productDate;
    private String userPhone;
    private String userEmail;
    public Product() {}
    public Product(String productId,String productName,String productPrice,String productLocal,String productImage,String productDate,String userPhone,String userEmail)
    {
        this.productId=productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productLocal = productLocal;
        this.productImage = productImage;
        this.productDate = productDate;
        this.userPhone = userPhone;
        this.userEmail=userEmail;
    }
    //productId

    public String getProductId()
    {
        return productId;
    }

    //productName
    public String getProductName()
    {
        return productName;
    }
    public void setProductName(String nom)
    {
        this.productName = productName;
    }

    //productPrice
    public String getProductPrice()
    {
        return productPrice;
    }
    public void setProductPrice(String productPrice)
    {
        this.productPrice = productPrice;
    }

    //productLocal
    public String getProductLocal() {
        return productLocal;
    }
    public void setProductLocal(String productLocal)
    {
        this.productLocal = productLocal;
    }

    //productImage
    public String getProductImage() {
        return productImage;
    }
    public void setProductImage(String productImage)
    {
        this.productImage = productImage;
    }

    //productDate
    public String getProductDate() {
        return productDate;
    }

    //userPhone
    public String getUserPhone() {
        return userPhone;
    }
    public String getUserEmail() {
        return userEmail;
    }



}

