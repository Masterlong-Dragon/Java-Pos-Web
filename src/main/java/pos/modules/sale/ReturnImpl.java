package pos.modules.sale;

import pos.auth.PermissionImpl;
import pos.config.ConfigManager;
import pos.config.ConfigManagerImpl;
import pos.db.DBConnection;
import pos.entities.*;
import pos.entities.individuals.Individual;
import pos.modules.individuals.CustomerManager;
import pos.modules.individuals.SalesPersonManager;
import pos.modules.order.OrderManager;
import pos.modules.order.OrderManagerImpl;
import pos.modules.pay.PaymentManager;
import pos.modules.pay.PaymentManagerImpl;
import pos.modules.pay.TransactionManager;
import pos.modules.pay.TransactionManagerImpl;
import pos.modules.printer.ReceiptPrinter;
import pos.modules.printer.ReceiptPrinterImpl;
import pos.modules.stock.StockManager;
import pos.modules.stock.StockManagerImpl;
import pos.user.Role;
import pos.user.User;

import java.math.BigDecimal;
import java.util.Date;

public class ReturnImpl implements Sale {

    private Individual salesPerson;
    private Order currentOrder;
    private Order currentReturnOrder;
    private Payment currentPayment;
    private TransactionLog currentTransactionLog;
    private StockManager stockManager;
    private CustomerManager customerManager;
    private SalesPersonManager salesPersonManager;
    private PaymentManager paymentManager;
    private TransactionManager transactionManager;
    private ReceiptPrinter receiptPrinter;
    private ConfigManager configManager;
    private DBConnection conn;

    public static ReturnImpl Instance() {
        return ReturnImpl.InstanceHolder.INSTANCE.getInstance();
    }

    private enum InstanceHolder {
        INSTANCE();
        private ReturnImpl instance;

        InstanceHolder() {
            instance = new ReturnImpl();
        }

        public ReturnImpl getInstance() {
            return instance;
        }
    }

    public ReturnImpl(){
        receiptPrinter = ReceiptPrinterImpl.Instance();
        configManager = ConfigManagerImpl.Instance();
        conn = new DBConnection(configManager.getDBURL(),
                configManager.getDBUser(Role.SalesPerson),
                configManager.getDBPassword(Role.SalesPerson));
        stockManager = StockManagerImpl.Instance();
        salesPersonManager = SalesPersonManager.Instance();
        paymentManager = PaymentManagerImpl.Instance();
        transactionManager = TransactionManagerImpl.Instance();
        customerManager = CustomerManager.Instance();
    }

    @Override
    public void start(User user, Individual salesPerson, Order order) throws IllegalArgumentException{
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.SalesPerson))
            throw new IllegalArgumentException("No permission");
        this.salesPerson = salesPerson;
        currentOrder = order;
        currentReturnOrder = new Order(currentOrder.getCustomer());
    }

    @Override
    public void addItem(User user, int productID, int quantity) throws IllegalArgumentException{
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.SalesPerson))
            throw new IllegalArgumentException("No permission");
        if (currentOrder.getDetail(productID).getQuantity() < quantity)
            throw new IllegalArgumentException("Too much");
        currentReturnOrder.addDetail(stockManager.showProduct(user, productID), quantity);
        currentOrder.removeDetail(productID, quantity);
    }

    @Override
    public void updateItem(User user, int productID, int quantity) {
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.SalesPerson))
            throw new IllegalArgumentException("No permission");
        if (currentOrder.getDetail(productID).getQuantity() < quantity)
            throw new IllegalArgumentException("Too much");
        currentReturnOrder.updateDetail(productID, quantity);
        currentOrder.updateDetail(productID, quantity);
    }

    @Override
    public void removeItem(User user, int productID, int quantity) throws IllegalArgumentException{
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.SalesPerson))
            throw new IllegalArgumentException("No permission");
        if (currentReturnOrder.getDetail(productID).getQuantity() < quantity)
            throw new IllegalArgumentException("Too much");
        currentReturnOrder.removeDetail(productID);
        currentOrder.addDetail(stockManager.showProduct(user, productID), quantity);
    }

    @Override
    public void removeItem(User user, int productID) {
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.SalesPerson))
            throw new IllegalArgumentException("No permission");
        currentReturnOrder.removeDetail(productID);
        currentOrder.addDetail(stockManager.showProduct(user, productID), currentOrder.getDetail(productID).getQuantity());
    }

    @Override
    public void submit(User user) throws IllegalArgumentException{
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.SalesPerson))
            throw new IllegalArgumentException("No permission");
        // 更新库存数据
        for (OrderDetail orderDetail : currentReturnOrder.getDetail()) {
            Product product = orderDetail.getProduct();
            stockManager.updateStock(user, product.getProductID(), orderDetail.getQuantity(), TransactionType.Return);
        }
        OrderManagerImpl.Instance().updateOrder(user, currentOrder);
    }

    @Override
    public BigDecimal getChange(User user, BigDecimal cash) throws IllegalArgumentException{
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.SalesPerson))
            throw new IllegalArgumentException("No permission");
        return cash.subtract(currentReturnOrder.getTotalAmount()).multiply(BigDecimal.valueOf(-1));
    }

    @Override
    public Order getOrder(User user) throws IllegalArgumentException{
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.SalesPerson))
            throw new IllegalArgumentException("No permission");
        return currentReturnOrder;
    }

    @Override
    public void createPayment(User user, BigDecimal cash, PaymentMethod paymentMethod) throws IllegalArgumentException{
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.SalesPerson))
            throw new IllegalArgumentException("No permission");
        currentPayment = new Payment(cash.multiply(BigDecimal.valueOf(-1)), getChange(user, cash), paymentMethod, currentOrder.getID());
    }

    @Override
    public void submitPayment(User user) throws IllegalArgumentException{
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.SalesPerson))
            throw new IllegalArgumentException("No permission");
        currentPayment.setDate(new Date());
        // paymentManager.addPayment(user, currentPayment);
        currentTransactionLog = new TransactionLog(currentOrder, currentPayment, salesPerson, TransactionType.Return);
        transactionManager.addTransactionLog(user, currentTransactionLog);
    }

    @Override
    public void printReceipt(User user) throws IllegalArgumentException{
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.SalesPerson))
            throw new IllegalArgumentException("No permission");
        receiptPrinter.printReceipt(currentReturnOrder, currentTransactionLog);
    }

    @Override
    public void close(User user) throws IllegalArgumentException{
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin))
            throw new IllegalArgumentException("No permission");
        conn.close();
    }
}
