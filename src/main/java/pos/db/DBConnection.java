package pos.db;

import pos.config.ConfigManager;
import pos.config.ConfigManagerImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnection {

    private Connection conn;
    private ConfigManager configManager;
    private String tableName;
    private String IDName;
    private String labelName;
    private PreparedStatement selectById;
    private PreparedStatement selectByName;
    private PreparedStatement selectRange;
    private PreparedStatement selectAlike;
    private PreparedStatement count;
    private boolean autoCommit;

    public DBConnection(String url, String user, String password) {
        configManager = ConfigManagerImpl.Instance();
        // 1. 注册驱动
        try {
            Class.forName(configManager.getDBDriver());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // 获取数据库的连接
        try {
            conn = java.sql.DriverManager.getConnection(url, user, password);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        autoCommit = true;
    }

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public Connection getConnection() {
        return conn;
    }

    public PreparedStatement createPreparedStatement(String statement) {
        try {
            return conn.prepareStatement(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet selectIDQuery(int ID) {
        try {
            selectById.setInt(1, ID);
            // 执行查询
            selectById.execute();
            // 获取结果集
            if(!autoCommit)
                conn.commit();
            return selectById.getResultSet();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet selectStringQuery(String name) {
        try {
            selectByName.setString(1, name);
            // 执行查询
            selectByName.execute();
            // 获取结果集
            if(!autoCommit)
                conn.commit();
            return selectByName.getResultSet();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet selectStringAlikeQuery(String name) {
        try {
            selectAlike.setString(1, "%" + name + "%");
            // 执行查询
            selectAlike.execute();
            // 获取结果集
            if(!autoCommit)
                conn.commit();
            return selectAlike.getResultSet();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet selectRangeQuery(int limit, int offset) {
        try {
            selectRange.setInt(1, limit);
            selectRange.setInt(2, offset);
            // 执行查询
            selectRange.execute();
            // 获取结果集
            if(!autoCommit)
                conn.commit();
            return selectRange.getResultSet();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int selectCountQuery() throws IllegalArgumentException {
        try {
            // 执行查询
            count.execute();
            if(!autoCommit)
                conn.commit();
            // 获取结果集
            ResultSet rs = count.getResultSet();
            if (!rs.next())
                throw new IllegalArgumentException("Table corrupted.");
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) throws IllegalArgumentException {
        if (tableName != null && !tableName.equals("") && !tableName.equals(this.tableName)) {
            selectRange = createPreparedStatement("SELECT * FROM pos." + tableName + " LIMIT ? OFFSET ?");
            count = createPreparedStatement("SELECT COUNT(*) FROM pos." + tableName);
        } else {
            throw new IllegalArgumentException("Table name is null or empty or same as before.");
        }
        this.tableName = tableName;
    }

    public String getIDName() {
        return IDName;
    }

    public void setIDName(String IDName) throws IllegalArgumentException {
        if (tableName != null && IDName != null && !tableName.equals("") && !IDName.equals("") && !IDName.equals(this.IDName))
            selectById = createPreparedStatement("SELECT * FROM pos." + tableName + " WHERE " + IDName + " = ?");
        else
            throw new IllegalArgumentException("Table name or ID name is null or empty or same as before.");
        this.IDName = IDName;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        if (tableName != null && labelName != null && !tableName.equals("") && !labelName.equals("") && !labelName.equals(this.labelName)) {
            selectByName = createPreparedStatement("SELECT * FROM pos." + tableName + " WHERE " + labelName + " = ?");
            selectAlike = createPreparedStatement("SELECT * FROM pos." + tableName + " WHERE " + labelName + " LIKE ?");
        } else
            throw new IllegalArgumentException("Table name or label name is null or empty or same as before.");
        this.labelName = labelName;
    }
}
