package pos.modules.order;

import pos.entities.Order;
import pos.entities.individuals.Individual;
import pos.user.User;

import java.util.ArrayList;

public interface OrderManager {
    Order getOrder(User user, int orderID);
    ArrayList<Order> getOrders(User user, int begin, int end);
    ArrayList<Order> getOrders(User user, Individual customer);
    void addOrder(User user, Order order);
    void removeOrder(User user, int orderID);
    void updateOrder(User user, Order order);
    void close(User user);
}
