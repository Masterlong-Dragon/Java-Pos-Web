package pos.entities;

import pos.entities.individuals.Individual;

import java.util.Date;

public class TransactionLog {
    private int ID;
    private Date date;
    private Individual customer;
    private Individual salesPerson;
    private TransactionType type;
    private Payment payment;

    public TransactionLog(Order order, Payment payment, Individual salesPerson, TransactionType type) {
        this.payment = payment;
        this.date = payment.getDate();
        this.customer = order.getCustomer();
        this.salesPerson = salesPerson;
        this.type = type;
    }

    public TransactionLog(Date date, Individual customer, Individual salesPerson, TransactionType type, Payment payment) {
        this.date = date;
        this.customer = customer;
        this.salesPerson = salesPerson;
        this.type = type;
        this.payment = payment;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public java.util.Date getDate() {
        return date;
    }

    public void setDate(java.util.Date date) {
        this.date = date;
    }

    public Individual getCustomer() {
        return customer;
    }

    public void setCustomer(Individual customer) {
        this.customer = customer;
    }

    public Individual getSalesPerson() {
        return salesPerson;
    }

    public void setSalesPerson(Individual salesPerson) {
        this.salesPerson = salesPerson;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}
