package pos.modules.pay;

import pos.auth.PermissionImpl;
import pos.entities.TransactionLog;
import pos.modules.pool.PoolFlush;
import pos.modules.pool.ResourcePool;
import pos.modules.pool.ResourcePoolGet;
import pos.user.Role;
import pos.user.User;

import java.util.ArrayList;
import java.util.Date;

public class TransactionManagerImpl implements TransactionManager {


    private static TransactionManagerImpl instance;

    public static TransactionManagerImpl Instance() {
        return TransactionManagerImpl.InstanceHolder.INSTANCE.getInstance();
    }


    private enum InstanceHolder {
        INSTANCE();
        private TransactionManagerImpl instance;

        InstanceHolder() {
            instance = new TransactionManagerImpl();
        }

        public TransactionManagerImpl getInstance() {
            return instance;
        }
    }

    private TransactionDbEnd transactionDbEnd;
    private ResourcePool<TransactionLog> transactionFactory;

    private TransactionManagerImpl() {
        transactionDbEnd = new TransactionDbEndImpl();
        transactionFactory = new ResourcePool<>(100, new ResourcePoolGet<TransactionLog>() {
            @Override
            public TransactionLog get(int ID) {
                return transactionDbEnd.getTransactionLog(ID);
            }
        });
        transactionDbEnd.setPoolFlush(new PoolFlush<TransactionLog>() {
            @Override
            public void flush(int ID, TransactionLog object) {
                transactionFactory.flush(object.getID(), object);
            }
        });
    }


    @Override
    public TransactionLog getTransactionLog(User user, int transactionID) throws IllegalArgumentException{
        if(!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.Finance))
            throw new IllegalArgumentException("No permission to access transaction manager.");
        return transactionDbEnd.getTransactionLog(transactionID);
    }

    @Override
    public TransactionLog getTransactionLogByPayment(User user, int paymentID) throws IllegalArgumentException{
        if(!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.Finance))
            throw new IllegalArgumentException("No permission to access transaction manager.");
        return transactionDbEnd.getTransactionLogByPayment(paymentID);
    }

    @Override
    public ArrayList<TransactionLog> getTransactionLogs(User user, int begin, int end) throws IllegalArgumentException{
        if(!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.Finance))
            throw new IllegalArgumentException("No permission to access transaction manager.");
        return transactionDbEnd.getTransactionLogs(begin, end);
    }

    @Override
    public ArrayList<TransactionLog> getTransactionLogsByCustomer(User user, int customerID) throws IllegalArgumentException{
        if(!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.Finance))
            throw new IllegalArgumentException("No permission to access transaction manager.");
        return transactionDbEnd.getTransactionLogsByCustomer(customerID);
    }

    @Override
    public ArrayList<TransactionLog> getTransactionLogsBySalesPerson(User user, int salesPersonID) throws IllegalArgumentException{
        if(!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.Finance))
            throw new IllegalArgumentException("No permission to access transaction manager.");
        return transactionDbEnd.getTransactionLogsBySalesPerson(salesPersonID);
    }

    @Override
    public ArrayList<TransactionLog> getTransactionLogs(User user, Date begin, Date end) throws IllegalArgumentException{
        if(!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.Finance))
            throw new IllegalArgumentException("No permission to access transaction manager.");
        return transactionDbEnd.getTransactionLogs(begin, end);
    }

    @Override
    public void addTransactionLog(User user, TransactionLog transactionLog) throws IllegalArgumentException{
        if(!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.SalesPerson))
            throw new IllegalArgumentException("No permission to access transaction manager.");
        transactionDbEnd.addTransactionLog(transactionLog);
    }

    @Override
    public int getCount(User user) throws IllegalArgumentException{
        if(!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.Finance))
            throw new IllegalArgumentException("No permission to access transaction manager.");
        return transactionDbEnd.getCount();
    }

    @Override
    public void close(User user) throws IllegalArgumentException{
        if(!PermissionImpl.Instance().checkPermission(user, Role.Admin))
            throw new IllegalArgumentException("No permission to access transaction manager.");
        transactionDbEnd.close();
    }
}
