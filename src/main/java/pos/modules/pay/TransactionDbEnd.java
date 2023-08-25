package pos.modules.pay;

import pos.entities.TransactionLog;
import pos.modules.pool.PoolFlush;

import java.util.ArrayList;
import java.util.Date;

public interface TransactionDbEnd {
    TransactionLog getTransactionLog(int transactionID);
    TransactionLog getTransactionLogByPayment(int paymentID);
    ArrayList<TransactionLog> getTransactionLogs(int begin, int end);
    ArrayList<TransactionLog> getTransactionLogsByCustomer(int customerID);
    ArrayList<TransactionLog> getTransactionLogsBySalesPerson(int salesPersonID);
    ArrayList<TransactionLog> getTransactionLogs(Date begin, Date end);
    void addTransactionLog(TransactionLog transactionLog);
    int getCount();
    void setPoolFlush(PoolFlush<TransactionLog> poolFlush);
    void close();
}
