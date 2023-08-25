package pos.entities;

import pos.entities.individuals.Individual;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Order {
    private int ID;
    private Individual customer;
    private Date date;
    private List<OrderDetail> detail;
    private BigDecimal totalAmount;

    public Order(Individual customer) {
        this.customer = customer;
        this.date = new Date();
        this.totalAmount = new BigDecimal(0);
        this.detail = new LinkedList<>();
    }

    public Order(int ID, Individual customer, Date date, List<OrderDetail> detail, BigDecimal totalAmount) {
        this.ID = ID;
        this.customer = customer;
        this.date = date;
        this.detail = detail;
        this.totalAmount = totalAmount;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Individual getCustomer() {
        return customer;
    }

    public void setCustomer(Individual customer) {
        this.customer = customer;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<OrderDetail> getDetail() {
        return detail;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void updateTotalAmount() {
        // 重新计算总金额
        this.totalAmount = new BigDecimal(0);
        for (OrderDetail orderDetail : detail) {
            this.totalAmount = this.totalAmount.add(orderDetail.getUnitPrice());
        }
    }

    public void addDetail(Product product, int quantity) {
        // 检查是否已经存在
        for (OrderDetail orderDetail : detail) {
            if (orderDetail.getProduct().getProductID() == product.getProductID()) {
                orderDetail.setQuantity(orderDetail.getQuantity() + quantity);
                this.updateTotalAmount();
                return;
            }
        }
        OrderDetail orderDetail = new OrderDetail(product, quantity);
        this.detail.add(orderDetail);
        this.updateTotalAmount();
    }

    public void updateDetail(int productID, int quantity) {
        for (OrderDetail orderDetail : detail) {
            if (orderDetail.getProduct().getProductID() == productID) {
                orderDetail.setQuantity(quantity);
                this.updateTotalAmount();
                break;
            }
        }
    }

    public void removeDetail(int productID) {
        for (OrderDetail orderDetail : detail) {
            if (orderDetail.getProduct().getProductID() == productID) {
                this.detail.remove(orderDetail);
                this.updateTotalAmount();
                break;
            }
        }
    }

    public void removeDetail(int productID, int quantity) {
        for (OrderDetail orderDetail : detail) {
            if (orderDetail.getProduct().getProductID() == productID) {
                if (orderDetail.getQuantity() <= quantity) {
                    this.detail.remove(orderDetail);
                } else {
                    orderDetail.setQuantity(orderDetail.getQuantity() - quantity);
                }
                this.updateTotalAmount();
                break;
            }
        }
    }

    public OrderDetail getDetail(int productID) {
        for (OrderDetail orderDetail : detail) {
            if (orderDetail.getProduct().getProductID() == productID) {
                return orderDetail;
            }
        }
        throw new IllegalArgumentException("Order detail not found.");
    }
}
