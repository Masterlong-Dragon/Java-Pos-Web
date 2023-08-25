package pos.entities;

import java.math.BigDecimal;
import java.util.Date;

public class Payment {
    private int ID;
    private int orderID;
    private Date date;
    private PaymentMethod method;
    private BigDecimal amount;
    private BigDecimal changeAmount;

    public Payment(BigDecimal amount, BigDecimal changeAmount, PaymentMethod paymentMethod, int orderID) {
        this.amount = amount;
        this.changeAmount = changeAmount;
        this.method = paymentMethod;
        this.orderID = orderID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(BigDecimal changeAmount) {
        this.changeAmount = changeAmount;
    }
}
