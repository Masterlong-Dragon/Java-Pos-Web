package pos.auth;

import pos.user.User;

import java.util.UUID;

public interface Session {
    UUID createSession(int userID);
    void removeSession(UUID sessionID);
    int getUserID(UUID sessionID);
    boolean isSessionValid(UUID sessionID);
}
