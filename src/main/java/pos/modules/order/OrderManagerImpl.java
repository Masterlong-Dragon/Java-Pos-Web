package pos.modules.order;

import pos.auth.PermissionImpl;
import pos.entities.Order;
import pos.entities.individuals.Individual;
import pos.modules.individuals.CustomerManager;
import pos.modules.pool.PoolFlush;
import pos.modules.pool.ResourcePool;
import pos.modules.pool.ResourcePoolGet;
import pos.modules.stock.StockManager;
import pos.modules.stock.StockManagerImpl;
import pos.user.Role;
import pos.user.User;

import java.util.ArrayList;

public class OrderManagerImpl implements OrderManager {

    private ResourcePool<Order> orderFactory;
    private OrderDbEnd orderDbEnd;

    private static OrderManagerImpl instance;

    public static OrderManagerImpl Instance() {
        return OrderManagerImpl.InstanceHolder.INSTANCE.getInstance();
    }

    private enum InstanceHolder {
        INSTANCE();
        private OrderManagerImpl instance;

        InstanceHolder() {
            instance = new OrderManagerImpl();
        }

        public OrderManagerImpl getInstance() {
            return instance;
        }
    }

    public OrderManagerImpl() {
        orderDbEnd = new OrderDbEndImpl(StockManagerImpl.Instance(), CustomerManager.Instance());
        orderFactory = new ResourcePool<>(100, new ResourcePoolGet<Order>() {
            @Override
            public Order get(int ID) {
                return orderDbEnd.getOrder(ID);
            }
        });
        orderDbEnd.setPoolFlush(new PoolFlush<Order>() {
            @Override
            public void flush(int ID, Order order) {
                orderFactory.flush(order.getID(), order);
            }
        });
    }

    @Override
    public Order getOrder(User user, int orderID) throws IllegalArgumentException{
        if (!PermissionImpl.Instance().checkPermission(user, pos.user.Role.Admin, pos.user.Role.SalesPerson))
            throw new IllegalArgumentException("No permission to access order manager.");
        return orderFactory.get(orderID);
    }

    @Override
    public ArrayList<Order> getOrders(User user, int begin, int end) throws IllegalArgumentException{
        if (!PermissionImpl.Instance().checkPermission(user, pos.user.Role.Admin))
            throw new IllegalArgumentException("No permission to access order manager.");
        ArrayList<Order> orders = orderDbEnd.getOrders(begin, end);
        return orders;
    }

    @Override
    public ArrayList<Order> getOrders(User user, Individual customer) throws IllegalArgumentException{
        if (!PermissionImpl.Instance().checkPermission(user, pos.user.Role.Admin))
            throw new IllegalArgumentException("No permission to access order manager.");
        ArrayList<Order> orders = orderDbEnd.getOrders(customer.getID());
        return orders;
    }

    @Override
    public void addOrder(User user, Order order) throws IllegalArgumentException{
        if (!PermissionImpl.Instance().checkPermission(user, pos.user.Role.Admin, Role.SalesPerson))
            throw new IllegalArgumentException("No permission to access order manager.");
        orderDbEnd.addOrder(order);
    }

    @Override
    public void removeOrder(User user, int orderID) throws IllegalArgumentException{
        if (!PermissionImpl.Instance().checkPermission(user, pos.user.Role.Admin))
            throw new IllegalArgumentException("No permission to access order manager.");
        orderDbEnd.removeOrder(orderID);
    }

    @Override
    public void updateOrder(User user, Order order) throws IllegalArgumentException{
        if (!PermissionImpl.Instance().checkPermission(user, pos.user.Role.Admin))
            throw new IllegalArgumentException("No permission to access order manager.");
        orderDbEnd.updateOrder(order);
    }

    @Override
    public void close(User user) throws IllegalArgumentException{
        if (!PermissionImpl.Instance().checkPermission(user, pos.user.Role.Admin))
            throw new IllegalArgumentException("No permission to access order manager.");
        orderDbEnd.close();
    }
}
