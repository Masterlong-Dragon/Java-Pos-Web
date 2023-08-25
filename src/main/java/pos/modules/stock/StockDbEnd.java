package pos.modules.stock;

import pos.entities.Product;
import pos.modules.pool.PoolFlush;

import java.util.ArrayList;

public interface StockDbEnd {
    Product getProduct(int productID);
    Product getProduct(String name);
    ArrayList<Product> getProducts(int begin, int end);
    ArrayList<Product> getProducts(String name);
    void addProduct(Product product);
    void removeProduct(int productID);
    void updateProduct(Product product);
    int getCount();
    void close();
    void setPoolFlush(PoolFlush<Product> poolFlush);
}
