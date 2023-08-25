package pos.modules.pay;

import pos.auth.PermissionImpl;
import pos.entities.Payment;
import pos.modules.pool.PoolFlush;
import pos.modules.pool.ResourcePool;
import pos.modules.pool.ResourcePoolGet;
import pos.user.Role;
import pos.user.User;

import java.util.ArrayList;
import java.util.Date;

public class PaymentManagerImpl implements PaymentManager {

    private PaymentDbEnd paymentDbEnd;
    private ResourcePool<Payment> paymentFactory;

    private static PaymentManagerImpl instance;

    public static PaymentManagerImpl Instance() {
        return PaymentManagerImpl.InstanceHolder.INSTANCE.getInstance();
    }

    private enum InstanceHolder {
        INSTANCE();
        private PaymentManagerImpl instance;

        InstanceHolder() {
            instance = new PaymentManagerImpl();
        }

        public PaymentManagerImpl getInstance() {
            return instance;
        }
    }

    private PaymentManagerImpl() {
        paymentDbEnd = new PaymentDbEndImpl();
        paymentFactory = new ResourcePool<>(100, new ResourcePoolGet<Payment>() {
            @Override
            public Payment get(int ID) {
                return paymentDbEnd.getPayment(ID);
            }
        });
        paymentDbEnd.setPoolFlush(new PoolFlush<Payment>() {
            @Override
            public void flush(int ID, Payment payment) {
                paymentFactory.flush(payment.getID(), payment);
            }
        });
    }

    @Override
    public Payment getPayment(User user, int paymentID) throws IllegalArgumentException{
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.Finance))
            throw new IllegalArgumentException("No permission to access payment manager.");
        return paymentDbEnd.getPayment(paymentID);
    }

    @Override
    public ArrayList<Payment> getPaymentByOrder(User user, int orderID) throws IllegalArgumentException{
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.Finance))
            throw new IllegalArgumentException("No permission to access payment manager.");
        return paymentDbEnd.getPaymentByOrder(orderID);
    }

    @Override
    public ArrayList<Payment> getPayments(User user, int begin, int end) throws IllegalArgumentException{
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.Finance))
            throw new IllegalArgumentException("No permission to access payment manager.");
        return paymentDbEnd.getPayments(begin, end);
    }

    @Override
    public ArrayList<Payment> getPayments(User user, Date begin, Date end) throws IllegalArgumentException{
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.Finance))
            throw new IllegalArgumentException("No permission to access payment manager.");
        return paymentDbEnd.getPayments(begin, end);
    }

    @Override
    public void addPayment(User user, Payment payment) throws IllegalArgumentException{
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.Finance))
            throw new IllegalArgumentException("No permission to access payment manager.");
        paymentDbEnd.addPayment(payment);
    }


    @Override
    public void close(User user) throws IllegalArgumentException{
        if (!PermissionImpl.Instance().checkPermission(user, Role.Finance))
            throw new IllegalArgumentException("No permission to access payment manager.");
        paymentDbEnd.close();
    }
}
