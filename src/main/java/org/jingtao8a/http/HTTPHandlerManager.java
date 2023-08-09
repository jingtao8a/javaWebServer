package org.jingtao8a.http;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HTTPHandlerManager {
    private static Map<UUID, HttpHandler> map = new HashMap<>();

    public static void remove(UUID uuid) {
        map.remove(uuid);
    }
    public static HttpHandler get(UUID uuid) {
        if (!map.containsKey(uuid)) {
            map.put(uuid, new HttpHandler());
        }
        return map.get(uuid);
    }
}
