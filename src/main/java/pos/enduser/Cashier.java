package pos.enduser;

import pos.entities.*;
import pos.entities.individuals.*;
import pos.modules.order.OrderManager;
import pos.modules.order.OrderManagerImpl;
import pos.modules.sale.ReturnImpl;
import pos.modules.sale.Sale;
import pos.modules.sale.SaleImpl;
import pos.modules.stock.StockManager;
import pos.user.*;

import java.math.BigDecimal;

public class Cashier {

    private Sale currentState;
    private User user;
    private Individual salesPerson;

    public Cashier(User user) {
        this.user = user;
        try {
            currentState = SaleImpl.Instance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCurrentState(Sale currentState) {
        this.currentState = currentState;
    }

    public void start(Individual salesPerson, Order order) {
        this.salesPerson = salesPerson;
        currentState.start(user, salesPerson, order);
    }

    public void addItem(int productID, int quantity) {
        currentState.addItem(user, productID, quantity);
    }

    public void updateItem(int productID, int quantity) {
        currentState.updateItem(user, productID, quantity);
    }

    public void removeItem(int productID, int quantity) {
        currentState.removeItem(user, productID, quantity);
    }

    public void removeItem(int productID){
        currentState.removeItem(user, productID);
    }

    public void createPayment(BigDecimal cash, PaymentMethod paymentMethod) {
        currentState.createPayment(user, cash, paymentMethod);
    }

    public void submitPayment() {
        currentState.submitPayment(user);
    }

    public void submitSale() {
        currentState.submit(user);
    }


    public void printReceipt() {
        currentState.printReceipt(user);
    }

    public User getUser() {
        return user;
    }

    public Individual getSalePerson() {
        return salesPerson;
    }

    public Order getOrder(int orderID) {
        return OrderManagerImpl.Instance().getOrder(user, orderID);
    }

    public Order getCurrentOrder() {
        return currentState.getOrder(user);
    }
}
