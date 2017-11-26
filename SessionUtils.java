package test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by feiliu on 2017/11/25.
 */
public class SessionUtils {
    private static Map<String, SocketHandlerImpl> users = new HashMap();

    public static SocketHandlerImpl getUsers(String who) {
        return users.get(who);
    }

    public static void save(String who, SocketHandlerImpl socketHandler) {
        users.put(who, socketHandler);
    }

    public static Collection<SocketHandlerImpl> getUsers() {
        return users.values();
    }
}
