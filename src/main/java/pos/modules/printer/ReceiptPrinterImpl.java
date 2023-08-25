package pos.modules.printer;

import pos.entities.Order;
import pos.entities.OrderDetail;
import pos.entities.Payment;
import pos.entities.TransactionLog;

public class ReceiptPrinterImpl implements ReceiptPrinter {

    public static ReceiptPrinterImpl Instance() {
        return InstanceHolder.INSTANCE.getInstance();
    }

    private enum InstanceHolder {
        INSTANCE();
        private ReceiptPrinterImpl instance;

        InstanceHolder() {
            instance = new ReceiptPrinterImpl();
        }

        public ReceiptPrinterImpl getInstance() {
            return instance;
        }
    }

    public void printReceipt(Order order, TransactionLog transactionLog) {
        Payment payment = transactionLog.getPayment();
        for (OrderDetail orderDetail :
                order.getDetail()) {
            System.out.println(orderDetail.toString());
        }
        System.out.println("Total: " + order.getTotalAmount());
        System.out.println("Paid: " + payment.getAmount());
        System.out.println("Change: " + payment.getChangeAmount());
        System.out.println("Payment method: " + payment.getMethod());
        System.out.println("Date: " + payment.getDate());
        System.out.println("Sales person: " + transactionLog.getSalesPerson().getName());
        System.out.println("Customer: " + order.getCustomer().getName());
    }
}
