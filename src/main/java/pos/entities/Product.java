package pos.entities;

import java.math.BigDecimal;

public class Product {
    private int ProductID;
    private String name;
    private BigDecimal price;
    private int stock;

    public Product(int productID, String name, BigDecimal price, int stock) {
        this.ProductID = productID;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public int getProductID() {
        return ProductID;
    }

    public void setProductID(int productID) {
        ProductID = productID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
