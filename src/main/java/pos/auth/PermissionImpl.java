package pos.auth;

import pos.config.ConfigManager;
import pos.config.ConfigManagerImpl;
import pos.db.DBConnection;
import pos.user.Role;
import pos.user.User;

import java.sql.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PermissionImpl implements Permission {

    private static PermissionImpl instance;

    private Session session;
    private ConfigManager configManager;

    private PermissionImpl() {
        session = SessionImpl.Instance();
        configManager = ConfigManagerImpl.Instance();
    }

    public static PermissionImpl Instance() {
        return InstanceHolder.INSTANCE.getInstance();
    }

    private enum InstanceHolder {
        INSTANCE();
        private PermissionImpl instance;

        InstanceHolder() {
            instance = new PermissionImpl();
        }

        public PermissionImpl getInstance() {
            return instance;
        }
    }

    @Override
    public User login(String name, String password) throws IllegalArgumentException {
        // 数据库登录 管理员模式
        User user = null;

        DBConnection conn = new DBConnection(configManager.getDBURL(), configManager.getDBUser(Role.Admin), configManager.getDBPassword(Role.Admin));
        System.out.println("Connection established");
        try {            // 账号验证
            // 使用prepared statement
            PreparedStatement ps = conn.createPreparedStatement("select * from pos.useraccounts where Username = ? and Password = ?;");
            ps.setString(1, name);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            // 检查是否有结果
            if (!rs.next()) {
                System.out.println("Login failed");
                throw new IllegalArgumentException("Login failed");
            }
            // 有结果
            // 获取用户信息
            int userID = rs.getInt(1);
            String role = rs.getString(4);
            // 根据角色创建用户
            // 数据库确认完成 创建会话
            UUID sessionID = session.createSession(userID);
            user = new User(userID, name, Role.valueOf(role), sessionID);
            // 关闭数据库连接
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public void logout(User user) {
        session.removeSession(user.getSessionID());
        System.out.println(user.getName() + " logout successfully");
    }

    @Override
    public boolean isSessionValid(UUID sessionID) {
        return session.isSessionValid(sessionID);
    }

    @Override
    public boolean checkPermission(User user, Role... roles) {
        boolean roleMatch = false;
        for (Role role : roles) {
            if (roleMatch = user.getRole().equals(role))
                break;
        }
        return roleMatch && isSessionValid(user.getSessionID());
    }
}
