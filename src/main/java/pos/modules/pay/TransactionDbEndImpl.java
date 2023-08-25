package pos.modules.pay;

import pos.config.ConfigManager;
import pos.config.ConfigManagerImpl;
import pos.db.DBConnection;
import pos.entities.Payment;
import pos.entities.TransactionLog;
import pos.entities.TransactionType;
import pos.entities.individuals.Individual;
import pos.modules.individuals.CustomerManager;
import pos.modules.pool.PoolFlush;
import pos.modules.pool.ResourcePool;
import pos.modules.pool.ResourcePoolGet;
import pos.user.Role;
import pos.user.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class TransactionDbEndImpl implements TransactionDbEnd {

    private final DBConnection conn;
    private final ConfigManager configManager;
    private final PreparedStatement selectByPaymentID;
    private final PreparedStatement selectByCustomerID;
    private final PreparedStatement selectBySalesPersonID;
    private final PreparedStatement selectByDate;
    private final PreparedStatement insert;

    private PoolFlush<TransactionLog> poolFlush;
    private ResourcePool<TransactionLog> transactionLogFactory;
    private PaymentManager paymentManager;
    private User user;

    public TransactionDbEndImpl() {
        poolFlush = new PoolFlush<>() {
            @Override
            public void flush(int ID, TransactionLog object) {

            }
        };
        transactionLogFactory = new ResourcePool<>(100, new ResourcePoolGet<TransactionLog>() {
            @Override
            public TransactionLog get(int ID) {
                return getTransactionLog(ID);
            }
        });
        configManager = ConfigManagerImpl.Instance();
        paymentManager = PaymentManagerImpl.Instance();

        conn = new DBConnection(configManager.getDBURL(),
                configManager.getDBUser(Role.Admin),
                configManager.getDBPassword(Role.Admin));
        conn.setTableName("transactionlog");
        conn.setIDName("TransactionID");
        try {
            conn.getConnection().setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // 初始化预编译语句
        selectByPaymentID = conn.createPreparedStatement("SELECT * FROM pos.transactionlog WHERE PaymentID = ?");
        selectByCustomerID = conn.createPreparedStatement("SELECT * FROM pos.transactionlog WHERE CustomerID = ?");
        selectBySalesPersonID = conn.createPreparedStatement("SELECT * FROM pos.transactionlog WHERE SalesPersonID = ?");
        selectByDate = conn.createPreparedStatement("SELECT * FROM pos.transactionlog WHERE TransactionDate BETWEEN ? AND ?");
        insert = conn.createPreparedStatement("INSERT INTO pos.transactionlog (TransactionDate, CustomerID, SalesPersonID, TransactionType, PaymentID) VALUES (?, ?, ?, ?, ?)");

        user = new User(0, "root", Role.Admin, UUID.fromString("00000000-0000-0000-0000-000000000000"));
    }

    private TransactionLog getTransactionLog(ResultSet rs) {
        int transactionID = 0;
        int paymentID = 0;
        int customerID = 0;
        int SalesPersonID = 0;
        TransactionType transactionType = null;
        try {
            transactionID = rs.getInt("TransactionID");
            paymentID = rs.getInt("PaymentID");
            customerID = rs.getInt("CustomerID");
            SalesPersonID = rs.getInt("SalesPersonID");
            transactionType = TransactionType.valueOf(rs.getString("TransactionType"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Payment payment = paymentManager.getPayment(user, paymentID);
        Individual customer = CustomerManager.Instance().getIndividual(user, customerID);
        Individual salesPerson = CustomerManager.Instance().getIndividual(user, SalesPersonID);
        TransactionLog transactionLog = new TransactionLog(payment.getDate(), customer, salesPerson, transactionType, payment);
        transactionLog.setID(transactionID);
        poolFlush.flush(transactionID, transactionLog);
        return transactionLog;
    }


    @Override
    public TransactionLog getTransactionLog(int transactionID) {
        TransactionLog transactionLog = null;
        try {
            ResultSet rs = conn.selectIDQuery(transactionID);
            if (!rs.next()) {
                throw new IllegalArgumentException("No such transaction log");
            }
            transactionLog = getTransactionLog(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactionLog;
    }

    @Override
    public TransactionLog getTransactionLogByPayment(int paymentID) {
        TransactionLog transactionLog = null;
        try {
            selectByPaymentID.setInt(1, paymentID);
            ResultSet rs = selectByPaymentID.executeQuery();
            conn.getConnection().commit();
            if (!rs.next()) {
                throw new IllegalArgumentException("No such transaction log");
            }
            transactionLog = getTransactionLog(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactionLog;
    }

    private ArrayList<TransactionLog> getTransactionLogs(ResultSet rs) {
        ArrayList<TransactionLog> transactionLogs = new ArrayList<>();
        try {
            while (rs.next()) {
                transactionLogs.add(getTransactionLog(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactionLogs;
    }

    @Override
    public ArrayList<TransactionLog> getTransactionLogs(int begin, int end) {
        TransactionLog transactionLog = null;
        ArrayList<TransactionLog> transactionLogs = new ArrayList<>();
        ResultSet rs = conn.selectRangeQuery(end - begin, begin);
        return getTransactionLogs(rs);
    }

    @Override
    public ArrayList<TransactionLog> getTransactionLogsByCustomer(int customerID) {
        TransactionLog transactionLog = null;
        ResultSet rs = null;
        ArrayList<TransactionLog> transactionLogs = new ArrayList<>();
        try {
            selectByCustomerID.setInt(1, customerID);
            rs = selectByCustomerID.executeQuery();
            conn.getConnection().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getTransactionLogs(rs);
    }

    @Override
    public ArrayList<TransactionLog> getTransactionLogsBySalesPerson(int salesPersonID) {
        TransactionLog transactionLog = null;
        ResultSet rs = null;
        try {
            selectBySalesPersonID.setInt(1, salesPersonID);
            rs = selectBySalesPersonID.executeQuery();
            conn.getConnection().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getTransactionLogs(rs);
    }

    @Override
    public ArrayList<TransactionLog> getTransactionLogs(Date begin, Date end) {
        TransactionLog transactionLog = null;
        ResultSet rs = null;
        try {
            selectByDate.setDate(1, new java.sql.Date(begin.getTime()));
            selectByDate.setDate(2, new java.sql.Date(end.getTime()));
            rs = selectByDate.executeQuery();
            conn.getConnection().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getTransactionLogs(rs);
    }

    @Override
    public void addTransactionLog(TransactionLog transactionLog) {
        try {
            paymentManager.addPayment(user, transactionLog.getPayment());
            insert.setDate(1, new java.sql.Date(transactionLog.getDate().getTime()));
            insert.setInt(2, transactionLog.getCustomer().getID());
            insert.setInt(3, transactionLog.getSalesPerson().getID());
            insert.setString(4, transactionLog.getType().toString());
            insert.setInt(5, transactionLog.getPayment().getID());
            insert.executeUpdate();
            conn.getConnection().commit();
        } catch (Exception e) {
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
    public void setPoolFlush(PoolFlush<TransactionLog> poolFlush) {
        this.poolFlush = poolFlush;
    }

    @Override
    public void close() {
        conn.close();
    }
}
