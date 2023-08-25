package pos.modules.stock;

import pos.config.ConfigManager;
import pos.config.ConfigManagerImpl;
import pos.db.DBConnection;
import pos.entities.Product;
import pos.modules.pool.PoolFlush;
import pos.user.Role;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class StockDbEndImpl implements StockDbEnd {

    private final DBConnection conn;
    private final PreparedStatement insert;
    private final PreparedStatement update;
    private final PreparedStatement delete;
    private PoolFlush<Product> poolFlush;

    public StockDbEndImpl() {
        poolFlush = new PoolFlush<>() {
            @Override
            public void flush(int ID, Product object) {

            }
        };
        ConfigManager configManager = ConfigManagerImpl.Instance();
        conn = new DBConnection(configManager.getDBURL(),
                configManager.getDBUser(Role.StockClerk),
                configManager.getDBPassword(Role.StockClerk));
        // 初始化预编译语句
        conn.setTableName("products");
        conn.setIDName("ProductID");
        conn.setLabelName("Name");
        insert = conn.createPreparedStatement("INSERT INTO pos.products (Name, Price, Stock) VALUES (?, ?, ?)");
        update = conn.createPreparedStatement("UPDATE pos.products SET Name = ?, Price = ?, Stock = ? WHERE ProductID = ?");
        delete = conn.createPreparedStatement("DELETE FROM pos.products WHERE ProductID = ?");
    }

    @Override
    public Product getProduct(int productID) throws IllegalArgumentException {
        Product product = null;
        try {
            // 结果不为空
            ResultSet rs = conn.selectIDQuery(productID);
            if (!rs.next())
                throw new IllegalArgumentException("Product not found.");
            product = new Product(rs.getInt("ProductID"),
                    rs.getString("Name"),
                    rs.getBigDecimal("Price"),
                    rs.getInt("Stock"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }

    @Override
    public Product getProduct(String name) throws IllegalArgumentException {
        Product product = null;
        try {
            // 结果不为空
            ResultSet rs = conn.selectStringQuery(name);
            if (!rs.next())
                throw new IllegalArgumentException("Product not found.");
            product = new Product(rs.getInt("ProductID"),
                    rs.getString("Name"),
                    rs.getBigDecimal("Price"),
                    rs.getInt("Stock"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        poolFlush.flush(product.getProductID(), product);
        return product;
    }

    @Override
    public ArrayList<Product> getProducts(int begin, int end) {
        ArrayList<Product> products = new ArrayList<>();
        // 查询products表
        try {
            // 结果不为空
            ResultSet rs = conn.selectRangeQuery(end - begin, begin);
            while (rs.next()) {
                Product product = new Product(rs.getInt("ProductID"),
                        rs.getString("Name"),
                        rs.getBigDecimal("Price"),
                        rs.getInt("Stock"));
                poolFlush.flush(product.getProductID(), product);
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    @Override
    public ArrayList<Product> getProducts(String name) {
        ArrayList<Product> products = new ArrayList<>();
        // 查询products表
        try {
            // 结果不为空
            ResultSet rs = conn.selectStringAlikeQuery(name);
            while (rs.next()) {
                Product product = new Product(rs.getInt("ProductID"),
                        rs.getString("Name"),
                        rs.getBigDecimal("Price"),
                        rs.getInt("Stock"));
                poolFlush.flush(product.getProductID(), product);
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    @Override
    public void addProduct(Product product) {
        // 检查是否存在
        // 匹配名称
        try {
            ResultSet rs = conn.selectStringQuery(product.getName());
            if (rs.next())
                throw new IllegalArgumentException("Product already exists.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // 插入数据库
        try {
            insert.setString(1, product.getName());
            insert.setBigDecimal(2, product.getPrice());
            insert.setInt(3, product.getStock());
            insert.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeProduct(int productID) {
        try {
            delete.setInt(1, productID);
            delete.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateProduct(Product product) {
        // 检查是否存在
        if (getProduct(product.getProductID()) == null)
            throw new IllegalArgumentException("Product does not exists.");
        // 不允许更新重名
        try {
            ResultSet rs = conn.selectStringQuery(product.getName());
            if (rs.next() && rs.getInt("ProductID") != product.getProductID())
                throw new IllegalArgumentException("Product duplicates.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // 检查价格是否为正 检查库存是否为正
        if (product.getPrice().compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Price must be positive.");
        if (product.getStock() < 0)
            throw new IllegalArgumentException("Stock must be positive.");
        // 更新数据库
        try {
            update.setString(1, product.getName());
            update.setBigDecimal(2, product.getPrice());
            update.setInt(3, product.getStock());
            update.setInt(4, product.getProductID());
            update.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        // 查询products表
        try {
            // 结果不为空
            return conn.selectCountQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void close() {
        conn.close();
    }

    public void setPoolFlush(PoolFlush<Product> poolFlush) {
        this.poolFlush = poolFlush;
    }
}
