package pos.modules.order;

import pos.entities.Order;
import pos.modules.pool.PoolFlush;

import java.util.ArrayList;

public interface OrderDbEnd {
    Order getOrder(int orderID);
    ArrayList<Order> getOrders(int begin, int end);
    ArrayList<Order> getOrders(int customerID);
    void addOrder(Order order);
    void removeOrder(int orderID);
    void updateOrder(Order order);
    int getCount();
    void setPoolFlush(PoolFlush<Order> poolFlush);
    void close();
}
