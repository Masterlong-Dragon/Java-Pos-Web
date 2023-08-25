package pos.modules.sale;

import pos.entities.Order;
import pos.entities.PaymentMethod;
import pos.entities.individuals.Individual;
import pos.modules.order.OrderManager;
import pos.modules.stock.StockManager;
import pos.user.User;

import java.math.BigDecimal;

public interface Sale {
    void start(User user, Individual salesPerson, Order order);// 开始新的销售
    void addItem(User user, int productID, int quantity);// 添加商品
    void updateItem(User user, int productID, int quantity);// 更新商品
    void removeItem(User user, int productID, int quantity);// 移除商品
    void removeItem(User user, int productID);// 移除商品
    void submit(User user);// 提交销售
    BigDecimal getChange(User user, BigDecimal cash);// 获取找零
    Order getOrder(User user);// 获取订单
    void createPayment(User user, BigDecimal cash, PaymentMethod paymentMethod);// 创建支付
    void submitPayment(User user);// 提交支付
    void printReceipt(User user);// 打印小票
    void close(User user);
}
