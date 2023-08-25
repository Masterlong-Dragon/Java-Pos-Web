package pos.modules.printer;

import pos.entities.Order;
import pos.entities.TransactionLog;

public interface ReceiptPrinter {
    void printReceipt(Order order, TransactionLog transactionLog);
}
