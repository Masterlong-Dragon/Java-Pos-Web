package pos.modules.pay;

import pos.entities.Payment;
import pos.modules.pool.PoolFlush;
import pos.user.User;

import java.util.ArrayList;
import java.util.Date;

public interface PaymentManager {
    void addPayment(User user, Payment payment);
    Payment getPayment(User user, int paymentID);
    ArrayList<Payment> getPaymentByOrder(User user, int orderID);
    ArrayList<Payment> getPayments(User user, int begin, int end);
    ArrayList<Payment> getPayments(User user, Date begin, Date end);
    void close(User user);
}
