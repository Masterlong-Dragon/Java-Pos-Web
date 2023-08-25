package pos.modules.stock;

import pos.auth.PermissionImpl;
import pos.entities.Product;
import pos.entities.TransactionType;
import pos.modules.pool.PoolFlush;
import pos.modules.pool.ResourcePool;
import pos.modules.pool.ResourcePoolGet;
import pos.modules.printer.ReceiptPrinterImpl;
import pos.user.Role;
import pos.user.User;

import java.util.ArrayList;

public class StockManagerImpl implements StockManager {

    private ResourcePool<Product> productFactory;
    private StockDbEnd stockDbEnd;

    public static StockManagerImpl Instance() {
        return StockManagerImpl.InstanceHolder.INSTANCE.getInstance();
    }

    private enum InstanceHolder {
        INSTANCE();
        private StockManagerImpl instance;

        InstanceHolder() {
            instance = new StockManagerImpl();
        }

        public StockManagerImpl getInstance() {
            return instance;
        }
    }

    public StockManagerImpl() {
        stockDbEnd = new StockDbEndImpl();
        productFactory = new ResourcePool<>(100, new ResourcePoolGet<Product>() {
            @Override
            public Product get(int ID) {
                return stockDbEnd.getProduct(ID);
            }
        });
        stockDbEnd.setPoolFlush(new PoolFlush<Product>() {
            @Override
            public void flush(int ID, Product product) {
                productFactory.flush(product.getProductID(), product);
            }
        });
    }

    public int getCount(User user) throws IllegalArgumentException{
        if (PermissionImpl.Instance().checkPermission(user, Role.Finance))
            throw new IllegalArgumentException("No permission to access stock manager.");
        return stockDbEnd.getCount();
    }


    @Override
    public Product showProduct(User user, int productID) throws IllegalArgumentException{
        if (PermissionImpl.Instance().checkPermission(user, Role.Finance))
            throw new IllegalArgumentException("No permission to access stock manager.");
        Product product = productFactory.get(productID);
        return product;
    }

    @Override
    public Product showProduct(User user, String name)throws IllegalArgumentException {
        if (PermissionImpl.Instance().checkPermission(user, Role.Finance))
            throw new IllegalArgumentException("No permission to access stock manager.");
        Product product = stockDbEnd.getProduct(name);
        return product;
    }

    @Override
    public Product getProduct(User user, int productID, int quantity) throws IllegalArgumentException {
        if (PermissionImpl.Instance().checkPermission(user, Role.SalesPerson, Role.Finance))
            throw new IllegalArgumentException("No permission to access stock manager.");
        Product product = showProduct(user, productID);
        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("Not enough stock.");
        }
        return product;
    }

    @Override
    public ArrayList<Product> getProducts(User user, int begin, int end) throws IllegalArgumentException{
        if (PermissionImpl.Instance().checkPermission(user, Role.SalesPerson, Role.Finance))
            throw new IllegalArgumentException("No permission to access stock manager.");
        ArrayList<Product> list = stockDbEnd.getProducts(begin, end);
        return list;
    }

    @Override
    public ArrayList<Product> getProducts(User user, String name) throws IllegalArgumentException{
        if (PermissionImpl.Instance().checkPermission(user, Role.SalesPerson, Role.Finance))
            throw new IllegalArgumentException("No permission to access stock manager.");
        ArrayList<Product> list = stockDbEnd.getProducts(name);
        return list;
    }

    @Override
    public void addProduct(User user, Product product) throws IllegalArgumentException {
        if (PermissionImpl.Instance().checkPermission(user, Role.SalesPerson, Role.Finance))
            throw new IllegalArgumentException("No permission to add product");
        stockDbEnd.addProduct(product);
    }


    @Override
    public void removeProduct(User user, int productID) throws IllegalArgumentException{
        if (PermissionImpl.Instance().checkPermission(user, Role.SalesPerson, Role.Finance))
            throw new IllegalArgumentException("No permission to remove product");
        // 检查是否存在
        if (showProduct(user, productID) == null)
            throw new IllegalArgumentException("Product does not exists.");
        // 从数据库中删除
        stockDbEnd.removeProduct(productID);
        // 更新对象池中的对象
        if (productFactory.contains(productID))
            productFactory.remove(productID);
    }

    private void innerUpdateProduct(Product product) throws IllegalArgumentException {
        stockDbEnd.updateProduct(product);
        // 更新对象池中的对象
        if (productFactory.contains(product.getProductID()))
            productFactory.update(product.getProductID(), product);
    }

    @Override
    public void updateProduct(User user, Product product) throws IllegalArgumentException{
        if (PermissionImpl.Instance().checkPermission(user, Role.SalesPerson, Role.Finance))
            throw new IllegalArgumentException("No permission to update product");
        innerUpdateProduct(product);
    }

    // 售货员只能更新库存数量
    @Override
    public void updateStock(User user, int productID, int quantity, TransactionType transactionType) throws IllegalArgumentException{
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin, Role.SalesPerson, Role.StockClerk))
            throw new IllegalArgumentException("No permission to access stock manager.");
        // 更新对象池中的对象
        Product product = showProduct(user, productID);
        int stock = product.getStock();
        if (productFactory.contains(productID))
            product.setStock(transactionType == TransactionType.Sale ? stock - quantity : stock + quantity);
        innerUpdateProduct(product);
    }

    @Override
    public void close(User user) throws IllegalArgumentException{
        if (!PermissionImpl.Instance().checkPermission(user, Role.Admin))
            throw new IllegalArgumentException("No permission to access stock manager.");
        stockDbEnd.close();
    }

}
