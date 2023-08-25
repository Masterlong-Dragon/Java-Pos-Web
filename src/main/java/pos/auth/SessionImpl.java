package pos.auth;

import pos.user.User;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

class SessionImpl implements Session {

    private static SessionImpl instance;

    private Map<UUID, Integer> sessions;
    private UUID rootSessionID;

    private SessionImpl() {
        sessions = new ConcurrentHashMap<>();
        // 创建一个内部使用的持久会话
        sessions.put(rootSessionID = UUID.fromString("00000000-0000-0000-0000-000000000000"), 0);
    }

    public static SessionImpl Instance() {
        return InstanceHolder.INSTANCE.getInstance();
    }

    private enum InstanceHolder {
        INSTANCE();
        private SessionImpl instance;

        InstanceHolder() {
            instance = new SessionImpl();
        }

        public SessionImpl getInstance() {
            return instance;
        }
    }

    // 创建会话
    @Override
    public UUID createSession(int userID) {
        UUID sessionID;
        do {
            sessionID = UUID.randomUUID();
        } while (sessions.containsKey(sessionID) || sessionID.equals(rootSessionID));
        sessions.put(sessionID, userID);
        return sessionID;
    }

    @Override
    public void removeSession(UUID sessionID) {
        sessions.remove(sessionID);
    }

    @Override
    public int getUserID(UUID sessionID) throws IllegalArgumentException {
        if (sessions.containsKey(sessionID))
            return sessions.get(sessionID);
        else throw new IllegalArgumentException("POS.Session not found");
    }

    @Override
    public boolean isSessionValid(UUID sessionID) {
        return sessions.containsKey(sessionID);
    }
}
