package pos.config;

import pos.user.Role;

public interface ConfigManager {
    String getDBURL();
    String getDBUser(Role role);
    String getDBPassword(Role role);
    String getDBDriver();
}
