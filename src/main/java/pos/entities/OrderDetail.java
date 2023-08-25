package pos.entities;

import java.math.BigDecimal;

public class OrderDetail {
    private Product product;
    private int quantity;
    private BigDecimal unitPrice;

    public OrderDetail(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        updateUnitPrice();
    }

    public OrderDetail(Product product, int quantity, BigDecimal unitPrice) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        updateUnitPrice();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        updateUnitPrice();
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void updateUnitPrice() {
        unitPrice = product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public String toString() {
        return "POS.OrderDetail{" +
                "product=" + product +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                '}';
    }
}
