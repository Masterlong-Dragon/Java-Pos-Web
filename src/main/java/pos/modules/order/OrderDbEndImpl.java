package pos.modules.order;

import pos.config.ConfigManager;
import pos.config.ConfigManagerImpl;
import pos.db.DBConnection;
import pos.entities.Order;
import pos.entities.OrderDetail;
import pos.entities.individuals.Individual;
import pos.modules.individuals.CustomerManager;
import pos.modules.pool.PoolFlush;
import pos.modules.stock.StockManager;
import pos.modules.stock.StockManagerImpl;
import pos.user.Role;
import pos.user.User;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class OrderDbEndImpl implements OrderDbEnd {

    private User user;
    private final DBConnection conn;
    private final StockManager stockManager;
    private final CustomerManager customerManager;
    private final PreparedStatement selectByCustomerID;
    private final PreparedStatement selectDetails;
    private final PreparedStatement insert;
    private final PreparedStatement update;
    private final PreparedStatement delete;
    private final PreparedStatement insertDetail;
    private final PreparedStatement updateDetail;
    private final PreparedStatement deleteDetail;
    private final PreparedStatement lastID;
    private PoolFlush<Order> poolFlush;

    public OrderDbEndImpl(StockManager stockManager, CustomerManager customerManager) {
        poolFlush = new PoolFlush<>() {
            @Override
            public void flush(int ID, Order object) {

            }
        };
        this.stockManager = stockManager;
        this.customerManager = customerManager;
        ConfigManager configManager = ConfigManagerImpl.Instance();
        conn = new DBConnection(configManager.getDBURL(),
                configManager.getDBUser(Role.Admin),
                configManager.getDBPassword(Role.Admin));
        try {
            conn.getConnection().setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // 这个部分应当更严谨地设计 但我没精力调整了
        user = new User(0, "root", Role.Admin, UUID.fromString("00000000-0000-0000-0000-000000000000"));
        // 初始化预编译语句
        conn.setTableName("orders");
        conn.setIDName("OrderID");
        selectByCustomerID = conn.createPreparedStatement("SELECT * FROM pos.orders WHERE CustomerID = ?");
        selectDetails = conn.createPreparedStatement("SELECT * FROM pos.orderdetails WHERE OrderID = ?");
        // insert采用事务
        insert = conn.createPreparedStatement("INSERT INTO pos.orders (CustomerID, TotalAmount, OrderDate) VALUES (?, ?, ?)");
        update = conn.createPreparedStatement("UPDATE pos.orders SET CustomerID = ?, TotalAmount = ?, OrderDate = ? WHERE OrderID = ?");
        delete = conn.createPreparedStatement("DELETE FROM pos.orders WHERE OrderID = ?");
        insertDetail = conn.createPreparedStatement("INSERT INTO pos.orderdetails (OrderID, ProductID, Quantity, UnitPrice) VALUES (?, ?, ?, ?)");
        updateDetail = conn.createPreparedStatement("UPDATE pos.orderdetails SET ProductID = ?, Quantity = ?, UnitPrice = ? WHERE OrderID = ?");
        deleteDetail = conn.createPreparedStatement("DELETE FROM pos.orderdetails WHERE OrderID = ?");
        lastID = conn.createPreparedStatement("SELECT LAST_INSERT_ID() AS OrderID");
    }

    private Order getOrder(ResultSet rs) throws SQLException {
        int orderID = rs.getInt("OrderID");
        int customerID = rs.getInt("CustomerID");
        BigDecimal totalAmount = rs.getBigDecimal("TotalAmount");
        Date date = rs.getDate("OrderDate");
        // 获取订单详情
        selectDetails.setInt(1, orderID);
        selectDetails.execute();
        conn.getConnection().commit();
        ResultSet rsDetails = selectDetails.getResultSet();
        ArrayList<OrderDetail> details = new ArrayList<>();
        while (rsDetails.next()) {
            int productID = rsDetails.getInt("ProductID");
            int quantity = rsDetails.getInt("Quantity");
            BigDecimal price = rsDetails.getBigDecimal("UnitPrice");
            details.add(new OrderDetail(stockManager.showProduct(user, productID), quantity, price));
        }
        // 获取顾客信息
        Individual customer = (Individual) customerManager.getIndividual(user, customerID);
        return new Order(orderID, customer, date, details, totalAmount);
    }

    @Override
    public Order getOrder(int orderID) {
        Order order = null;
        try {
            // 结果不为空
            ResultSet rs = conn.selectIDQuery(orderID);
            if (!rs.next())
                throw new IllegalArgumentException("Order not found.");
            return getOrder(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<Order> getOrders(int begin, int end) {
        ArrayList<Order> orders = new ArrayList<>();
        try {
            ResultSet rs = conn.selectRangeQuery(end - begin, begin);
            Order order = getOrder(rs);
            poolFlush.flush(order.getID(), order);
            orders.add(order);
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    @Override
    public ArrayList<Order> getOrders(int customerID) {
        ArrayList<Order> orders = new ArrayList<>();
        try {
            selectByCustomerID.setInt(1, customerID);
            selectByCustomerID.execute();
            conn.getConnection().commit();
            ResultSet rs = selectByCustomerID.getResultSet();
            Order order = getOrder(rs);
            poolFlush.flush(order.getID(), order);
            orders.add(order);
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    @Override
    public void addOrder(Order order) throws IllegalArgumentException{
        try {
            insert.setInt(1, order.getCustomer().getID());
            insert.setBigDecimal(2, order.getTotalAmount());
            insert.setDate(3, new java.sql.Date(order.getDate().getTime()));
            insert.execute();
            // 获取订单ID
            ResultSet rs = lastID.executeQuery();
            if (!rs.next())
                throw new IllegalArgumentException("Order not found.");
            order.setID(rs.getInt("OrderID"));
            // 订单详情
            for (OrderDetail detail : order.getDetail()) {
                insertDetail.setInt(1, order.getID());
                insertDetail.setInt(2, detail.getProduct().getProductID());
                insertDetail.setInt(3, detail.getQuantity());
                insertDetail.setBigDecimal(4, detail.getUnitPrice());
                insertDetail.execute();
            }
            conn.getConnection().commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.getConnection().rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void removeOrder(int orderID) {
        try {
            // 检查是否存在
            ResultSet rs = conn.selectIDQuery(orderID);
            if (!rs.next())
                throw new IllegalArgumentException("Order not found.");
            delete.setInt(1, orderID);
            delete.execute();
            // 删除订单详情
            deleteDetail.setInt(1, orderID);
            deleteDetail.execute();
            conn.getConnection().commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.getConnection().rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void updateOrder(Order order) throws IllegalArgumentException{
        try {
            // 检查是否存在
            ResultSet rs = conn.selectIDQuery(order.getID());
            if (!rs.next())
                throw new IllegalArgumentException("Order not found.");
            update.setInt(1, order.getCustomer().getID());
            update.setBigDecimal(2, order.getTotalAmount());
            update.setDate(3, new java.sql.Date(order.getDate().getTime()));
            update.setInt(4, order.getID());
            update.execute();
            // 更新订单详情
            deleteDetail.setInt(1, order.getID());
            deleteDetail.execute();
            for (OrderDetail detail : order.getDetail()) {
                insertDetail.setInt(1, order.getID());
                insertDetail.setInt(2, detail.getProduct().getProductID());
                insertDetail.setInt(3, detail.getQuantity());
                insertDetail.setBigDecimal(4, detail.getUnitPrice());
                insertDetail.execute();
            }
            conn.getConnection().commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.getConnection().rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public int getCount() {
        return conn.selectCountQuery();
    }

    @Override
    public void setPoolFlush(PoolFlush<Order> poolFlush) {
        this.poolFlush = poolFlush;
    }

    @Override
    public void close() {
        conn.close();
    }
}
