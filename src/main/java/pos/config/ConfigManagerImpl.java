package pos.config;

import pos.user.Role;

public class ConfigManagerImpl implements ConfigManager{

    private static ConfigManagerImpl instance;

    private ConfigManagerImpl() {
    }

    public static ConfigManagerImpl Instance() {
        return ConfigManagerImpl.InstanceHolder.INSTANCE.getInstance();
    }

    private enum InstanceHolder {
        INSTANCE();
        private ConfigManagerImpl instance;

        InstanceHolder() {
            instance = new ConfigManagerImpl();
        }

        public ConfigManagerImpl getInstance() {
            return instance;
        }
    }


    @Override
    public String getDBURL() {
        return "jdbc:mysql://localhost:3306/?user=root";
    }

    @Override
    public String getDBUser(Role role) {
        return "root";
    }

    @Override
    public String getDBPassword(Role role) {
        return "root";
    }

    @Override
    public String getDBDriver() {
        return "com.mysql.cj.jdbc.Driver";
    }
}
