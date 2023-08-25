package pos.modules.pay;

import pos.entities.TransactionLog;
import pos.modules.pool.PoolFlush;
import pos.user.User;

import java.util.ArrayList;
import java.util.Date;

public interface TransactionManager {
    TransactionLog getTransactionLog(User user, int transactionID);
    TransactionLog getTransactionLogByPayment(User user, int paymentID);
    ArrayList<TransactionLog> getTransactionLogs(User user, int begin, int end);
    ArrayList<TransactionLog> getTransactionLogsByCustomer(User user, int customerID);
    ArrayList<TransactionLog> getTransactionLogsBySalesPerson(User user, int salesPersonID);
    ArrayList<TransactionLog> getTransactionLogs(User user, Date begin, Date end);
    void addTransactionLog(User user, TransactionLog transactionLog);
    int getCount(User user);
    void close(User user);
}
