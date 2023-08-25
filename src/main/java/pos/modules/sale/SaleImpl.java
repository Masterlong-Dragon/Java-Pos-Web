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

public class SaleImpl implements Sale {

    private Individual salesPerson;
    private Order currentOrder;
    private Payment currentPayment;
    private TransactionLog currentTransactionLog;
    private StockManager stockManager;
    private OrderManager orderManager;
    private PaymentManager paymentManager;
    private TransactionManager transactionManager;
    private CustomerManager customerManager;
    private SalesPersonManager salesPersonManager;
    private ReceiptPrinter receiptPrinter;
    private ConfigManager configManager;
    private DBConnection conn;
    private static SaleImpl instance;

    public static SaleImpl Instance() {
        return SaleImpl.InstanceHolder.INSTANCE.getInstance();
    }

    private enum InstanceHolder {
        INSTANCE();
        private SaleImpl instance;

        InstanceHolder() {
            instance = new SaleImpl();
        }

        public SaleImpl getInstance() {
            return instance;
        }
    }

    public SaleImpl() {
        receiptPrinter = ReceiptPrinterImpl.Instance();
        configManager = ConfigManagerImpl.Instance();
        conn = new DBConnection(configManager.getDBURL(),
                configManager.getDBUser(Role.SalesPerson),
                configManager.getDBPassword(Role.SalesPerson));
        stockManager = StockManagerImpl.Instance();
        salesPersonManager = SalesPersonManager.Instance();
        customerManager = CustomerManager.Instance();
        orderManager = OrderManagerImpl.Instance();
        paymentManager = PaymentManagerImpl.Instance();
        transactionManager = TransactionManagerImpl.Instance();
    }

    @Override
    public void start(User user, Individual salesPerson, Order order) throws IllegalArgumentException {
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.SalesPerson))
            throw new IllegalArgumentException("No permission");
        this.salesPerson = salesPerson;
        currentOrder = order;
    }

    @Override
    public BigDecimal getChange(User user, BigDecimal cash) throws IllegalArgumentException {
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.SalesPerson))
            throw new IllegalArgumentException("No permission");
        // 计算找零
        BigDecimal change = new BigDecimal(0);
        if (cash.compareTo(currentOrder.getTotalAmount()) >= 0) {
            change = cash.subtract(currentOrder.getTotalAmount());
        } else {
            throw new IllegalArgumentException("Cash is not enough.");
        }
        return change;
    }

    @Override
    public Order getOrder(User user) throws IllegalArgumentException {
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.SalesPerson))
            throw new IllegalArgumentException("No permission");
        return currentOrder;
    }

    @Override
    public void createPayment(User user, BigDecimal cash, PaymentMethod paymentMethod) throws IllegalArgumentException {
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.SalesPerson))
            throw new IllegalArgumentException("No permission");
        currentPayment = new Payment(cash, getChange(user, cash), paymentMethod, currentOrder.getID());
    }

    @Override
    public void submitPayment(User user) throws IllegalArgumentException {
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.SalesPerson))
            throw new IllegalArgumentException("No permission");
        // 假设已经提交
        currentPayment.setDate(new Date());
        currentTransactionLog = new TransactionLog(currentOrder, currentPayment, salesPerson, TransactionType.Sale);
        // 保存支付信息
        // paymentManager.addPayment(user, currentPayment);
        // 保存交易信息
        transactionManager.addTransactionLog(user, currentTransactionLog);
    }

    @Override
    public void submit(User user) throws IllegalArgumentException {
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.SalesPerson))
            throw new IllegalArgumentException("No permission");
        // 更新库存数据
        for (OrderDetail orderDetail : currentOrder.getDetail()) {
            Product product = orderDetail.getProduct();
            stockManager.updateStock(user, product.getProductID(), orderDetail.getQuantity(), TransactionType.Sale);
        }
        orderManager.addOrder(user, currentOrder);
    }

    @Override
    public void printReceipt(User user) throws IllegalArgumentException {
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.SalesPerson))
            throw new IllegalArgumentException("No permission");
        // 打印小票
        receiptPrinter.printReceipt(currentOrder, currentTransactionLog);
    }


    @Override
    public void close(User user) throws IllegalArgumentException {
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin))
            throw new IllegalArgumentException("No permission");
        // 关闭数据库连接
        conn.close();
    }

    @Override
    public void addItem(User user, int productID, int quantity) throws IllegalArgumentException {
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.SalesPerson))
            throw new IllegalArgumentException("No permission");
        currentOrder.addDetail(stockManager.getProduct(user, productID, quantity), quantity);
    }

    @Override
    public void updateItem(User user, int productID, int quantity) {
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.SalesPerson))
            throw new IllegalArgumentException("No permission");
        currentOrder.updateDetail(productID, quantity);
    }

    @Override
    public void removeItem(User user, int productID, int quantity) throws IllegalArgumentException {
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.SalesPerson))
            throw new IllegalArgumentException("No permission");
        currentOrder.removeDetail(productID, quantity);
    }

    @Override
    public void removeItem(User user, int productID) {
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.SalesPerson))
            throw new IllegalArgumentException("No permission");
        currentOrder.removeDetail(productID);
    }

    public void setSalePerson(User user, Individual salesPerson) throws IllegalArgumentException {
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.SalesPerson))
            throw new IllegalArgumentException("No permission");
        this.salesPerson = salesPerson;
    }
}
