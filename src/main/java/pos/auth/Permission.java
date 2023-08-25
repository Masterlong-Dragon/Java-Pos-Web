package pos.auth;

import pos.user.Role;
import pos.user.User;

import java.util.UUID;

public interface Permission {
    User login(String name, String password);
    void logout(User user);
    boolean isSessionValid(UUID sessionID);
    boolean checkPermission(User user, Role ...roles);
}
