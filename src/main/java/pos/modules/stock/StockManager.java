package pos.modules.stock;

import pos.entities.PaymentMethod;
import pos.entities.Product;
import pos.entities.TransactionType;
import pos.user.User;

import java.util.ArrayList;

public interface StockManager {
    Product showProduct(User user, int productID);
    Product showProduct(User user, String name);
    Product getProduct(User user, int productID, int quantity);
    ArrayList<Product> getProducts(User user, int begin, int end);
    ArrayList<Product> getProducts(User user, String name);
    void addProduct(User user, Product product);
    void removeProduct(User user, int productID);
    void updateProduct(User user, Product product);
    void updateStock(User user, int productID, int quantity, TransactionType transactionType);
    int getCount(User user);
    void close(User user);
}
