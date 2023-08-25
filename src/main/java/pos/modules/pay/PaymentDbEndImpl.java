package pos.modules.pay;

import pos.config.ConfigManager;
import pos.config.ConfigManagerImpl;
import pos.db.DBConnection;
import pos.entities.Payment;
import pos.entities.PaymentMethod;
import pos.modules.order.OrderManagerImpl;
import pos.modules.pool.PoolFlush;
import pos.user.Role;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class PaymentDbEndImpl implements PaymentDbEnd {

    private final DBConnection conn;
    private final ConfigManager configManager;
    private final PreparedStatement selectByOrderID;
    private final PreparedStatement selectByDate;
    private final PreparedStatement insert;
    private final PreparedStatement lastID;
    private PoolFlush<Payment> poolFlush;

    public PaymentDbEndImpl() {
        poolFlush = new PoolFlush<>() {
            @Override
            public void flush(int ID, Payment object) {

            }
        };
        configManager = ConfigManagerImpl.Instance();
        conn = new DBConnection(configManager.getDBURL(),
                configManager.getDBUser(Role.Admin),
                configManager.getDBPassword(Role.Admin));
        conn.setTableName("payments");
        conn.setIDName("PaymentID");
        try {
            conn.getConnection().setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // 初始化预编译语句
        selectByOrderID = conn.createPreparedStatement("SELECT * FROM pos.payments WHERE OrderID = ?");
        selectByDate = conn.createPreparedStatement("SELECT * FROM pos.payments WHERE PaymentDate BETWEEN ? AND ?");
        insert = conn.createPreparedStatement("INSERT INTO pos.payments (OrderID, PaymentDate, PaymentMethod, ChangeAmount, Amount) VALUES (?, ?, ?, ?, ?)");
        lastID = conn.createPreparedStatement("SELECT LAST_INSERT_ID() AS PaymentID");
    }


    @Override
    public Payment getPayment(int paymentID) {
        Payment payment = null;
        try {
            ResultSet rs = conn.selectIDQuery(paymentID);
            if (!rs.next()) {
                throw new IllegalArgumentException("No such payment");
            }
            int orderID = rs.getInt("OrderID");
            Date paymentDate = rs.getDate("PaymentDate");
            String paymentMethod = rs.getString("PaymentMethod");
            BigDecimal changeAmount = rs.getBigDecimal("ChangeAmount");
            BigDecimal amount = rs.getBigDecimal("Amount");
            payment = new Payment(amount, changeAmount, PaymentMethod.valueOf(paymentMethod), orderID);
            payment.setDate(paymentDate);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        poolFlush.flush(paymentID, payment);
        return payment;
    }

    @Override
    public ArrayList<Payment> getPaymentByOrder(int orderID) {
        ArrayList<Payment> payments = new ArrayList<>();
        try {
            ResultSet rs = selectByOrderID.executeQuery();
            conn.getConnection().commit();
            while (rs.next()) {
                int paymentID = rs.getInt("PaymentID");
                Date paymentDate = rs.getDate("PaymentDate");
                String paymentMethod = rs.getString("PaymentMethod");
                BigDecimal changeAmount = rs.getBigDecimal("ChangeAmount");
                BigDecimal amount = rs.getBigDecimal("Amount");
                Payment payment = new Payment(amount, changeAmount, PaymentMethod.valueOf(paymentMethod), orderID);
                payment.setDate(paymentDate);
                payments.add(payment);
                poolFlush.flush(paymentID, payment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }

    @Override
    public ArrayList<Payment> getPayments(int begin, int end) {
        ArrayList<Payment> payments = new ArrayList<>();
        try {
            ResultSet rs = conn.selectRangeQuery(end - begin, begin);
            getPayment(payments, rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }

    private void getPayment(ArrayList<Payment> payments, ResultSet rs) throws SQLException {
        while (rs.next()) {
            int paymentID = rs.getInt("PaymentID");
            int orderID = rs.getInt("OrderID");
            Date paymentDate = rs.getDate("PaymentDate");
            String paymentMethod = rs.getString("PaymentMethod");
            BigDecimal changeAmount = rs.getBigDecimal("ChangeAmount");
            BigDecimal amount = rs.getBigDecimal("Amount");
            Payment payment = new Payment(amount, changeAmount, PaymentMethod.valueOf(paymentMethod), orderID);
            payment.setDate(paymentDate);
            payments.add(payment);
            poolFlush.flush(paymentID, payment);
        }
    }

    @Override
    public ArrayList<Payment> getPayments(Date begin, Date end) {
        ArrayList<Payment> payments = new ArrayList<>();
        try {
            ResultSet rs = selectByDate.executeQuery();
            conn.getConnection().commit();
            getPayment(payments, rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }

    @Override
    public void addPayment(Payment payment) throws IllegalArgumentException{
        try {
            insert.setInt(1, payment.getOrderID());
            insert.setDate(2, new java.sql.Date(payment.getDate().getTime()));
            insert.setString(3, payment.getMethod().toString());
            insert.setBigDecimal(4, payment.getChangeAmount());
            insert.setBigDecimal(5, payment.getAmount());
            insert.executeUpdate();
            conn.getConnection().commit();
            ResultSet rs = lastID.executeQuery();
            if (rs.next()) {
                int paymentID = rs.getInt("PaymentID");
                payment.setID(paymentID);
                poolFlush.flush(paymentID, payment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.getConnection().rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw new IllegalArgumentException("Payment add failed");
        }
    }

    @Override
    public int count() {
        return conn.selectCountQuery();
    }

    @Override
    public void setPoolFlush(PoolFlush<Payment> poolFlush) {
        this.poolFlush = poolFlush;
    }

    @Override
    public void close() {
        conn.close();
    }
}
