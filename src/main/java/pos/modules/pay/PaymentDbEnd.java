package pos.modules.pay;

import pos.entities.Payment;
import pos.modules.pool.PoolFlush;

import java.util.ArrayList;
import java.util.Date;

public interface PaymentDbEnd {
    Payment getPayment(int paymentID);
    ArrayList<Payment> getPaymentByOrder(int orderID);
    ArrayList<Payment> getPayments(int begin, int end);
    ArrayList<Payment> getPayments(Date begin, Date end);
    void addPayment(Payment payment);
    void setPoolFlush(PoolFlush<Payment> poolFlush);
    int count();
    void close();
}
