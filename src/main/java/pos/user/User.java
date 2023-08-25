package pos.user;

import pos.auth.Permission;
import pos.auth.PermissionImpl;

import java.util.UUID;

public class User {

    private Role role;
    private int ID;
    private UUID sessionID;
    private String name;

    private Permission permission;

    public User(int ID, String name, Role role, UUID sessionID) {
        this.ID = ID;
        this.name = name;
        this.role = role;
        this.sessionID = sessionID;
        permission = PermissionImpl.Instance();
    }

    public void logout() {
        permission.logout(this);
    }

    public Role getRole() {
        return role;
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public UUID getSessionID() {
        return sessionID;
    }
}
